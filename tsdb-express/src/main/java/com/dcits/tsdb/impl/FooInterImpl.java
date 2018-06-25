package com.dcits.tsdb.impl;

import com.dcits.tsdb.interfaces.FooInter;
import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */
@Component
public class FooInterImpl implements FooInter {
	private static int curNum = 0;
	private static FooInterImpl inst = null;
	@Override
	public String Foo()
	{
			curNum ++;
			return "FooInterImpl:"+curNum;
	}


	public static FooInterImpl getInstance()
	{
		if(inst == null){
			inst = new FooInterImpl();

		}
		return inst;
	}
}
