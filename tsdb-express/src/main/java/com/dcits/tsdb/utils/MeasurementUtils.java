package com.dcits.tsdb.utils;

import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.annotations.QueryMeasurement;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Created by kongxiangwen on 7/11/18 w:28.
 */
public class MeasurementUtils {

	/**
	 * set time unit from Measurement annotation
	 * @param clazz
	 * @return
	 */
	public  static  TimeUnit getTimeUnit(final Class<?> clazz) {
		TimeUnit tu = TimeUnit.MILLISECONDS;
		Measurement measure = (Measurement) clazz.getAnnotation(Measurement.class);
		if(measure != null){
			tu = measure.timeUnit();
		}

		Objects.requireNonNull(tu, "Measurement.TimeUnit");
		return tu;
	}

	/**
	 * set measurement from Measurement annotation
	 * @param clazz
	 * @return
	 */

	public static String getMeasurementName(final Class<?> clazz) {
		String measurementName = null;
		Measurement measure = (Measurement) clazz.getAnnotation(Measurement.class);
		if(measure != null){
			measurementName = measure.name();
		}
		//if null, use class name.
		if(StringUtils.isEmpty(measurementName)){
			String lastName = ClassUtils.getShortName(clazz.getName());
			measurementName = Introspector.decapitalize(lastName);

		}
		Objects.requireNonNull(measurementName, "measurementName");
		return measurementName;
	}


	/**
	 *
	 * @param method
	 * @return
	 */
	public static String getQueryMeasurementName(Method method)
	{

		String queryMeasurementName = null;
		QueryMeasurement measure = (QueryMeasurement) method.getAnnotation(QueryMeasurement.class);
		if(measure != null){
			queryMeasurementName = measure.name();
		}
		//if null, return null.
		return queryMeasurementName;
	}


	/**
	 *
	 * @param method
	 * @return
	 */
	public static TimeUnit getQueryTimeUnit(Method method) {
		TimeUnit tu = TimeUnit.MILLISECONDS;
		QueryMeasurement measure = (QueryMeasurement) method.getAnnotation(QueryMeasurement.class);
		if(measure != null){
			tu = measure.timeUnit();
		}
		Objects.requireNonNull(tu, "Measurement.TimeUnit");
		return tu;
	}
}
