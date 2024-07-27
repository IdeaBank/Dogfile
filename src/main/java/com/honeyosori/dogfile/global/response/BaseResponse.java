package com.honeyosori.dogfile.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@JsonPropertyOrder({"result", "code", "message", "data"})
public class BaseResponse<T> {
    @JsonProperty("result")
    private boolean result;
    @Getter
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public BaseResponse(BaseResponseStatus status, T data) {
        this.result = status.getResult();
        this.code = status.getStatus();
        this.message = status.getMessage();
        this.data = data;
    }

    public static ResponseEntity<?> getResponseEntity(BaseResponse<?> baseResponse) {
        return new ResponseEntity<>(baseResponse, HttpStatusCode.valueOf(baseResponse.code));
    }
}
