package com.my.aspect;

import com.my.service.UserManager;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AuditionAspect {

    @Pointcut("within(@com.my.annotation.Audition *) && execution(private * * (..))")
    public void annotatedByAudition() {
    }

    @Before("annotatedByAudition()")
    public void before(JoinPoint joinPoint) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UserManager.getLoggedInUser() == null ? "No User: " : UserManager.getLoggedInUser().getId() + " - " + UserManager.getLoggedInUser().getName() + ": ");
        stringBuilder.append("Calling method ").append(joinPoint.getSignature());
        System.out.println(stringBuilder);
    }

    @After("annotatedByAudition()")
    public void after(JoinPoint joinPoint) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UserManager.getLoggedInUser() == null ? "No User: " : UserManager.getLoggedInUser().getId() + " - " + UserManager.getLoggedInUser().getName() + ": ");
        stringBuilder.append("Execution of method ").append(joinPoint.getSignature()).append(" finished with code ");
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof HttpServletResponse response) {
                int responseCode = response.getStatus();
                stringBuilder.append(responseCode);
            }
        }
        System.out.println(stringBuilder);
    }
}
