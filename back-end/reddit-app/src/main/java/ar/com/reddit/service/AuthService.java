package ar.com.reddit.service;

import ar.com.reddit.dto.AuthenticationResponse;
import ar.com.reddit.dto.LoginRequest;
import ar.com.reddit.dto.RefreshTokenRequest;
import ar.com.reddit.dto.RegisterRequest;
import ar.com.reddit.exceptions.SpringRedditException;
import ar.com.reddit.model.NotificationEmail;
import ar.com.reddit.model.User;
import ar.com.reddit.model.VerificationToken;
import ar.com.reddit.respository.UserRepository;
import ar.com.reddit.respository.VerificationTokenRepository;
import ar.com.reddit.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvidder;
    private final RefreshTokenService refreshTokenService;

    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder
                .encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(Boolean.FALSE);

        userRepository.save(user);

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail("Please Activate your Avvount",
                user.getEmail(), "Thank you for signing up to Spring Reddit" +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accouuntVerification"+token));
    }

    @Transactional
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User)
                        SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();
        return userRepository.findByusername(principal.getUsername())
                .orElseThrow(() ->
                        new SpringRedditException("username not found - "
                                + principal.getUsername()));
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken =
                verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(
                () -> new SpringRedditException("Invalid Token")));
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken
                        (loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvidder.generateToken(authenticate);

        return AuthenticationResponse.builder().authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvidder.getJwtExpirationMillis()))
                .username(loginRequest.getUsername()).build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefresToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvidder.generateTokenWithuserNama(refreshTokenRequest.getUsername());

        return AuthenticationResponse.builder().authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant
                        .now().plusMillis(jwtProvidder.getJwtExpirationMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);

        return token;
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByusername(username)
                .orElseThrow(() ->
                        new SpringRedditException("User not fount with the name - " + username));
        user.setEnabled(Boolean.TRUE);
        userRepository.save(user);
    }
}