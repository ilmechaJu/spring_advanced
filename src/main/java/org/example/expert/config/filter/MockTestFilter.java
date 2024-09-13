package org.example.expert.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.UserDetailsImpl;
import org.example.expert.domain.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Slf4j
public class MockTestFilter implements Filter {

    /**
     * Mock Test용 우회 필터
     * @param servletRequest Mock principal이 담긴 Request 객체
     * @param servletResponse Attribute가 담길 Responce객체
     * @param filterChain 다음 Filter Chain
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            String url = request.getRequestURI();
            if (url.startsWith("/auth")) {
                filterChain.doFilter(request, servletResponse);
                return;
            }

            // request에 담긴 인증된 유저 정보를 추출
            Authentication authentication = (Authentication)request.getUserPrincipal();

            // SecurityContextHolder에 인증된 사용자 정보로 User정보를 넘겨준다.
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            User user = ((UserDetailsImpl)authentication.getPrincipal()).getUser();
            insertRequestUser(user, request);

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    /**
     * HttpServlerRequest에 user정보 삽입하는 메서드
     * @param user 삽입할 유저 정보
     * @param request request 객체
     */
    private void insertRequestUser(User user, HttpServletRequest request) {
        // @AutoUser 에너테이션을 위해서 해당 Attribute를 넣어준다.
        request.setAttribute("userId", user.getId());
        request.setAttribute("email", user.getEmail());
        request.setAttribute("userRole", user.getUserRole().name());
    }
}