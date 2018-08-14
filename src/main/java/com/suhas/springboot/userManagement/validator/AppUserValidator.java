package com.suhas.springboot.userManagement.validator;

import com.suhas.springboot.userManagement.dao.AppUserDAO;
import com.suhas.springboot.userManagement.formbean.AppUserForm;
import com.suhas.springboot.userManagement.model.AppUser;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class AppUserValidator implements Validator{

    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired
    private AppUserDAO appUserDAO;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == AppUserForm.class;
    }

    @Override
    public void validate(@Nullable Object target, Errors errors) {
        AppUserForm appUserForm = (AppUserForm)target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty.appUserForm.userName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty.appUserForm.firstName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty.appUserForm.lastName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.appUserForm.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.appUserForm.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotEmpty.appUserForm.confirmPassword");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "NotEmpty.appUserForm.gender");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryCode", "NotEmpty.appUserForm.countryCode");

        if (!this.emailValidator.isValid(appUserForm.getEmail())) {
            errors.rejectValue("email","Pattern.appUserForm.email");
        } else if (appUserForm.getUserId() == null) {
            AppUser appUser = appUserDAO.findAppUserByEmail(appUserForm.getEmail());
            if (null != null) {
                //Email has been used by another account.
                errors.rejectValue("email","Duplicate.appUserForm.email");
            }
        }

        if (!errors.hasFieldErrors("userName")) {
            AppUser appUser = appUserDAO.findAppUserByUserName(appUserForm.getUserName());
            if (null != appUser) {
                // Username is not available.
                errors.rejectValue("userName", "Duplicate.appUserForm.userName");
            }
        }

        if (!errors.hasErrors()) {
            //Compare password if there is no errors.
            if (!appUserForm.getConfirmPassword().equals(appUserForm.getPassword())) {
                errors.rejectValue("confirmPassword", "Match.appUserForm.confirmPassword");
            }
        }

    }
}
