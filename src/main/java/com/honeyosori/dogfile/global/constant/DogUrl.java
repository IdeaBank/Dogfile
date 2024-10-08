package com.honeyosori.dogfile.global.constant;

import lombok.Getter;

@Getter
public enum DogUrl {
    DOGUS("http://dogus.dogus.svc.cluster.local:8080"),
    DOGCHAT("http://dogchat.dogchat.svc.cluster.local:8080"),
    ;

    private final String url;

    DogUrl(String url) {
        this.url = url;
    }
}
