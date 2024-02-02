package cg.project.tmptool.security;

import cg.project.tmptool.dto.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cg.project.tmptool.security.SecurityConfigConsts.EXPIRATION_TIME;
import static cg.project.tmptool.security.SecurityConfigConsts.SECRETE_KEY;

@Component
public class JwtTokenProvider {

    // 1 - Generate the token
    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());

        Date expireDate = new Date(now.getTime() + EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String, Object> claims = new HashMap<>();

        claims.put("id", (Long.toString(user.getId())));
        claims.put("username", user.getUsername());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SECRETE_KEY)
                .compact();
    }

    // 2 - Validate the token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRETE_KEY).parseClaimsJws(token);
            // 使用Jwts.parser().setSigningKey(SECRETE_KEY)配置JWT解析器，其中SECRETE_KEY是用于验证签名的密钥。
            // .parseClaimsJws(token)尝试解析和验证传入的token, 如果token有效（即没有抛出任何异常），方法返回true。
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid JTW Signature");
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT Token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT Token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT Token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty");
        }

        return false;
    }

    // 3 - Get user id from token
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRETE_KEY).parseClaimsJws(token).getBody();
        // 通过.parseClaimsJws(token).getBody()获取JWT的负载（Claims）
        String id = (String) claims.get("id");

        return Long.parseLong(id);
    }
}