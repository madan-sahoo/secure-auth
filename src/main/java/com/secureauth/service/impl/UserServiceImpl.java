package com.secureauth.service.impl;

import com.secureauth.dto.request.RegisterRequest;
import com.secureauth.dto.response.RegisterResponse;
import com.secureauth.entity.Role;
import com.secureauth.entity.User;
import com.secureauth.entity.UserRole;
import com.secureauth.exception.RoleNotFoundException;
import com.secureauth.exception.UserAlreadyExistException;
import com.secureauth.repository.RoleRepository;
import com.secureauth.repository.UserRepository;
import com.secureauth.repository.UserRoleRepository;
import com.secureauth.service.UserService;
import com.secureauth.util.Constant;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(Constant.BCRYPT_PASSWORD_STRENGTH);

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RegisterResponse register(RegisterRequest registerRequest) {
        try {
            // check user existence: if yes then throw exception already exist
            if (userRepository.existsByEmail(registerRequest.getEmail()))
                throw new UserAlreadyExistException("User already exist with " + registerRequest.getEmail());

            // create user save user
            User user = new User(registerRequest.getUsername(),
                    registerRequest.getEmail(), passwordEncoder.encode(registerRequest.getPassword()));
            User savedUser = userRepository.save(user);

            // Fetch default USER role and assign to created user
            Role defaultRoleUser = roleRepository.findByName(Constant.ROLE_USER)
                    .orElseThrow(() -> new RoleNotFoundException(Constant.ROLE_USER + " role not found to assign."));

            // assign role to user
            UserRole userRole = new UserRole(savedUser, defaultRoleUser);
            userRoleRepository.save(userRole);

            // return RegisterResponse
            return new RegisterResponse(user.getId(), user.getUsername(), Set.of(defaultRoleUser.getName()));
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Database constraint violation during user registration", ex);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
}
