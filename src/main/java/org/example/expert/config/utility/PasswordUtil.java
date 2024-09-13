package org.example.expert.config.utility;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordUtil {
    // 비밀번호는 소문자, 숫자, 대문자가 필수로 들어가야 하는 8 ~ 15글자의 문자열이다.
    private final String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,15}$";

    /**
     * 비밀번호가 조건에 맞는지 확인하는 메서드
     * @param password 작성된 비밀번호
     * @return True : 조건에 맞는 비밀번호 / False : 조건에 부합한 비밀번호
     */
    public boolean isValidPassword(String password) {
        return Pattern.matches(regex, password);
    }
}