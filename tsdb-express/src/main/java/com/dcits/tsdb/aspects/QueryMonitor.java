package com.dcits.tsdb.aspects;

import java.lang.reflect.Method;
import javax.xml.ws.Response;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 6/21/18 w:25.
 */

@Component
@Aspect
public class QueryMonitor {
	@Pointcut("execution(* com.dcits.tsdb.impl.*.*(..))")
	public void pointCut() {
	}

	@After("pointCut()")
	public void after(JoinPoint joinPoint) {


	}

	@Before("pointCut()")
	public void before(JoinPoint joinPoint) {

	}

	@AfterReturning(pointcut = "pointCut()", returning = "returnVal")
	public void afterReturning(JoinPoint joinPoint, Object returnVal) {

	}

	@Around("pointCut()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {

		Object response = null;
		//如果需要这里可以取出参数进行处理

		Object[] args = pjp.getArgs();
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();

		String methodName = method.getName();
		String className = signature.getDeclaringTypeName();;
		boolean timeStat = false;
		long start = 0;
		if(methodName.startsWith("findBy") || methodName.startsWith("write") || methodName.startsWith("query")) {
			timeStat = true;
		}

		if(timeStat) {
			start = System.currentTimeMillis();
		}

		try {
			response = (Object) pjp.proceed();
		} catch (Throwable ex) {
			System.out.println("error in around");
			throw ex;
		}
		if(timeStat) {
			long duration = System.currentTimeMillis() - start;
			System.out.println(String.format("%s.%s:time elapsed  %d milliseconds", className,methodName, duration));
		}
		return response;

	}

	@AfterThrowing(pointcut = "pointCut()", throwing = "error")
	public void afterThrowing(JoinPoint jp, Throwable error) {
		System.out.println("error:" + error);
	}


}
