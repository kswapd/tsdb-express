package com.dcits.tsdb.interfaces;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */

@MyCustomBean
public interface BarInter<T>{

	public String Bar();
	public T getT();
	public T setT(T a);

}
