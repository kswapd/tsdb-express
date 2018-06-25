package com.dcits.tsdb.impl;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */

public class RepoProxyFactory<T> implements FactoryBean<T> {

	private Class<T> interfaceClass;
	public Class<T> getInterfaceClass() {
		return interfaceClass;
	}
	public void setInterfaceClass(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}
	@Override
	public T getObject() throws Exception {
		return (T) new RepoInvocationHandler().bind(interfaceClass);
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}

	@Override
	public boolean isSingleton() {
		// 单例模式
		return true;
	}

}