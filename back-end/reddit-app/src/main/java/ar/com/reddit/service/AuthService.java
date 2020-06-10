package ar.com.reddit.service;

import ar.com.reddit.dto.RegisterRequest;
import ar.com.reddit.respository.UserRepository;
import ar.com.reddit.respository.VerificationTokenRepository;
import ar.com.reddit.security.JwtProvidder;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvidder jwtProvidder;
    private final RefreshTokenService refreshTokenService;

    public void signup(RegisterRequest registerRequest) {
        
    }


}
