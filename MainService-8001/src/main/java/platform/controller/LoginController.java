package platform.controller;

import entity.LoginInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import constants.Result;
import constants.ResultCode;
import platform.service.LoginService;
import org.springframework.web.bind.annotation.*;
import platform.util.RSAUtil;

import java.util.Map;

@Slf4j
@RequestMapping(value = "/mp/internet/wechat")
@RestController
public class LoginController {

    @Resource
    LoginService loginService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginInfo loginInfo, HttpServletRequest request) {
        int status = 0;
        try {
            status = loginService.loginStatus(loginInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Result(ResultCode.FAILURE, e.getMessage());
        }
        if (status == 0) {
            Map<String, Object> userMap = null;
            try {
                userMap = loginService.login(loginInfo, request);
            } catch (Exception e) {
                log.error(e.getMessage());
                return new Result(ResultCode.FAILURE, e.getMessage());
            }
            HttpSession session = request.getSession();
            session.setAttribute("uid", userMap.get("uid"));

            loginService.saveLogin(userMap, request);
            return new Result(ResultCode.SUCCESS, "Login succeed", userMap);
        }
        return switch (status) {
            case 1 -> new Result(ResultCode.FAILURE, "Login failed, user doesn't exist");
            case 2 -> new Result(ResultCode.FAILURE, "Login failed, password is incorrect, please try again.");
            case 3 -> new Result(ResultCode.FAILURE, "Login failed, CAPTCHA is incorrect, please try again.");
            default -> new Result(ResultCode.FAILURE, "Login failed, phone code is wrong, please try again.");
        };
    }

    @GetMapping("/getPublicKey")
    public Result getPublicKey() {
        try {
            return new Result(ResultCode.SUCCESS, "Success", RSAUtil.getPublicKey().getEncoded());
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Result(ResultCode.FAILURE, "Failed to obtain public key");
        }
    }

    @GetMapping("/getPicCode")
    public Result getPicCode() {
        try {
            Map<String, Object> verifyCode = loginService.generatePicCode();
            return new Result(ResultCode.SUCCESS, "Successfully obtained the picture code.", verifyCode);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Result(ResultCode.FAILURE, "Failed to obtain the picture code.");
        }
    }

    @PostMapping("/verifyPicCode")
    public Result verifyPicCode(@RequestBody Map<String, String> picCodeInfo) {
        if (loginService.verifyCode(picCodeInfo)) {
            return new Result(ResultCode.SUCCESS, "Verify Code Successful");
        }
        return new Result(ResultCode.FAILURE, "Verify Code Failed");
    }

    @PostMapping("/sendCode")
    public Result sendCode(@RequestBody Map<String, String> payload) {
        try {
            int code = loginService.generateCode(payload);
            return new Result(ResultCode.SUCCESS, "success", code);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Result(ResultCode.FAILURE, "Failed to send the text code");
        }
    }

    @PostMapping("/passwordChange")
    public Result changePassword(@RequestBody Map<String, String> payload) {
        try {
            int status = loginService.changePassword(payload);
            return switch (status){
                case 0 -> new Result(ResultCode.SUCCESS, "Password changed successfully");
                case 1 -> new Result(ResultCode.FAILURE, "New password must contain at least 1 number, 1 alphabet, 1 special character, length between 8 and 20");
                case 2 -> new Result(ResultCode.FAILURE, "The new password can't contain 4 consecutive numbers of the user's phone number");
                case 3 -> new Result(ResultCode.FAILURE, "The new password cannot be the same as the last 5 passwords");
                default -> new Result(ResultCode.FAILURE, "Password isn't correct");
            };
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Result(ResultCode.FAILURE, e.getMessage());
        }
    }
}
