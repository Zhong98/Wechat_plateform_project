package login;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.PostMapping;
import platform.MainApplication;
import platform.mapper.LoginMapper;
import platform.util.JWTUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest(classes = MainApplication.class)
public class LoginApplicationTests {

    @Resource
    LoginMapper loginMapper;

    @Test
    @PostMapping("/login")
    public void login() {

    }

    @Test
    public void getToken() {
        String token1 = JWTUtil.generateToken(1, "127.0.0.1", "zzx");
        System.out.println(token1);
        System.out.println(JWTUtil.parseToken(token1).getSubject());
        System.out.println(JWTUtil.parseToken(token1).get("username"));
    }

    @Test
    public void regex() {
        String phone = "13198534102";
        String password = "131a9853bv4102";
        String regex = "(?=(\\d{4}))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(phone);
        Matcher matcher2 = pattern.matcher(password);

        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        while (matcher1.find()) {
            list1.add(matcher1.group(1));
        }
        while (matcher2.find()) {
            list2.add(matcher2.group(1));
        }
        for (String s : list2) {
            if (list1.contains(s)) {
                System.out.println(s);
            }
        }
    }
}
