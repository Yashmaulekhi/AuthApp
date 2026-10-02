package auth.auth_app_backend.auth.services;

import auth.auth_app_backend.dtos.JwtAuthResponse;
import auth.auth_app_backend.auth.payload.LoginRequest;
import auth.auth_app_backend.auth.payload.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    UserDto registerUser(UserDto userDto);

    JwtAuthResponse login(LoginRequest loginDto);
    //login user


}
