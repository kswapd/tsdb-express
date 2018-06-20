package com.dcits.tsdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Measurement {

	String name();

	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
