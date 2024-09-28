package platform.service;

import entity.LastLogin;
import entity.LoginInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface LoginService {

    int loginStatus(LoginInfo loginInfo) throws Exception;

    Map<String, Object> generatePicCode() throws Exception;

    int generateCode(Map<String, String> payload) throws Exception;

    boolean verifyCode(Map<String, String> picCodeInfo);

    LastLogin getLastLogin(String phone);

    Map<String, Object> login(LoginInfo loginInfo, HttpServletRequest request) throws Exception;

    void saveLogin(Map<String, Object> userMap, HttpServletRequest request);

    int changePassword(Map<String, String> payload) throws Exception;
}
