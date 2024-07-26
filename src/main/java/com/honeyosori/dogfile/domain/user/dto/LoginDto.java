package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginDto(String username,
                       @JsonIgnore String password) {
}
