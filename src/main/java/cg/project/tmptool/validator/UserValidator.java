package cg.project.tmptool.validator;

import cg.project.tmptool.dto.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
    //  确定该验证器（UserValidator）是否可以对特定类型的对象（即传入的 clazz 参数）进行验证。
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (user.getPassword().length() < 6) {
            errors.rejectValue("password", "Length", "Password must be at least 6 characters");
        }

        if (!user.getPassword().equals(user.getConfirmedPassword())) {
            errors.rejectValue("confirmedPassword", "Match", "Confirmed Password must match with the Password");
        }
    }
}