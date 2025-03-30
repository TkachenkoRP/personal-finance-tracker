package com.my.aspect;

import com.my.service.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditionAspect {
    private final Marker marker = MarkerFactory.getMarker("AUDITION");

    @Pointcut("within(@com.my.annotation.Audition *) && execution(public * * (..))")
    public void annotatedByAudition() {
    }

    @Before("annotatedByAudition()")
    public void before(JoinPoint joinPoint) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UserManager.getLoggedInUser() == null ? "No User: " : UserManager.getLoggedInUser().getId() + " - " + UserManager.getLoggedInUser().getName() + ": ");
        stringBuilder.append("Calling method ").append(joinPoint.getSignature());
        log.info(marker, stringBuilder.toString());
    }

    @AfterReturning(pointcut = "annotatedByAudition()", returning = "result")
    public void after(JoinPoint joinPoint, Object result) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UserManager.getLoggedInUser() == null ? "No User: " : UserManager.getLoggedInUser().getId() + " - " + UserManager.getLoggedInUser().getName() + ": ");
        if (result instanceof ResponseEntity<?> responseEntity) {
            int statusCode = responseEntity.getStatusCode().value();
            String methodName = joinPoint.getSignature().getName();
            stringBuilder.append("Method ").append(methodName).append(" finished with code ").append(statusCode);
        }
        log.info(marker, stringBuilder.toString());
    }
}
