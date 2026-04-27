package org.example.vo_ai_service.comm;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtUtil {

    // 简单密钥，实际生产环境建议放配置文件
    private static final String SECRET = "voice-note-ai-secret-key-1234567890";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRE = 86400000; // 24小时

    public static String generateToken(Long userId, String username, String type) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", type) // user 或 admin
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY)
                .compact();
    }

    public static Long getUserId(String token) {
        try {
            String sub = Jwts.parser().verifyWith(KEY).build()
                    .parseSignedClaims(token).getPayload().getSubject();
            return Long.valueOf(sub);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getType(String token) {
        try {
            return Jwts.parser().verifyWith(KEY).build()
                    .parseSignedClaims(token).getPayload().get("type", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
