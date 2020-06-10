package ar.com.reddit.service;

import ar.com.reddit.exceptions.SpringRedditException;
import ar.com.reddit.model.RefreshToken;
import ar.com.reddit.respository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreateDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    void validateRefresToken(String token) {
        refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> new SpringRedditException("Invalid refresh Token"));
    }

    public void deleteRefresToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

}
