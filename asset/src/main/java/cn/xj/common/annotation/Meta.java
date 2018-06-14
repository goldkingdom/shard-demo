package cn.xj.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Meta {

    boolean pKey() default false;

    boolean nullable() default true;

    String column() default "";

    String generator() default "";

    String defaultValue() default "";

    String defaultCheck() default "";

}
