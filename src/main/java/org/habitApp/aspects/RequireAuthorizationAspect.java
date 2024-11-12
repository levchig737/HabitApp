package org.habitApp.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.habitApp.annotations.RequiresAuthorization;
import org.habitApp.auth.AuthInMemoryContext;
import org.habitApp.domain.entities.UserEntity;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Аспект для проверки авторизации перед вызовом метода
 */
@Aspect
@Component
public class RequireAuthorizationAspect {
    @Pointcut("@annotation(org.habitApp.annotations.RequiresAuthorization)")
    public void annotatedByRequiresAnnotation() {}

    @Around("annotatedByRequiresAnnotation()")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {
        UserEntity currentUser = AuthInMemoryContext.getContext().getAuthentication();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        RequiresAuthorization annotation = method.getAnnotation(RequiresAuthorization.class);

        if (annotation.forAdmin() && !currentUser.isAdmin()) {
            throw new UnauthorizedAccessException("User is not admin");
        }

        return joinPoint.proceed();
    }
}
