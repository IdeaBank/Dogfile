package com.honeyosori.dogfile.domain.klat.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KlatLoginDto {
    private String userId;
    private String password;
}
