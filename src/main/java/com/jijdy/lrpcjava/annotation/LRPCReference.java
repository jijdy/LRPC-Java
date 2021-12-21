package com.jijdy.lrpcjava.annotation;

import java.lang.annotation.*;

/* autowire proxy field to bean
 * @Author jijdy
 * @Date 2021/12/21 14:38
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LRPCReference {

    String version() default "";

    String addr() default "";
}
