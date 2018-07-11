package com.dcits.tsdb.impl;

import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.annotations.QueryMeasurement;
import com.dcits.tsdb.exceptions.MethodInvocationException;
import com.dcits.tsdb.utils.ExecutedMethodInterceptor;
import com.dcits.tsdb.utils.MeasurementUtils;
import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */
public class RepoInvocationHandler implements InvocationHandler {

	private Class<?> interfaceClass;
	private ParameterizedType tp;

	private CustomRepoImpl repoImpl;
	private Class<?> innerClass;
	public Object bind(Class<?> cls) {
		this.interfaceClass = cls;
		repoImpl = CustomRepoImpl.getInstance();
		for (Type t : cls.getGenericInterfaces()) {
			//kxw todo other generic type check method?
			if(t.toString().contains("<")){
				Type t1 = ((ParameterizedType)t).getActualTypeArguments()[0];
				Class<?> curClass = null;
				innerClass = (Class<?>) t1;
				repoImpl.setInnerClass((Class<?>) t1);
				break;
			}
		}
		return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {interfaceClass}, this);
	}



	TimeUnit getTimeUnit(Class <?> clazz) {
		TimeUnit tu = TimeUnit.MILLISECONDS;
		Measurement measure = (Measurement) clazz.getAnnotation(Measurement.class);
		if(measure != null){
			tu = measure.timeUnit();
		}

		Objects.requireNonNull(tu, "Measurement.TimeUnit");
		return tu;
	}


	private String getQueryMeasurementName(Method method)
	{


		Class<?> clazz = interfaceClass;
		String queryMeasurementName = null;
		QueryMeasurement measure = (QueryMeasurement) method.getAnnotation(QueryMeasurement.class);
		if(measure != null){
			queryMeasurementName = measure.name();
		}
		//if null, return null.
		return queryMeasurementName;
	}

	TimeUnit getQueryTimeUnit(Method method) {
		TimeUnit tu = TimeUnit.MILLISECONDS;
		//Measurement measure = (Measurement) clazz.getAnnotation(Measurement.class);
		QueryMeasurement measure = (QueryMeasurement) method.getAnnotation(QueryMeasurement.class);
		if(measure != null){
			tu = measure.timeUnit();
		}
		Objects.requireNonNull(tu, "Measurement.TimeUnit");
		return tu;
	}

	/**
	 * Use CustomRepoImpl to proxy user repo interface, as type erasure of generic type class, method.invoke will not find method if
	 * we use parameter class type, so we convert parameter class type to Object.class if parameters of declared
	 * method is type 'ParameterizedTypeImpl' or 'TypeVariableImpl'.
	 * @param proxy  proxy of user declared interface
	 * @param method the mothod invoked
	 * @param args	paramters
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Class<?>[] classArr = null;
		Type[] typeArr = null;
		repoImpl.setInnerClass(innerClass);


		String methodName = method.getName();
		Class <?> repoClass = repoImpl.getClass();
		boolean implFoundMethod = false;
		Method baseQueryMethod = null;
		for (Method methods:repoClass.getDeclaredMethods()){

			if(methods.getName().equals(methodName)) {
				//System.out.println(methods.getName());
				typeArr = method.getGenericParameterTypes();
				implFoundMethod  = true;
				break;
			}
		}

		//as reason of type erasure in generic type,  method.invoke must use Object.class as generic type when the parameter
		//type is T or list<T> etc.
		if(implFoundMethod) {
			if (args != null && args.length > 0) {
				classArr = new Class<?>[args.length];
				for (int i = 0; i < args.length; i++) {
					//System.out.println("["+args[i].getClass().getName());
					classArr[i] = args[i].getClass();//Object.class;//
					//System.out.println("]"+classArr[i].getName());
					if (typeArr[i].getClass().getName().equals("sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl") || typeArr[i].getClass().getName().equals("sun.reflect.generics.reflectiveObjects.TypeVariableImpl")) {
						classArr[i] = Object.class;
					}
				}
			}

			Method me = repoClass.getDeclaredMethod(methodName, classArr);
			return me.invoke(repoImpl, args);

		}else if (methodName.contains("findBy") || methodName.contains("aggregateBy")) {
				String useMeasurement = null;
				TimeUnit tu = TimeUnit.MILLISECONDS;
				//Use @QueryMeasurement of interface first. If this is null, use @Measuerment, if still null,
				//use bean class name.
				useMeasurement = MeasurementUtils.getQueryMeasurementName(method);
				tu = getQueryTimeUnit(method);
				if(StringUtils.isEmpty(useMeasurement)) {
					useMeasurement = MeasurementUtils.getMeasurementName(innerClass);
					tu = getTimeUnit(innerClass);

				}


				for (Method methods : repoClass.getDeclaredMethods()) {
					if (methods.getName().equals("find") && methods.getParameterTypes().length == 2) {
						baseQueryMethod = methods;
						break;
					}
				}
				Objects.requireNonNull(baseQueryMethod, "baseQueryMethod");
				String sqlQuery = ExecutedMethodInterceptor.getInfluxDBSql(useMeasurement, method, args);
				Object obj = baseQueryMethod.invoke(repoImpl, sqlQuery, tu);
				return obj;

		}else{

			throw new IllegalArgumentException("Invocation method not found:"+methodName);
		}


	}

}
