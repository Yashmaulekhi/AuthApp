package auth.auth_app_backend.auth.services.impl;

import auth.auth_app_backend.auth.services.AuthService;
import auth.auth_app_backend.auth.services.UserService;
import auth.auth_app_backend.dtos.JwtAuthResponse;
import auth.auth_app_backend.auth.payload.LoginRequest;
import auth.auth_app_backend.auth.payload.UserDto;
import auth.auth_app_backend.auth.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDto registerUser(UserDto userDto) {
        //logic
        //verify email
        //verify password
        // default roles
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        //assign the default role

        UserDto userDto1 = userService.createUser(userDto);

        return userDto1;

    }

    @Override
    public JwtAuthResponse login(LoginRequest loginRequest) {
        return null;
    }


}
