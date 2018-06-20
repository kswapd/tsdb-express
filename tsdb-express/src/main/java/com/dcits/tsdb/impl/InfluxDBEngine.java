package com.dcits.tsdb.impl;

import com.dcits.tsdb.interfaces.TSDBEngine;
import com.dcits.tsdb.utils.InfluxDBResultMapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<T> pojoList = null;
		try {
			pojoList = resultMapper.toPOJO(queryResult, clazz);
		}
		catch (RuntimeException e){

		}

		return pojoList;
	}
}
