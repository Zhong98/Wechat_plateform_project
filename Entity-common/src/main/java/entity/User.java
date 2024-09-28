package entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    private int uid;
    private String phone;
    private String password;
    private String username;
    private int status;
}
