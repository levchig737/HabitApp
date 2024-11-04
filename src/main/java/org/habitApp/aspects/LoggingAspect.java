package org.habitApp.aspects;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Arrays;

public class LoggingAspect implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = invocation.getMethod().getDeclaringClass().getSimpleName();
        String methodName = invocation.getMethod().getName();
        Object[] args = invocation.getArguments();

        System.out.println("Entering " + className + "." + methodName + " with arguments: " + Arrays.toString(args));

        Object result = invocation.proceed();

        long endTime = System.currentTimeMillis();
        System.out.println("Exiting " + className + "." + methodName + ", execution time: " + (endTime - startTime) + " ms");

        return result;
    }
}
