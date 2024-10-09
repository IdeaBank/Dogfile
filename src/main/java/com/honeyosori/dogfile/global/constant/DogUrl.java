package com.honeyosori.dogfile.global.constant;

public final class DogUrl {
    public static final String DOGUS = "http://dogus.dogus.svc.cluster.local:8080";
    public static final String DOGCHAT = "http://dogchat.dogchat.svc.cluster.local:8080";

    public static final String DOGFILE_OAUTH = "/dogfile/v1/oauth/kakao/oauth";
    public static final String DOGUS_REGISTER = "/api/v1/user/register";
    public static final String DOGCHAT_REGISTER = "/api/v1/chat/user?userId=%s&userName=%s";
    public static final String DOGCHAT_WITHDRAW = "/api/v1/chat/user?userId=%s";
}
