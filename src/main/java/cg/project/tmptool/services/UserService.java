package cg.project.tmptool.services;

import cg.project.tmptool.dto.User;
import cg.project.tmptool.exceptions.UserNameExistsException;
import cg.project.tmptool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder cryptPasswordEncoder;

    public User saveUser(User newUser) {
        try {
            String password = newUser.getPassword();
            String username = newUser.getUsername();
            newUser.setPassword(cryptPasswordEncoder.encode(password));
            newUser.setUsername(username);
            return userRepository.save(newUser);
        } catch (Exception e) {
            throw new UserNameExistsException("Username: " + newUser.getUsername() + " already existed");
        }

    }
}