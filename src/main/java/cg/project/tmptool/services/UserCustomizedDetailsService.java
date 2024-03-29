package cg.project.tmptool.services;

import cg.project.tmptool.dto.User;
import cg.project.tmptool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCustomizedDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + "not found");
        }

        return user;
    }

    @Transactional
    public User loadUserById(Long id) {
        boolean isExist = userRepository.findById(id).isPresent();
        if (!isExist) {
            throw new UsernameNotFoundException("User with id: " + id + "not existed");
        }
        return userRepository.findById(id).get();
    }

}