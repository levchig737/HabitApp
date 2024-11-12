package org.habitApp.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
/**
 * Аспект логирования
 */
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Логирование выполнения метода и возврата результата.
     * @param joinPoint joinPoint
     * @return result
     * @throws Throwable Throwable
     */
    @Around("execution(* org.habitApp..controllers..*(..)) || execution(* org.habitApp..services..*(..)) || execution(* org.habitApp..repositories..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        logger.info("Method {} is about to execute with arguments: {}",
                joinPoint.getSignature(),
                joinPoint.getArgs());

        Object result;
        try {
            result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            logger.info("Method {} executed successfully, returned: {}, execution time: {} ms",
                    joinPoint.getSignature(),
                    result,
                    endTime - startTime);
        } catch (Throwable throwable) {
            logger.error("Exception in method {}: {}",
                    joinPoint.getSignature(),
                    throwable.getMessage());
            throw throwable;
        }
        return result;
    }
}
