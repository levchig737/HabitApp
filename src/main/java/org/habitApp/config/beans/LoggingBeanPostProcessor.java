package org.habitApp.config.beans;

import org.habitApp.annotations.Loggable;
import org.habitApp.aspects.LoggingAspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {

    private final Advisor loggingAdvisor;

    public LoggingBeanPostProcessor(LoggingAspect loggingAspect) {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(Loggable.class, true);
        this.loggingAdvisor = new DefaultPointcutAdvisor(pointcut, loggingAspect);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Проверяем, нужно ли создавать прокси для данного бина
        if (isEligibleForProxying(bean)) {
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.addAdvisor(loggingAdvisor);
            return proxyFactory.getProxy();
        }
        return bean;
    }

    // Метод для проверки, нужно ли создавать прокси для бина
    private boolean isEligibleForProxying(Object bean) {
        // Проверяем, есть ли у бина аннотация @Loggable
        if (bean.getClass().isAnnotationPresent(Loggable.class)) {
            return true;
        }
        // Проверяем, есть ли у методов бина аннотация @Loggable
        for (Method method : bean.getClass().getMethods()) {
            if (method.isAnnotationPresent(Loggable.class)) {
                return true;
            }
        }
        // Если аннотация @Loggable не найдена, то прокси не нужен
        return false;
    }
}
