package auth.auth_app_backend.auth.services.impl;

import auth.auth_app_backend.auth.config.ApiConstants;
import auth.auth_app_backend.auth.payload.UserDto;

import auth.auth_app_backend.auth.entities.Provider;
import auth.auth_app_backend.auth.entities.Role;
import auth.auth_app_backend.auth.entities.User;
import auth.auth_app_backend.auth.services.UserService;
import auth.auth_app_backend.exceptions.ResourceNotFoundException;
import auth.auth_app_backend.auth.helpers.UserHelper;
import auth.auth_app_backend.auth.repositories.RoleRepository;
import auth.auth_app_backend.auth.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository ;
    private ModelMapper modelMapper;
    private RoleRepository roleRepository;



    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
    }

        //...



    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        if(userDto.getEmail()==null||userDto.getEmail().isBlank()){
            throw new IllegalArgumentException("email required");
        }
        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("email already exists");
        }
        //extra checks
        User user=modelMapper.map(userDto,User.class);

       user.setProvider(userDto.getProvider()!=null?userDto.getProvider(): Provider.Local);

       //assign role
        Role role=roleRepository.findByName("ROLE_"+ ApiConstants.GUEST_ROLE).orElse(null);
        user.getRole().add(role);
        User savedUser=this.userRepository.save(user);


       return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
       User user= userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found with the given email ID."));
       return modelMapper.map(user,UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        UUID uId=UserHelper.parseUUID(userId);
        User existUser=userRepository.findById(uId).orElseThrow(()->new ResourceNotFoundException("user not found with givwn id"));
        if(userDto.getName()!=null) existUser.setName((userDto.getName()));
        if(userDto.getImage()!=null) existUser.setImage((userDto.getImage()));
        if(userDto.getProvider()!=null) existUser.setProvider((userDto.getProvider()));
        if(userDto.getPassword()!=null) existUser.setPassword((userDto.getPassword()));

        existUser.setEnabled(userDto.isEnabled());
        existUser.setUpdatedAt(userDto.getUpdatedAt());
        User updatedUser=userRepository.save(existUser);
        return modelMapper.map(updatedUser,UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {
        UUID uId= UserHelper.parseUUID(userId);
        User user=this.userRepository.findById(uId).orElseThrow(()-> new ResourceNotFoundException("User not found with given email"));
       userRepository.delete(user);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        User user=this.userRepository.findById(UserHelper.parseUUID(userId)).orElseThrow(()-> new ResourceNotFoundException("User not found with given email"));
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public Iterable<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}