package config;

import org.springframework.stereotype.Component;

import org.example.expert.config.UserDetailsImpl;
import org.example.expert.config.filter.MockTestFilter;
import org.example.expert.domain.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Component
public class TestWebConfig {
    /**
     * mockMvc에 사용할 Filter를 MockTestFilter로 지정
     * @param wac Mvc에 사용할 WebApplicationContext객체
     * @return Filter가 적용된 MockMvc객체
     */
    public static MockMvc setFilter(WebApplicationContext wac) {
        return MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity(new MockTestFilter()))
                .build();
    }

    /**
     * User정보를 인증이 완료된 객체로 변환해주는 메서드
     * @param user 인증완료 처리할 유저 객체
     * @return User정보가 담긴 인증 완료 객체
     */
    public static UsernamePasswordAuthenticationToken userSetup(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}