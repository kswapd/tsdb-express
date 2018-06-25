package com.dcits.tsdb.impl;

import com.dcits.tsdb.annotations.EnableRepoInterfaceScan;
import com.dcits.tsdb.interfaces.CustomRepo;
import com.dcits.tsdb.interfaces.MyCustomBean;
import java.beans.Introspector;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * Created by kongxiangwen on 6/23/18 w:25.
 */

public class RepoBeanRegister implements ImportBeanDefinitionRegistrar,EnvironmentAware,BeanFactoryAware/*,
		ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, BeanFactoryAware*/ {


	private CustomRepoImpl repo = null;
	private ConfigurableListableBeanFactory beanFactory;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			System.out.println("++++++++++");
			this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;

		}

		//cfg.postProcessBeanFactory(beanFactory);

	}

	private void CreateRepo()
	{

	}
	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		System.out.println("------------");

/*

		System.out.println("====="+environment.getProperty("influxdb.dbName"));
		System.out.println("JAVA_HOME:" + environment.getProperty("JAVA_HOME"));

*/


		Properties prop = new Properties();
		InputStream input = null;

		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("tsdb.properties")) {
			input = is;
			prop.load(input);
		}catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("====="+prop.getProperty("influxdb.dbName"));
		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(EnableRepoInterfaceScan.class.getCanonicalName());
		if (annotationAttributes != null) {
			String[] basePackages = (String[]) annotationAttributes.get("value");
			if (basePackages.length == 0){
				basePackages = new String[]{((StandardAnnotationMetadata) metadata).getIntrospectedClass().getPackage().getName()};
			}
			// using these packages, scan for interface annotated with MyCustomBean
			ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false, environment){
				@Override
				protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
					AnnotationMetadata metadata = beanDefinition.getMetadata();
					if(metadata.isIndependent() && metadata.isInterface())
					{
						System.out.println("get interface:"+beanDefinition.getMetadata().getClassName());
					}
					return metadata.isIndependent() && metadata.isInterface();
				}
			};
			provider.addIncludeFilter(new AnnotationTypeFilter(MyCustomBean.class));

			// Scan all packages
			for (String basePackage : basePackages) {
				System.out.println(basePackage);
				for (BeanDefinition beanDefinition : provider.findCandidateComponents(basePackage)) {
					System.out.println(beanDefinition.getBeanClassName());
					Class<?> clz = null;
						try {
							clz = Class.forName(beanDefinition.getBeanClassName());
							System.out.println("------" + clz.toString());
							for (Type t : clz.getGenericInterfaces()) {
								System.out.println(t);
							}
						}catch (ClassNotFoundException e) {
							e.printStackTrace();
						}

					String lastName = ClassUtils.getShortName(beanDefinition.getBeanClassName());
					String beanShortName = Introspector.decapitalize(lastName);
					BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clz);
					GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();

					definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
					definition.setBeanClass(RepoProxyFactory.class);
					definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
					// 注册bean名,一般为类名首字母小写
					registry.registerBeanDefinition(beanShortName, definition);
				}
			}
		}
	}
	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
	private void registerProxyBean(BeanDefinitionRegistry registry,String name,Class<?> beanClass){
		AnnotatedBeanDefinition annotatedBeanDefinition  = new AnnotatedGenericBeanDefinition(beanClass);
		//可以自动生成name
		String beanName = (name != null?name:this.beanNameGenerator.generateBeanName(annotatedBeanDefinition, registry));
		//bean注册的holer类.
		BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(annotatedBeanDefinition, beanName);
		//使用bean注册工具类进行注册.
		BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
	}

	private Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		System.out.println("......"+environment.getProperty("influxdb.address"));

	}

	/**
	 * 创建动态代理
	 *
	 * @param annotatedBeanDefinition
	 * @return
	 */
	/*private Object createProxy(AnnotatedBeanDefinition annotatedBeanDefinition) {
		try {
			AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
			Class<?> target = Class.forName(annotationMetadata.getClassName());
			InvocationHandler invocationHandler = createInvocationHandler();
			Object proxy = Proxy.newProxyInstance(HTTPRequest.class.getClassLoader(), new Class[]{target}, invocationHandler);
			return proxy;
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		}
		return null;
	}
*/
	/**
	 * 创建InvocationHandler，将方法调用全部代理给DemoHttpHandler
	 *
	 * @return
	 */
	/*private InvocationHandler createInvocationHandler() {
		return new InvocationHandler() {
			private DemoHttpHandler demoHttpHandler = new DemoHttpHandler();

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

				return demoHttpHandler.handle(method);
			}
		};
	}*/

}
