package com.dcits.tsdb.impl;

import com.dcits.tsdb.annotations.Column;
import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.annotations.Tag;
import com.dcits.tsdb.interfaces.TSDBEngine;
import com.dcits.tsdb.utils.InfluxDBResultMapper;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */

@Repository("tsdbEngine")
public class InfluxDBEngine implements TSDBEngine{


	@Value("${influxdb.address}")
	private String address;
	@Value("${influxdb.user}")
	private String user;
	@Value("${influxdb.password}")
	private String password;

	@Value("${influxdb.dbName}")
	private String dbName;
	@Value("${influxdb.rpName}")
	private String rpName;
	@Value("${influxdb.maxBatchSize}")
	private int maxBatchSize;
	@Value("${influxdb.maxBatchInterval}")
	private int maxBatchInterval;

	private static InfluxDBEngine influxDBengine = null;
	private  InfluxDB influxDB = null;


	@Resource(name="influxDBWrapper")
	private InfluxDBResultMapper influxDBMapper;

	@PreDestroy
	public void Destroy()
	{
		if(influxDB != null) {
			influxDB.close();
		}
	}
	@PostConstruct
	public void init()
	{


		System.out.println(address);
		influxDB = InfluxDBFactory.connect(address, user, password);
		influxDB.createDatabase(dbName);
		influxDB.enableBatch(maxBatchSize, maxBatchInterval, TimeUnit.MILLISECONDS);
	}

	@Override
	public void write(Point data)
	{

		influxDB.write(dbName, rpName, data);
	}

	@Override
	public QueryResult query(String queryLang)
	{
		QueryResult queryResult = influxDB.query(new Query(queryLang, dbName));
		return queryResult;

	}
	@Override
	public <T> List<T> queryPOJOs(String queryLang, final Class<T> clazz)
	{


		QueryResult queryResult = query(queryLang);
		//InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<T> pojoList = null;
		try {
			pojoList = influxDBMapper.toPOJO(queryResult, clazz);
		}
		catch (RuntimeException e){

		}

		return pojoList;
	}


	/*
	engine.write(Point.measurement("cpu")
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("idle", 90L + randIdle)
					.addField("user", 9L + randUser)
					.addField("system", 1L+randSys)
					.tag("host", "kxw_v2")
					.build());
	 */
	@Override
	public <T> void writePOJO(T pojo)
	{
		//return null;

		Point data = null;
		try {
			data = pojoToPoint(pojo);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		write(data);
	}

	private <T> Point pojoToPoint(T pojo) throws IllegalAccessException {
		Point p = null;
		Class<?> clazz = pojo.getClass();

		String measurementName = ((Measurement) clazz.getAnnotation(Measurement.class)).name();
		Objects.requireNonNull(measurementName, "measurementName");

		Point.Builder pointBuilder = Point.measurement(measurementName);
		//p = Point.measurement("cpu")
		//Object value = new Object();
		for(Field field : clazz.getDeclaredFields()){
			Class<?> fieldType = field.getType();
			Column colAnnotation = field.getAnnotation(Column.class);



			/*if (colAnnotation != null) {

			//	if(field.get)
				pointBuilder.addField(colAnnotation.name(),);
			}

			Tag tagAnnotation = field.getAnnotation(Tag.class);*/
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			if (colAnnotation != null) {

				Object value = field.get(pojo);
				if (String.class.isAssignableFrom(fieldType)) {
					if (value instanceof String) {
						pointBuilder.addField(colAnnotation.name(), String.valueOf(value));
					}
				}


				if (double.class.isAssignableFrom(fieldType)) {
					if (value instanceof Double) {
						pointBuilder.addField(colAnnotation.name(), ((Double) value).doubleValue());
					}
				}


				if (long.class.isAssignableFrom(fieldType)) {
					if (value instanceof Long) {
						pointBuilder.addField(colAnnotation.name(), ((Long) value).longValue());
					}
				}
				if (int.class.isAssignableFrom(fieldType)) {
					if (value instanceof Integer) {
						pointBuilder.addField(colAnnotation.name(), ((Integer) value).intValue());
					}
				}
				if (boolean.class.isAssignableFrom(fieldType)) {
					if (value instanceof Boolean) {
						//field.setBoolean(obj, Boolean.valueOf(String.valueOf(value)).booleanValue());
						pointBuilder.addField(colAnnotation.name(), Boolean.valueOf(String.valueOf(value)).booleanValue());
					}
				}


				if (Double.class.isAssignableFrom(fieldType)) {
					if (value instanceof Double) {
						//field.set(obj, value);
						pointBuilder.addField(colAnnotation.name(), (Double) value);
					}

				}
				if (Long.class.isAssignableFrom(fieldType)) {
					if (value instanceof Long) {
						pointBuilder.addField(colAnnotation.name(), Long.valueOf(((Double) value).longValue()));
					}
				}
				if (Integer.class.isAssignableFrom(fieldType)) {
					if (value instanceof Integer) {
						pointBuilder.addField(colAnnotation.name(), Integer.valueOf(((Integer) value).intValue()));
					}
				}
				if (Boolean.class.isAssignableFrom(fieldType)) {
					if (value instanceof Boolean) {
						pointBuilder.addField(colAnnotation.name(), Boolean.valueOf(String.valueOf(value)));
					}
				}




			}



			Tag tagAnnotation = field.getAnnotation(Tag.class);

			if (tagAnnotation != null) {
				Object value = field.get(pojo);
				if (String.class.isAssignableFrom(fieldType)) {
					if (value instanceof String) {
						pointBuilder.tag(tagAnnotation.name(), String.valueOf(value));
					}
				}
			}


		}
		pointBuilder = pointBuilder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);

		return pointBuilder.build();
	}

}
