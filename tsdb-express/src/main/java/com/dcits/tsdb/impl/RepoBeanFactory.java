package com.dcits.tsdb.impl;

import com.dcits.tsdb.interfaces.CustomRepo;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kongxiangwen on 6/22/18 w:25.
 */


//@Configuration
public class RepoBeanFactory {

	@Bean
	public CustomRepo customRepo() {
		ProxyFactory factory = new ProxyFactory(new CustomRepoImpl());
		factory.addAdvice(new CustomRepoInterceptor());
		CustomRepo repo = (CustomRepo) factory.getProxy();
		//CustomRepo repo = new CustomRepoImpl();
		//return new CustomRepoImpl();
		return repo;
	}
}




