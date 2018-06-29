package com.dcits.tsdb.annotations;

import com.dcits.tsdb.impl.RepoBeanRegister;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by kongxiangwen on 6/23/18 w:25.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Configuration
@Import({RepoBeanRegister.class})
public @interface EnableRepoInterfaceScan {
	String[] value() default {"com.dcits"};
}
