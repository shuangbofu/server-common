package org.example.server.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.server.user.utils.UserUtils;

import java.util.Optional;

public interface FillUserNickname {

    String getCreateBy();

    String getUpdateBy();

    @JsonProperty("createUser")
    default String createUser() {
        return Optional.ofNullable(UserUtils.getNickname(getCreateBy()))
                .orElse(getCreateBy());
    }

    @JsonProperty("updateUser")
    default String updateUser() {
        return Optional.ofNullable(UserUtils.getNickname(getUpdateBy()))
                .orElse(getUpdateBy());
    }
}
