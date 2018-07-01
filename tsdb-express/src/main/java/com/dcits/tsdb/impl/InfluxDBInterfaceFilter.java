package com.dcits.tsdb.impl;

import com.dcits.tsdb.interfaces.TSDBExpress;
import java.io.IOException;
import java.lang.reflect.Type;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;


/**
 * Created by kongxiangwen on 6/29/18 w:26.
 */

public class InfluxDBInterfaceFilter  implements TypeFilter {

	private Class <? extends TSDBExpress> dbClass;
	public InfluxDBInterfaceFilter(Class<? extends TSDBExpress> tsdbClass) {
		Assert.notNull(tsdbClass, "Class type must not be null");
		this.dbClass = tsdbClass;
	}

	public final boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		return this.match(metadataReader.getClassMetadata());
	}
	protected boolean match(ClassMetadata metadata) {

		boolean isMatch = false;
		String repoName = ClassUtils.getShortName(this.dbClass.getName());
		Class<?> p = null;
		try {
			p = Class.forName(metadata.getClassName());
		}
		catch (ClassNotFoundException e) {
			return isMatch;
		}

		//tp example:com.dcits.tsdb.interfaces.CustomRepo<com.dcits.repo.models.Memory>
		Type []tp = p.getGenericInterfaces();
		if( tp!=null && tp.length>0 ) {
			for (Type t : tp) {
				if (t.toString().contains(repoName)) {
					isMatch = true;
					break;
				}
			}
		}
		return isMatch;

	}
}