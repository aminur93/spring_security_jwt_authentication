package com.aminurDev.security.service;

import com.aminurDev.security.domain.entity.Role;
import com.aminurDev.security.domain.entity.User;
import com.aminurDev.security.domain.model.UserRequest;
import com.aminurDev.security.domain.repository.UserRepository;
import com.aminurDev.security.response.AuthenticationResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(UserRequest request) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public String login(UserRequest userRequest){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.getEmail(),
                        userRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(() -> new RuntimeException("User Invalid"));

        String jwtToken = jwtService.generateToken(user);

        return jwtToken;
    }
}
