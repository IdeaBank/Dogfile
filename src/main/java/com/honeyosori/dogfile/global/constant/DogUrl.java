package com.honeyosori.dogfile.global.constant;

import lombok.Getter;

@Getter
public enum DogUrl {
    // TODO: url 변경해놓기
    DOGFILE("http://dogfile.dogfile.svc.cluster.local:3000"),
    DOGUS("http://dogus.210-178-40-108.nip.io"),
    DOGTING("http://dogting.dogting.svc.cluster.local:8080"),
    DOGCHAT("http://dogchat.210-178-40-108.nip.io"),
    PUSH("http://push.push.svc.cluster.local:8080"),
    DOGGATE("http://doggate.210-178-40-108.nip.io")
    ;

    private String url;

    private DogUrl(String url) {
        this.url = url;
    }
}
