package org.example.expert.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* org.example.expert.domain.comment.controller..*(..))")
    public void pointCut() {}
    @Pointcut("execution(* org.example.expert.domain.user.controller..*(..))")
    public void pointCut2() {}

    @Around(value = "pointCut() || pointCut2()")
    public Object logBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();//
        HttpServletRequest request;
        for (Object arg : args) {
            if (arg instanceof RequestFacade) {
                request = (HttpServletRequest) arg;
                String userId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";
                String requestUrl = request.getRequestURL().toString();
                LocalDateTime requestTime = LocalDateTime.now();
                log.info("User ID: {}, Request Time: {}, Request URL: {}",
                        userId, requestTime, requestUrl);
            }
        }
    return joinPoint.proceed();
    }

}
