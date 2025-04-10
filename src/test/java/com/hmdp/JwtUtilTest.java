package com.hmdp;

import com.hmdp.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;

public class JwtUtilTest {

    // 密钥
    SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    String SECRET_KEY = Encoders.BASE64.encode(key.getEncoded());

    private Map<String,Object> sampleClaims;

    @Before
    public void setUp() {
        sampleClaims = new HashMap<>();
        sampleClaims.put("userId", 1001);
        sampleClaims.put("username", "testUser");
    }

    // 正常场景测试
    @Test
    public void testCreateJWT() {
        String token = JwtUtil.createJWT(SECRET_KEY, 3600000L, sampleClaims);

        Claims claims = JwtUtil.parseJWT(SECRET_KEY, token);
        System.out.println(claims);
        // 断言验证
        Assertions.assertEquals(1001, claims.get("userId"));
        Assertions.assertEquals("testUser", claims.get("username"));
        Assertions.assertNotNull(claims.getExpiration());
    }

    // 异常场景：过期Token测试
    @Test
    public void should_throw_expired_exception() throws InterruptedException {
        // 生成1毫秒过期的Token
        String token = JwtUtil.createJWT(SECRET_KEY, 1L, sampleClaims);
        Thread.sleep(2); // 确保过期

        Assertions.assertThrows(ExpiredJwtException.class,
                () -> JwtUtil.parseJWT(SECRET_KEY, token));
    }

    // 异常场景：签名密钥错误测试
    @Test
    public void should_throw_signature_exception() {
        String token = JwtUtil.createJWT(SECRET_KEY, 3600000L, sampleClaims);

        // 生成一个符合长度但内容错误的密钥（例如随机生成）
        String wrongKey = Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());

        Assertions.assertThrows(SignatureException.class,
                () -> JwtUtil.parseJWT(wrongKey, token));
    }

    // 边界测试：空claims处理
    @Test
    public void should_handle_empty_claims() {
        Map<String, Object> emptyClaims = new HashMap<>();
        String token = JwtUtil.createJWT(SECRET_KEY, 3600000L, emptyClaims);

        Claims claims = JwtUtil.parseJWT(SECRET_KEY, token);
        // 验证用户自定义声明为空
        Assertions.assertFalse(claims.containsKey("userId"));
        Assertions.assertFalse(claims.containsKey("username"));
        // 确保至少存在标准声明（如 exp）
        Assertions.assertTrue(claims.containsKey("exp"));
//        Map<String, Object> emptyClaims = new HashMap<>();
//        String token = JwtUtil.createJWT(SECRET_KEY, 3600000L, emptyClaims);
//
//        Claims claims = JwtUtil.parseJWT(SECRET_KEY, token);
//        Assertions.assertTrue(claims.isEmpty());
    }

}

