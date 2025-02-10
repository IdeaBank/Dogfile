package com.honeyosori.dogfile.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyosori.dogfile.domain.oauth.component.KakaoOAuthComponent;
import com.honeyosori.dogfile.domain.oauth.constant.KakaoUrl;
import com.honeyosori.dogfile.domain.oauth.constant.TokenType;
import com.honeyosori.dogfile.domain.oauth.dto.CreateKakaoAccountDto;
import com.honeyosori.dogfile.domain.oauth.dto.KakaoTokenResponse;
import com.honeyosori.dogfile.domain.oauth.dto.KakaoUserInformation;
import com.honeyosori.dogfile.domain.oauth.exception.OAuthException;
import com.honeyosori.dogfile.domain.user.dto.CreateDogclubUserDto;
import com.honeyosori.dogfile.domain.user.dto.CreateDogusUserDto;
import com.honeyosori.dogfile.domain.user.entity.User;
import com.honeyosori.dogfile.domain.user.repository.UserRepository;
import com.honeyosori.dogfile.global.constant.*;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import javax.swing.text.DateFormatter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KakaoOAuthService {
    private final UserRepository userRepository;
    private final KakaoOAuthComponent kakaoOAuthComponent;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtility jwtUtility;

    public KakaoOAuthService(UserRepository userRepository, KakaoOAuthComponent kakaoOAuthComponent, JwtUtility jwtUtility) {
        this.userRepository = userRepository;
        this.kakaoOAuthComponent = kakaoOAuthComponent;
        this.jwtUtility = jwtUtility;
    }

    @Transactional
    public ResponseEntity<?> registerUser(CreateKakaoAccountDto createKakaoAccountDto) {
        String email = getEmailUsingAccessToken(createKakaoAccountDto.kakaoAccessToken());
        Date birthday = createKakaoAccountDto.birthday();
        String phoneNumber = createKakaoAccountDto.phoneNumber();
        User.GenderType genderType = createKakaoAccountDto.gender();
        String realName = createKakaoAccountDto.realName();
        String accountName = createKakaoAccountDto.accountName();

        this.userRepository.findUserByEmail(email).ifPresent((u) -> {
            throw new OAuthException(BaseResponseStatus.USER_EXISTS);
        });

        User user = new User(email, birthday, phoneNumber, genderType, realName, accountName);

        this.userRepository.save(user);

        return BaseResponse.getResponseEntity(BaseResponseStatus.CREATED);
    }

    public String getEmailUsingAccessToken(String accessToken) {
        RestClient restClient = RestClient.builder().baseUrl(kakaoOAuthComponent.API_URI).build();

        System.out.println("getting access token using " + accessToken);

        KakaoUserInformation kakaoUserInformation = restClient.get()
                .uri(KakaoUrl.GET_USER_INFORMATION)
                .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new OAuthException(BaseResponseStatus.REJECTED);
                })
                .body(KakaoUserInformation.class);

        if (kakaoUserInformation == null || kakaoUserInformation.getKakaoAccount() == null) {
            throw new OAuthException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        if (kakaoUserInformation.getKakaoAccount().getEmail() == null || kakaoUserInformation.getKakaoAccount().getEmail().isEmpty()) {
            throw new OAuthException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        return kakaoUserInformation.getKakaoAccount().getEmail();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> loginWithKakao(String accessToken) {
        accessToken = accessToken.replace("Bearer ", "");

        return readUserInformation(accessToken);
    }

    private ResponseEntity<?> readUserInformation(String kakaoAccessToken) {
        String email = getEmailUsingAccessToken(kakaoAccessToken);

        // User 없으면 USER_NOT_FOUND 반환
        this.userRepository.findUserByEmail(email).orElseThrow(()
                -> new OAuthException(BaseResponseStatus.USER_NOT_FOUND));

        Map<String, String> claims = new HashMap<>();
        claims.put(PayloadData.EMAIL, email);

        return jwtUtility.generateJwtResponse(claims);
    }

    private void sendDeleteRequestToDogus(String dogfileUserId) {
        RestClient restClient = RestClient.builder()
                .baseUrl(DogUrl.DOGUS)
                .build();

        log.info("[DOGUS] Sending {}", dogfileUserId);

        String result = restClient.delete()
                .uri(DogUrl.DOGUS_DELETE + dogfileUserId)
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, (r, e) -> {
                    log.error("{} {}", e.getStatusCode(), e.getBody());
                    throw new OAuthException(BaseResponseStatus.INVALID_JWT_TOKEN);
                })
                .body(String.class);

        log.info("[DOGUS] Response {}", result);
    }

    private void sendRegisterRequestToDogus(CreateDogusUserDto createDogusUserDto) {
        RestClient restClient = RestClient.builder().baseUrl(DogUrl.DOGUS).build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String dogusAccountInformation = objectMapper.writeValueAsString(createDogusUserDto);

            log.info("[DOGUS] Sending {}", dogusAccountInformation);

            String result = restClient.post()
                    .uri(DogUrl.DOGUS_REGISTER)
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(dogusAccountInformation)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (r, e) -> {
                        log.error("{} {}", e.getStatusCode(), e.getBody());
                        throw new OAuthException(BaseResponseStatus.INVALID_JWT_TOKEN);
                    })
                    .body(String.class);

            log.info("[DOGCLUB] Response {}", result);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendRegisterRequestToDogclub(CreateDogclubUserDto createDogclubUserDto) {
        RestClient restClient = RestClient.builder().baseUrl(DogUrl.DOGCLUB).build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String dogclubAccountInformation = objectMapper.writeValueAsString(createDogclubUserDto);

            log.info("[DOGCLUB] Sending {}", dogclubAccountInformation);

            String result = restClient.post()
                    .uri(DogUrl.DOGCLUB_REGISTER)
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(dogclubAccountInformation)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (r, e) -> {
                        log.error("{} {}", e.getStatusCode(), e.getBody());
                        throw new OAuthException(BaseResponseStatus.INVALID_JWT_TOKEN);
                    })
                    .body(String.class);

            log.info("[DOGCLUB] Response {}", result);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
