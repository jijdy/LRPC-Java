package com.jijdy.lrpcjava.annotation;

import java.lang.annotation.*;

/* throw out and register service Object to registry
 * @Author jijdy
 * @Date 2021/12/21 14:41
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LRPCService {

    String version() default "";


}
