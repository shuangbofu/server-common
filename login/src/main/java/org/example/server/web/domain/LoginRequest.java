package org.example.server.web.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest implements Serializable {
    private String username;
    private String password;
    private String mobile;
    private String code;
}
