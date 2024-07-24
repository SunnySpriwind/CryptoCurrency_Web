package com.spriwind.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

/**
 * @author Sunny Spriwind
 * @since
 */
public class JWT {
    private static String signkey = "Spriwind";
    private static Long expiration =  86400000L;

    /**
     * To generate JWT. Expiration: 1d; Sign key:''
     * @param claims the second section of JWT.
     * @return JWT
     */
    public static String generateJWT(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signkey)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
        return jwt;
    }

    /**
     * To parse JWT.
     * @param jwt
     * @return the true value of the second section of JWT.
     */
    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(signkey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }

}
