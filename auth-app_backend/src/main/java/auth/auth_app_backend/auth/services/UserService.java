package auth.auth_app_backend.auth.services;

import auth.auth_app_backend.auth.payload.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    //create user
    UserDto createUser(UserDto userDto);

    //get user by email
    UserDto getUserByEmail(String email);

    //updated user
    UserDto updateUser(UserDto userDto,String userId);

    //delete user
    void deleteUser(String userId);

    //
    //get user by userid
    UserDto getUserByUserId(String userId);

    //all user
    Iterable<UserDto> getAllUsers();

}
