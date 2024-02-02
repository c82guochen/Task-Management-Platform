package cg.project.tmptool.controller;

import cg.project.tmptool.dto.User;
import cg.project.tmptool.payload.LoginResponse;
import cg.project.tmptool.security.JwtTokenProvider;
import cg.project.tmptool.services.MapValidationErrorService;
import cg.project.tmptool.services.UserService;
import cg.project.tmptool.validator.UserValidator;
import cg.project.tmptool.payload.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import javax.validation.Valid;
import java.util.concurrent.locks.StampedLock;

import static cg.project.tmptool.security.SecurityConfigConsts.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidation(result);
        if (errorMap != null) {
            return errorMap;
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        // call authenticate() to verify the current username and password(which is the main part fot authentication manager)

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // getContext locks context because of possibly more than one user who sends requests
        // to confirm the token is associated with corresponding user

        String jwt = TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return new ResponseEntity<LoginResponse>(new LoginResponse(true, jwt), HttpStatus.OK);

    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
        userValidator.validate(user, result);

        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidation(result);
        if (errorMap != null) {
            return errorMap;
        }

        User newUser = userService.saveUser(user);

         return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }
}