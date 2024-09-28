package platform.service;


import java.util.Map;

public interface KaptchaService {
    Map<String, Object> generateVerifyCode() throws Exception;
}
