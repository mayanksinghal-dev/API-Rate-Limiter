package com.example.rateLimit.utils;

import com.example.common.config.CommonConfig;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class JwtDecode {

    @Autowired
    private CommonConfig commonConfig;

    public PublicKey loadPublicKey() throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(commonConfig.publicKeyPath)));
        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public Jws<Claims> parse(String token) {
        try {
            PublicKey publicKey = loadPublicKey();
            Jws<Claims> payload = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
            System.out.println(payload.getPayload().get("tier"));
            return payload;
        } catch (IOException e) {
            throw new RuntimeException("Error loading public key", e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }
}
