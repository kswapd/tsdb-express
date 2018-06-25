package com.dcits.tsdb.impl;

import com.dcits.tsdb.interfaces.BarInter;
import com.dcits.tsdb.interfaces.FooInter;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */
@Component
public class BarInterImpl<T> implements BarInter<T> {
	private static int curNum = 0;
	private static BarInterImpl inst = null;
	private Class<T> innerClass;

	private T ta;
	@Override
	public String Bar()
	{
			//T a;
			curNum ++;

			return "BarInterImpl:";

	}

	@Override
	public T setT(T a)
	{
		System.out.println("11111:"+a.getClass().getName());
		try {
			ta = innerClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
//		System.out.println(real.toString());

		return ta;

	}
	@Override
	public T getT()
	{
		T a = null;
		try {
			a = innerClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		ta = a;
//		System.out.println(real.toString());
		return ta;
	}

	public void setInnerClass(Class <T> cls){
		System.out.println("setInnerClass:"+cls.getName());
		innerClass = cls;
	}



	public static BarInterImpl getInstance()
	{
		if(inst == null){
			inst = new BarInterImpl();

		}
		return inst;
	}
}
