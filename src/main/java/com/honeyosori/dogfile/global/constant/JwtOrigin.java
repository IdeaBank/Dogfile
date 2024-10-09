package com.honeyosori.dogfile.global.constant;

import lombok.Getter;

@Getter
public enum JwtOrigin {
    LOCAL("LOCAL"),
    KAKAO("KAKAO"),
    ;

    private final String name;

    JwtOrigin(String name) {
        this.name = name;
    }
}
