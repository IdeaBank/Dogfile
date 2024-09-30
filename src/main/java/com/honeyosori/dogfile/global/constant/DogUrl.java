package com.honeyosori.dogfile.global.constant;

import lombok.Getter;

@Getter
public enum DogUrl {
    DOGFILE("http://dogfile.dogfile.svc.cluster.local:3000"),
    DOGUS("http://dogus.dogus.svc.cluster.local:8080"),
    DOGTING("http://dogting.dogting.svc.cluster.local:8080"),
    DOGCHAT("http://dogchat.dogchat.svc.cluster.local:8080"),
    PUSH("http://push.push.svc.cluster.local:8080"),
    ;

    private String url;

    private DogUrl(String url) {
        this.url = url;
    }
}
