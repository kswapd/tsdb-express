package com.dcits.tsdb.annotations.processors;

/**
 * Created by kongxiangwen on 6/22/18 w:25.
 */
import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;


@Component
public class InstantiationBeanScanners extends
		InstantiationAwareBeanPostProcessorAdapter {

	public Object postProcessBeforeInstantiation(Class<?> beanClass,
												 String beanName) throws BeansException {
			System.out.println(beanName + "  before instant");
		return null;
	}

	public boolean postProcessAfterInstantiation(Object bean, String beanName)
			throws BeansException {

		System.out.println(beanName + "  after instant");

		return true;
	}

	public PropertyValues postProcessPropertyValues(PropertyValues pvs,
													PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {
		System.out.println(beanName + "  after properties");

		return pvs;
	}

}