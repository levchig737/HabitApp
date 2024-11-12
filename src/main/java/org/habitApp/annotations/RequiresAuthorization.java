package org.habitApp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки авторизации пользователя перед вызовом метода
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAuthorization {

    /**
     * Указывается если доступ только для админа
     * @return true если админ, false в ином случае.
     */
    boolean forAdmin() default false;
}
