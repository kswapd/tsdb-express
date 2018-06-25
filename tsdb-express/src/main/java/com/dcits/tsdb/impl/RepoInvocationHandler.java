package com.dcits.tsdb.impl;

import com.dcits.tsdb.interfaces.BarInter;
import com.dcits.tsdb.interfaces.FooInter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import javax.annotation.Resource;
import org.influxdb.InfluxDB;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */
public class RepoInvocationHandler implements InvocationHandler {

	private Class<?> interfaceClass;
	private ParameterizedType tp;

	//@Resource(name="fooInterImpl")
	private FooInter fooInterImpl;
	private CustomRepoImpl repoImpl;
	private Class<?> innerClass;
	private BarInterImpl barInterImpl;
	//private Object influxImpl;
	public Object bind(Class<?> cls) {
		this.interfaceClass = cls;
		fooInterImpl = FooInterImpl.getInstance();

		barInterImpl = BarInterImpl.getInstance();
		repoImpl = CustomRepoImpl.getInstance();
		for (Type t : cls.getGenericInterfaces()) {

			//System.out.println(t);
			System.out.println(t + "----------ok");
			/*for (Type t1:((ParameterizedType)t).getActualTypeArguments()) {

				System.out.println(t1 + "----------ok");
				//tp = t1;

			}*/
			if(t.toString().contains("<")){
				Type t1 = ((ParameterizedType)t).getActualTypeArguments()[0];
				System.out.println(t1 + "----------okok");
				Class<?> curClass = null;
				/*try {
					Class<?> clazz = (Class<?>) t1;
					//curClass = Class.forName(t1.getClass());
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}*/
				innerClass = (Class<?>) t1;
				barInterImpl.setInnerClass((Class<?>) t1);
				repoImpl.setInnerClass((Class<?>) t1);
			}
		}
		//ParameterizedTypeImpl
		/*try {
			influxImpl = InfluxDBRepo.class.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}*/


		return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {interfaceClass}, this);
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Class<?>[] classArr = null;
		Type[] typeArr = null;
		barInterImpl.setInnerClass(innerClass);
		repoImpl.setInnerClass(innerClass);


		String methodName = method.getName();
		Class <?> barClass = barInterImpl.getClass();
		Class <?> repoClass = repoImpl.getClass();

		for (Method methods:repoClass.getDeclaredMethods()){

			if(methods.getName() == methodName) {
				//System.out.println(methods.getName());
				typeArr = method.getGenericParameterTypes();
				break;
			}
		}

		if(args != null && args.length > 0) {
			classArr = new Class<?>[args.length];
			for(int i = 0; i < args.length; i ++){
				System.out.println("["+args[i].getClass().getName());
				classArr[i] = args[i].getClass();//Object.class;//
				System.out.println("]"+classArr[i].getName());
			}
		}


		if(typeArr!=null && typeArr.length > 0) {
			for(int i = 0; i < typeArr.length; i ++){
				//classArr[i] = Object.class;
				System.out.println("[[["+typeArr[i].getClass().getName());
				if(typeArr[i].getClass().getName() == "sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl" || typeArr[i].getClass().getName() == "sun.reflect.generics.reflectiveObjects.TypeVariableImpl"){
					classArr[i] = Object.class;
				}
			}
		}

		/*for(int i = 0; i < classArr.length; i ++){

			System.out.println("ffff:"+classArr[i].getName());
		}*/

		Method me = repoClass.getDeclaredMethod(methodName, classArr);
		return me.invoke(repoImpl, args);
	}

}
