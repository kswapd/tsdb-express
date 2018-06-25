package com.dcits.tsdb.impl;
import com.dcits.tsdb.interfaces.CustomRepo;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kongxiangwen on 6/23/18 w:25.
 */


@Configuration
public class RepoBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

//bean 的名称生成器.
private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();


@Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			System.out.println("MyBeanDefinitionRegistryPostProcessor.postProcessBeanFactory()");


			}

/**
 * 先执行：postProcessBeanDefinitionRegistry()方法，
 * 在执行：postProcessBeanFactory()方法。
 *
 */
@Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
			System.out.println("MyBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry()");

       /*
        * 在这里可以注入bean.
        */
		//registerBean(registry, "customRepoImplFac", CustomRepoImpl.class);

	//BeanDefinitionBuilder bb;
		/*ProxyFactory factory = new ProxyFactory(new CustomRepoImpl());
		factory.addAdvice(new CustomRepoInterceptor());
		CustomRepo repo = (CustomRepo) factory.getProxy();
		//CustomRepo repo = new CustomRepoImpl();
		//return new CustomRepoImpl();

		registerBean(registry, "customRepoImplFac", CustomRepoImpl.class);
*/
		}

		/**
		 * 提供公共的注册方法。
		 * @param beanDefinitionRegistry
		 * @param name
		 * @param beanClass
		 */
	private void registerBean(BeanDefinitionRegistry registry,String name,Class<?> beanClass){
		AnnotatedBeanDefinition annotatedBeanDefinition  = new AnnotatedGenericBeanDefinition(beanClass);
		//可以自动生成name
		String beanName = (name != null?name:this.beanNameGenerator.generateBeanName(annotatedBeanDefinition, registry));
		//bean注册的holer类.
		BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(annotatedBeanDefinition, beanName);
		//使用bean注册工具类进行注册.
		BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
	}


	private void registerProxyBean(BeanDefinitionRegistry registry,String name,Class<?> beanClass){
		AnnotatedBeanDefinition annotatedBeanDefinition  = new AnnotatedGenericBeanDefinition(beanClass);
		//可以自动生成name
		String beanName = (name != null?name:this.beanNameGenerator.generateBeanName(annotatedBeanDefinition, registry));
		//bean注册的holer类.
		BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(annotatedBeanDefinition, beanName);
		//使用bean注册工具类进行注册.
		BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
	}







		}