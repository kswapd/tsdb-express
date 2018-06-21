package com.dcits.tsdb.annotations.processors;

/**
 * Created by kongxiangwen on 6/21/18 w:25.
 */
import org.springframework.beans.BeansException;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


@Component
public class Scanners implements BeanPostProcessor {


	// Bean 实例化之前进行的处理

	public Object postProcessBeforeInitialization(Object bean, String beanName)

			throws BeansException {

		System.out.println("Initializing " + beanName);

		return bean;

	}

	// Bean 实例化之后进行的处理

	public Object postProcessAfterInitialization(Object bean, String beanName)

			throws BeansException {

		System.out.println(beanName + " initialized");

		return bean;

	}

}
