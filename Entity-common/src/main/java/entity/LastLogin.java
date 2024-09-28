package entity;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class LastLogin {
    private String phone;
    private String username;
    private String loginIp;
    private Timestamp loginTime;
}
