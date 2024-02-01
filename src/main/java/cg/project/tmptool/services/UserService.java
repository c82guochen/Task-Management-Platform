package cg.project.tmptool.services;

import cg.project.tmptool.dto.User;
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
        String password = newUser.getPassword();
        newUser.setPassword(cryptPasswordEncoder.encode(password));
        return userRepository.save(newUser);
    }
}