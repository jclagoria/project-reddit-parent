package ar.com.reddit.security;

import ar.com.reddit.exceptions.SpringRedditException;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.time.Instant;

import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

@Service
public class JwtProvidder {

    private KeyStore keyStore;

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationMillis;

    @PostConstruct
    public void init() {

        try{
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        }catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception ocurred qhile loading keysore", e);
        }

    }

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationMillis)))
                .compact();
    }

    public String generateTokenWithuserNama(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant
                        .now().plusMillis(jwtExpirationMillis)))
                .compact();
    }

    public boolean validateToken(String jwt) {
        parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
        return Boolean.TRUE;
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = parser()
                .setSigningKey(getPublickey())
                .parseClaimsJws(token).getBody();

        return claims.getSubject();
    }

    public Long getJwtExpirationMillis() {
        return jwtExpirationMillis;
    }

    private PrivateKey getPrivateKey(){
        try {
            return (PrivateKey) keyStore.getKey("springBlog", "secret".toCharArray());
        }
        catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving key from keystore", e);
        }
    }

    private PublicKey getPublickey() {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new
                    SpringRedditException("Exception occured while retrieving public key from keystore", e);
        }
    }

}
