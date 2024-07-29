package com.honeyosori.dogfile.global.auth;

import com.honeyosori.dogfile.global.constant.NonSecureMethod;
import com.honeyosori.dogfile.global.response.BaseResponse;
import com.honeyosori.dogfile.global.response.BaseResponseStatus;
import com.honeyosori.dogfile.global.utility.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter -> 한 번 실행 보장

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtility jwtUtility;

    @Override
    /**
     * JWT 토큰 검증 필터 수행
     */
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtility.resolveToken(request);
        BaseResponseStatus jwtStatus = jwtUtility.validateToken(token);

        if (jwtStatus == BaseResponseStatus.SUCCESS) {
            String username = jwtUtility.getUserInfoFromToken(token).get("username").toString();
            String password = jwtUtility.getUserInfoFromToken(token).get("password").toString();
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (!userDetails.getPassword().equals(password)) {
                BaseResponse<?> baseResponse = new BaseResponse<>(BaseResponseStatus.EXPIRED_JWT_TOKEN, null);

                response.setStatus(baseResponse.getCode());
                response.setContentType("application/json");
                response.getWriter().write(baseResponse.toString());

                return;
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } else {
            BaseResponse<?> baseResponse = new BaseResponse<>(jwtStatus, null);

            response.setStatus(baseResponse.getCode());
            response.setContentType("application/json");
            response.getWriter().write(baseResponse.toString());
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        for (NonSecureMethod nonSecureMethod : NonSecureMethod.values()) {
            if (new AntPathMatcher().match(nonSecureMethod.getUrl(), request.getServletPath())) {
                return true;
            }
        }

        return false;
    }
}