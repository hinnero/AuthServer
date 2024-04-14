package com.example.auth.services;

import com.example.auth.controller.AuthenticationRequest;
import com.example.auth.controller.AuthenticationResponse;
import com.example.auth.controller.RegisterRequest;
import com.example.auth.repo.UserRepo;
import com.example.auth.user.Role;
import com.example.auth.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserModel.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepo.save(user);
        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
        
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            var user = userRepo.findByUsername(request.getUsername()).orElseThrow();

            var jwt = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwt)
                    .build();
        } catch (Exception ex) {
            System.out.println("Authorization exception - " + ex.getMessage());
            return null;
        }
    }
}
