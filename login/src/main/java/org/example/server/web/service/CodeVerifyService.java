package org.example.server.web.service;

public interface CodeVerifyService {

    void sendCode(String mobile, String key);

    void verifyCode(String key, String mobile, String verifyCode);
}
