package org.habitApp.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Логирует медленные методы, которые работают дольше заданного порога.
 */
@Aspect
@Component
public class AuditLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingAspect.class);
    private static final long THRESHOLD = 1000; // Порог времени в миллисекундах

    /**
     * Логирует медленные методы, которые работают дольше заданного порога.
     * @param joinPoint точка выполнения метода
     * @return результат выполнения метода
     * @throws Throwable любое исключение
     */
    @Around("execution(* org.habitApp.controllers..*(..))")
    public Object logSlowMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (duration > THRESHOLD) {
            logger.warn("Method {} executed in {} ms, exceeding threshold of {} ms",
                    joinPoint.getSignature(),
                    duration,
                    THRESHOLD);
        }

        return result;
    }
}
