package org.example.expert.config.filter;

import jakarta.servlet.*;

import java.io.IOException;

public class MockTestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
