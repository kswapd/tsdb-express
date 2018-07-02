package com.dcits.tsdb.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */

//if use continuous query of influxdb, we may query data from other tables, eg:"rp_1d"."memory_mean_2m"
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface QueryMeasurement {

	String name() default "";

	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
