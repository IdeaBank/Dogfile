package com.honeyosori.dogfile.global.response.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public enum GeneralResponse {
    /**
     * 2XX
     */
    HEALTHZ(HttpStatus.OK.value(), "Dogfile v0.0.1 is healthy"),
    SUCCESS(HttpStatus.OK.value(), "Success"),
    FOUND(HttpStatus.FOUND.value(), "Found"),
    UPDATED(HttpStatus.OK.value(), "Update succeeded"),
    CREATED(HttpStatus.CREATED.value(), "Create succeeded"),
    UPDATED_WITHOUT_CONTENT(HttpStatus.NO_CONTENT.value(), "Update succeeded"),
    DELETED(HttpStatus.NO_CONTENT.value(), "Delete succeeded"),
    DATA_EMPTY(HttpStatus.NO_CONTENT.value(), "Data is empty"),

    /**
     * 4XX
     */
    REJECTED(HttpStatus.NOT_ACCEPTABLE.value(), "REJECTED"),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "Not authenticated"),
    // Access without token

    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access"),
    // Can't access to specific method

    EMAIL_EXISTS(HttpStatus.CONFLICT.value(), "Same email already exists"),
    BADGE_EXIST(HttpStatus.CONFLICT.value(), "Badge already exists"),
    BREED_EXIST(HttpStatus.CONFLICT.value(), "Breed already exists"),

    WITHDRAW_REQUESTED(HttpStatus.CONFLICT.value(), "Withdraw requested"),
    WITHDRAWN(HttpStatus.BAD_REQUEST.value(), "User has withdrawn"),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "Invalid password"),

    USER_EXISTS(HttpStatus.CONFLICT.value(), "User already exists"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "User not found"),
    BREED_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Breed not found"),
    BADGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Badge not found"),
    DOG_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Dog not found"),

    NOT_FOLLOWING(HttpStatus.CONFLICT.value(), "Not following"),
    NOT_BLOCKING(HttpStatus.CONFLICT.value(), "Not blocking"),

    ALREADY_OWN_BADGE(HttpStatus.CONFLICT.value(), "Badge already owned by user"),
    ALREADY_WAITING_FOR_WITHDRAW(HttpStatus.CONFLICT.value(), "Already waiting for withdraw"),

    DATA_TOO_BIG(HttpStatus.PAYLOAD_TOO_LARGE.value(), "File is too big"),

    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Expired token"),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Invalid token"),

    NO_JWT_TOKEN(HttpStatus.NOT_ACCEPTABLE.value(), "No token provided"),

    /**
     * 5xx
     */
    DATABASE_INSERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다."),
    DATABASE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 삭제에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 에러가 발생하였습니다.");

    private final int code;
    private final String message;

    GeneralResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
