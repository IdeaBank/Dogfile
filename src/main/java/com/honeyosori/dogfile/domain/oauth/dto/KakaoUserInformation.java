package com.honeyosori.dogfile.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class KakaoUserInformation {
   public static class KakaoAccount {
        @Getter
        private String email;
    }

    private Long id;
    private KakaoAccount kakaoAccount;
}
