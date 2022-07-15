package com.demo.withfootball.modules.account.validator;

import com.demo.withfootball.modules.account.AccountRepository;
import com.demo.withfootball.modules.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor //private final type 의 member variable 의 생성자 생성
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass){
        return aClass.isAssignableFrom(SignUpForm.class); //sign-up form type instance 검증
    }

    @Override
    public void validate(Object object, Errors errors){ //email, nickname 중복 여부 검사
        SignUpForm signUpForm = (SignUpForm) object;
        if(accountRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email","invalid.email",new Object[]{signUpForm.getEmail()},"이미 사용중인 이메일입니다.");
        }
        if(accountRepository.existsByNickname(signUpForm.getNickname())){
            errors.rejectValue("nickname","invalid.nickname",new Object[]{signUpForm.getNickname()},"이미 사용중인 닉네임입니다.");
        }


    }
}
