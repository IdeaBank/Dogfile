package com.honeyosori.dogfile.global.response;

import org.springframework.http.HttpStatus;

public enum BaseResponseStatus {
    /**
     * 2XX
     */
    SUCCESS(true, HttpStatus.OK.value(), "Request succeeded"),
    UPDATED(true, HttpStatus.OK.value(), "Update succeeded"),
    CREATED(true, HttpStatus.CREATED.value(), "Create succeeded"),
    UPDATED_WITHOUT_CONTENT(true, HttpStatus.NO_CONTENT.value(), "Update succeeded"),
    DELETED(true, HttpStatus.NO_CONTENT.value(), "Delete succeeded"),

    /**
     * 4XX
     */
    REJECTED(false, HttpStatus.NOT_ACCEPTABLE.value(), "REJECTED"),
    FORBIDDEN(false, HttpStatus.FORBIDDEN.value(), "Not authenticated"),
    // Access without token

    UNAUTHENTICATED(false, HttpStatus.UNAUTHORIZED.value(), "Unauthorized access"),
    // Can't access to specific method

    USERNAME_EXISTS(false, HttpStatus.CONFLICT.value(), "Same username already exists"),
    BADGE_EXIST(false, HttpStatus.CONFLICT.value(), "Badge already exists"),
    BREED_EXIST(false, HttpStatus.CONFLICT.value(), "Breed already exists"),

    WITHDRAW_REQUESTED(false, HttpStatus.CONFLICT.value(), "Withdraw requested"),
    WITHDRAWN(false, HttpStatus.BAD_REQUEST.value(), "User has withdrawn"),

    WRONG_PASSWORD(false, HttpStatus.UNAUTHORIZED.value(), "Invalid password"),

    USER_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "User not found"),
    BREED_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "Breed not found"),
    BADGE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "Badge not found"),

    NOT_FOLLOWING(false, HttpStatus.CONFLICT.value(), "Not following"),
    NOT_BLOCKING(false, HttpStatus.CONFLICT.value(), "Not blocking"),

    ALREADY_OWN_BADGE(false, HttpStatus.CONFLICT.value(), "Badge already owned by user"),
    ALREADY_WAITING_FOR_WITHDRAW(false, HttpStatus.CONFLICT.value(), "Already waiting for withdraw"),

    EXPIRED_JWT_TOKEN(false, HttpStatus.UNAUTHORIZED.value(), "Expired token"),
    INVALID_JWT_TOKEN(false, HttpStatus.UNAUTHORIZED.value(), "Invalid token"),

    NO_JWT_TOKEN(false, HttpStatus.NOT_ACCEPTABLE.value(), "No token provided"),

    /**
     * 5xx
     */
    DATABASE_INSERT_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 저장에 실패하였습니다."),
    DATABASE_DELETE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 삭제에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 에러가 발생하였습니다."),
    ;

    private final Boolean result;
    private final Integer status;
    private final String message;

    public Boolean getResult() {
        return result;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    BaseResponseStatus(Boolean result, Integer status, String message) {
        this.result = result;
        this.status = status;
        this.message = message;
    }
}