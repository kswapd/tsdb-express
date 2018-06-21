package com.dcits.tsdb.annotations.processors;

import com.dcits.tsdb.annotations.BeanBuilder;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;

/**
 * Created by kongxiangwen on 6/21/18 w:25.
 */

@SupportedAnnotationTypes(
		"com.dcits.tsdb.annotations.BeanBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class BuiderProcessors  extends AbstractProcessor {

	private Filer filer;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		// Filer是个接口，支持通过注解处理器创建新文件
		filer = processingEnv.getFiler();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement element : annotations) {
			if (element.getQualifiedName().toString().equals(BeanBuilder.class.getCanonicalName())) {
				// 创建main方法
				System.out.println(element.getSimpleName().toString()+"--------");
				//element.getClass().getPackage().getName();

				MethodSpec main = MethodSpec.methodBuilder("main")
						.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
						.returns(void.class)
						.addParameter(String[].class, "args")
						.addStatement("$T.out.println($S)", System.class, element.getEnclosedElements().getClass().getPackage().getName().toString())
						.build();
				// 创建HelloWorld类
				TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
						.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
						.addMethod(main)
						.build();

				/*try {
					// 生成 com.example.HelloWorld.java
					JavaFile javaFile = JavaFile.builder("com.example2", helloWorld)
							.addFileComment(" This codes are generated automatically. Do not modify!")
							.build();
					//　生成文件
					javaFile.writeTo(filer);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		}
		return true;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotations = new LinkedHashSet<>();
		annotations.add(BeanBuilder.class.getCanonicalName());
		return annotations;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
}
