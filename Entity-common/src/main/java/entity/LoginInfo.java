package entity;

import lombok.Data;

@Data
public class LoginInfo {
    private String phone;
    private String password;
    private String code;
    private String timestamp;
    private String picCode;
}
