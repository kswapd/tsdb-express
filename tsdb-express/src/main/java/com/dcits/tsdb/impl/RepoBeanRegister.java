package com.dcits.tsdb.impl;

import com.dcits.tsdb.annotations.BeanMethodBuilder;
import com.dcits.tsdb.annotations.EnableRepoInterfaceScan;
import com.dcits.tsdb.annotations.CustomRepoDeclared;
import com.dcits.tsdb.interfaces.CustomRepo;
import java.beans.Introspector;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.util.ClassUtils;

/**
 * Created by kongxiangwen on 6/23/18 w:25.
 */

public class RepoBeanRegister implements ImportBeanDefinitionRegistrar,EnvironmentAware/*,
		ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, BeanFactoryAware*/ {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(EnableRepoInterfaceScan.class.getCanonicalName());
		if (annotationAttributes != null) {
			String[] basePackages = (String[]) annotationAttributes.get("value");
			if (basePackages.length == 0){
				basePackages = new String[]{((StandardAnnotationMetadata) metadata).getIntrospectedClass().getPackage().getName()};
			}
			// using these packages, scan for interface annotated with CustomRepoDeclared
			ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false, environment){
				@Override
				protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
					AnnotationMetadata metadata = beanDefinition.getMetadata();
					if(metadata.isIndependent() && metadata.isInterface())
					{
						//System.out.println("get interface:"+beanDefinition.getMetadata().getClassName());
					}
					return metadata.isIndependent() && metadata.isInterface();
				}
			};
			//AnnotationTypeFilter
			//provider.addIncludeFilter(new AnnotationTypeFilter(CustomRepoDeclared.class));
			//provider.addExcludeFilter(new AssignableTypeFilter(CustomRepo.class));

			//RegexPatternTypeFilter
			//Pattern p = Pattern.compile(".*Repo.*");
			//provider.addIncludeFilter(new RegexPatternTypeFilter(p));

			//AnnotationTypeFilter
			//provider.addExcludeFilter(new AnnotationTypeFilter(BeanMethodBuilder.class));

			// Or use custom defined filter
			provider.addIncludeFilter(new InfluxDBInterfaceFilter(CustomRepo.class));
			for (String basePackage : basePackages) {
				for (BeanDefinition beanDefinition : provider.findCandidateComponents(basePackage)) {
					Class<?> clz = null;
						try {
							clz = Class.forName(beanDefinition.getBeanClassName());
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
					//System.out.println("Defining object:"+beanShortName);
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
