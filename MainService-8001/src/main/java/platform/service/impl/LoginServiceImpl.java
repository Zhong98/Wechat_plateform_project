package platform.service.impl;

import entity.LastLogin;
import entity.LoginInfo;
import entity.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import platform.mapper.LoginMapper;
import platform.service.KaptchaService;
import platform.service.LoginService;
import org.springframework.stereotype.Service;
import platform.util.JWTUtil;
import platform.util.RSAUtil;
import platform.util.RedisUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    LoginMapper loginMapper;

    @Resource
    KaptchaService kaptchaService;

    @Resource
    RedisUtil redisUtil;

    @Override
    public int loginStatus(LoginInfo loginInfo) throws Exception {
        //check if user exists
        String timestamp = loginInfo.getTimestamp();
        String phoneNumber = RSAUtil.decrypt(loginInfo.getPhone(), RSAUtil.getPrivateKey());

        User user = loginMapper.findUserByPhone(loginInfo.getPhone());

        if (user == null) return 1;

        //check password
        if (!user.getPassword().equals(loginInfo.getPassword())) return 2;

        //check CAPTCHA
        String picCode = redisUtil.get(timestamp);
        if (picCode == null || !picCode.equals(loginInfo.getPicCode())) return 3;

        //check phone code
        String code = redisUtil.get(phoneNumber);
        if (code == null || !code.equals(loginInfo.getCode())) return 4;

        redisUtil.delete(timestamp);
        redisUtil.delete(phoneNumber);
        return 0;
    }

    @Override
    public Map<String, Object> generatePicCode() throws Exception {
        Map<String, Object> verifyCode = kaptchaService.generateVerifyCode();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String textCode = (String) verifyCode.get("textCode");
        verifyCode.put("timestamp", timestamp);
        redisUtil.set(timestamp, textCode);
        redisUtil.expire(timestamp, 15, TimeUnit.MINUTES);
        return verifyCode;
    }

    @Override
    public int generateCode(Map<String, String> payload) throws Exception {
        String realPhone = RSAUtil.decrypt(payload.get("phone"), RSAUtil.getPrivateKey());
        int code = 100000 + new SecureRandom().nextInt(900000);
        redisUtil.set(realPhone, code + "");
        redisUtil.expire(realPhone, 120, TimeUnit.SECONDS);
        return code;
    }

    @Override
    public boolean verifyCode(Map<String, String> picCodeInfo) {
        String picCode = redisUtil.get(picCodeInfo.get("timestamp"));
        return picCode.equals(picCodeInfo.get("picCode"));
    }

    @Override
    public LastLogin getLastLogin(String phone) {
        return loginMapper.findLastLoginByPhone(phone);
    }

    @Override
    public Map<String, Object> login(LoginInfo loginInfo, HttpServletRequest request) throws Exception {
        User user = loginMapper.findUserByPhone(loginInfo.getPhone());
        LastLogin lastLogin = getLastLogin(user.getPhone());

        String token = JWTUtil.generateToken(user.getUid(), request.getRemoteAddr(), user.getUsername());
        redisUtil.set(user.getUid() + "", token);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", user.getUid());
        userMap.put("token", token);
        userMap.put("phone", user.getPhone());
        userMap.put("loginTime", lastLogin == null ? null : lastLogin.getLoginTime());
        userMap.put("status", user.getStatus());
        userMap.put("loginIp", lastLogin == null ? null : lastLogin.getLoginIp());
        userMap.put("username", user.getUsername());
        if (user.getStatus() == 0) {
            redisUtil.set(RSAUtil.decrypt(user.getPhone(), RSAUtil.getPrivateKey()),
                    RSAUtil.decrypt(user.getPassword(), RSAUtil.getPrivateKey())); //Decrypted password saved in Redis
        }
        return userMap;
    }

    @Override
    public void saveLogin(Map<String, Object> userMap, HttpServletRequest request) {
        Map<String, Object> loginMap = new HashMap<>();
        loginMap.put("phone", userMap.get("phone"));
        loginMap.put("username", userMap.get("username"));
        loginMap.put("loginIp", request.getRemoteAddr());
        loginMapper.saveLogin(loginMap);
    }

    @Override
    public int changePassword(Map<String, String> payload) throws Exception {
        String phoneEncrypted = payload.get("phone");
        String passwordEncrypted = payload.get("password");
        String newPasswordEncrypted = payload.get("newPassword");

        String phone = RSAUtil.decrypt(phoneEncrypted, RSAUtil.getPrivateKey());
        String password = RSAUtil.decrypt(passwordEncrypted, RSAUtil.getPrivateKey());
        String currentPassword = redisUtil.get(phone);
        if (currentPassword == null) {
            currentPassword = RSAUtil.decrypt(loginMapper.findUserByPhone(phoneEncrypted).getPassword(), RSAUtil.getPrivateKey());
        }
        if (password.equals(currentPassword)) {
            String newPassword = RSAUtil.decrypt(newPasswordEncrypted, RSAUtil.getPrivateKey());

            //regex
            //At least 1 number, 1 alphabet, 1 special character, length between 8 and 20
            String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*\\W)\\S{8,20}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(newPassword);
            if (!matcher.matches()) return 1;

            //The new password can't contain 4 consecutive numbers of the user's phone number
            regex = "(?=(\\d{4}))";
            pattern = Pattern.compile(regex);
            Matcher matcher1 = pattern.matcher(phone);
            Matcher matcher2 = pattern.matcher(newPassword);
            List<String> list1 = new ArrayList<>();
            List<String> list2 = new ArrayList<>();
            while (matcher1.find()) {
                list1.add(matcher1.group(1));
            }
            while (matcher2.find()) {
                list2.add(matcher2.group(1));
            }
            for (String s : list2) {
                if (list1.contains(s)) return 2;
            }


            List<String> oldPasswordList = loginMapper.findOldPassword(phoneEncrypted);
            if (!oldPasswordList.isEmpty()){
                for (String oldPassword : oldPasswordList) {
                    if (newPasswordEncrypted.equals(oldPassword)) return 3;
                }
            }
            loginMapper.updatePassword(newPasswordEncrypted, phoneEncrypted);
            loginMapper.saveModification(phoneEncrypted, newPasswordEncrypted);
            redisUtil.delete(phone);
            return 0;
        }
        return 4;
    }
}
