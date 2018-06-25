package com.dcits.tsdb.impl;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by kongxiangwen on 6/23/18 w:25.
 */
public class CustomRepoInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("start");
		Object obj = invocation.proceed();
		System.out.println("end");
		return obj;
	}

}
