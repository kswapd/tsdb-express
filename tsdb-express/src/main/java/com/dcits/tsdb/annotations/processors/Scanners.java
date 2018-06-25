package com.dcits.tsdb.annotations.processors;

/**
 * Created by kongxiangwen on 6/21/18 w:25.
 */
import com.dcits.tsdb.annotations.Column;
import java.lang.annotation.Annotation;
import org.springframework.beans.BeansException;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


@Component
public class Scanners implements BeanPostProcessor {


	/**
	 * be invoked after bean has been populated with property values and before bean was initialized.
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */

	public Object postProcessBeforeInitialization(Object bean, String beanName)

			throws BeansException {

		//System.out.println(beanName + " before initialize");

		for(Annotation annotation: bean.getClass().getAnnotations()){
			//System.out.println(annotation.annotationType().getCanonicalName());
			//System.out.println(Column.class.getCanonicalName());
		}
		return bean;

	}

	/**
	 * be invoked after bean was initialized.
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */

	public Object postProcessAfterInitialization(Object bean, String beanName)

			throws BeansException {

		//System.out.println(beanName + " after initialize");

		return bean;

	}

}
