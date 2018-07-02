package com.dcits.tsdb.impl;

import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.utils.ExecutedMethodInterceptor;
import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Objects;
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
			/*for (Type t1:((ParameterizedType)t).getActualTypeArguments()) {

				System.out.println(t1 + "----------ok");
				//tp = t1;

			}*/
			if(t.toString().contains("<")){
				Type t1 = ((ParameterizedType)t).getActualTypeArguments()[0];
				Class<?> curClass = null;
				/*try {
					Class<?> clazz = (Class<?>) t1;
					//curClass = Class.forName(t1.getClass());
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}*/
				innerClass = (Class<?>) t1;
				repoImpl.setInnerClass((Class<?>) t1);
				break;
			}
		}
		return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {interfaceClass}, this);
	}

	private String getMeasurementName(Class <?> clazz)
	{
		String measurementName = null;
		Measurement measure = (Measurement) clazz.getAnnotation(Measurement.class);
		if(measure != null){
			measurementName = measure.name();
		}
		//if null, use class name.
		if(StringUtils.isEmpty(measurementName)){
			String lastName = ClassUtils.getShortName(clazz.getName());
			measurementName = Introspector.decapitalize(lastName);

		}
		Objects.requireNonNull(measurementName, "measurementName");
		return measurementName;
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
		}


		/*if(typeArr!=null && typeArr.length > 0) {
			for(int i = 0; i < typeArr.length; i ++){
				//classArr[i] = Object.class;
				//System.out.println("[[["+typeArr[i].getClass().getName());
				if(typeArr[i].getClass().getName() == "sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl" || typeArr[i].getClass().getName() == "sun.reflect.generics.reflectiveObjects.TypeVariableImpl"){
					classArr[i] = Object.class;
				}
			}
		}*/

		/*for(int i = 0; i < classArr.length; i ++){

			System.out.println("ffff:"+classArr[i].getName());
		}*/


		if(implFoundMethod) {
			Method me = repoClass.getDeclaredMethod(methodName, classArr);
			return me.invoke(repoImpl, args);
		}else if (methodName.contains("findBy") || methodName.contains("aggregateBy")) {
				String measurementName = getMeasurementName(innerClass);
				for (Method methods : repoClass.getDeclaredMethods()) {
					if (methods.getName().equals("find")) {
						baseQueryMethod = methods;
						break;
					}
				}
				Objects.requireNonNull(baseQueryMethod, "baseQueryMethod");
				String sqlQuery = ExecutedMethodInterceptor.getInfluxDBSql(measurementName, method, args);
				Object obj = baseQueryMethod.invoke(repoImpl, sqlQuery);
				return obj;

		}
		return null;


	}

}
