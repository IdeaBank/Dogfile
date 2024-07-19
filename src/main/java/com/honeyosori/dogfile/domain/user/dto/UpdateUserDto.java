package com.honeyosori.dogfile.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.honeyosori.dogfile.global.constant.Role;

public record UpdateUserDto(@JsonIgnore String password,
                            @JsonIgnore Role role) {
}
