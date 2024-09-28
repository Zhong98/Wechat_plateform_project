package task.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

public class JWTUtil {

    //private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final String SECRET_KEY_STRING = "f7dK8ZQAnb57HS4Y8DsN3GTHrM4HxLp4j7F6KZpTV68=";
    private static final SecretKey SECRET_KEY;

    static {
        // 将字符串转换为 SecretKey 对象
        SECRET_KEY = new SecretKeySpec(SECRET_KEY_STRING.getBytes(), "HmacSHA256");
    }

    public static String generateToken(int uid, String loginIp, String username) {
        long expirationTime = 1000 * 60 * 60;

        // Generate the token
        return Jwts.builder()
                .subject(uid+"")
                .claim("loginIp", loginIp)
                .claim("username", username)
                .issuedAt(new Date())
                //.expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
