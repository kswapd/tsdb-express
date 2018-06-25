package com.dcits.tsdb.impl;

import com.dcits.tsdb.interfaces.CustomRepo;
import com.dcits.tsdb.utils.InfluxDBResultMapper;
import java.util.List;
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
import org.springframework.stereotype.Repository;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */

/**
 * Main class responsible for mapping a QueryResult and  a POJO.
 *
 * @author kongxiangwen
 */
@Repository("tsdbRepo")
public class InfluxDBRepoReal  {


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

	private static InfluxDBRepoReal influxDBengine = null;
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


	public void write(Point data)
	{

		influxDB.write(dbName, rpName, data);
	}


	public QueryResult query(String queryLang)
	{
		QueryResult queryResult = influxDB.query(new Query(queryLang, dbName));
		return queryResult;

	}

	public <T> List<T> queryBeans(String queryLang, final Class<T> clazz)
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


	/**
	 * write bean data to influxdb
	 * @param pojo
	 * @param <T>
	 */

	public  <T> void writeBean(T pojo)
	{
		Point data = null;
		try {
			data = influxDBMapper.pojoToPoint(pojo);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		write(data);
	}



	public <T> List<T> findByTime(String queryLang, final Class<T> clazz)
	{
		return null;
	}




}
