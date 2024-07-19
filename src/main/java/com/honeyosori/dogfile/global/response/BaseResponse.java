package com.honeyosori.dogfile.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonPropertyOrder({"result", "code", "message", "data"})
public class BaseResponse<T> {
    @JsonProperty("result")
    private boolean result;
    private final Integer status;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public BaseResponse(BaseResponseStatus status, T data) {
        this.result = BaseResponseStatus.SUCCESS.getResult();
        this.status = BaseResponseStatus.SUCCESS.getStatus();
        this.message = BaseResponseStatus.SUCCESS.getMessage();
        this.data = data;
    }
}
