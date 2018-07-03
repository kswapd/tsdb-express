package com.dcits.tsdb.impl;

import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.exceptions.InfluxDBMapperException;
import com.dcits.tsdb.interfaces.CustomRepo;
import com.dcits.tsdb.utils.InfluxDBResultMapper;
import java.beans.Introspector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */

/**
 * Main class responsible for mapping a QueryResult and  a POJO.
 *
 * @author kongxiangwen
 */
//kxw todo add database connection pool support
//@Repository("tsdbRepo")
public class CustomRepoImpl <T> implements CustomRepo<T> {


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


	@Value("${influxdb.enableGzip}")
	private boolean enableGzip;

	@Value("${tsdb.datasource.type}")
	private String dataSourceType;

	@Value("${tsdb.datasource.maxConnectionSize}")
	private int dataSourceMaxConnectionSize;

	private static InfluxDBRepo influxDBengine = null;
	private  InfluxDB influxDB = null;


	//@Resource(name="influxDBWrapper")
	private InfluxDBResultMapper influxDBMapper;

	private static CustomRepoImpl inst = null;
	private Class<T> innerClass;

	public void setInnerClass(Class <T> cls){
		innerClass = cls;
	}

	private static int sCurRepoChooseId = 0;
	private static Properties sRepoProp = null;
	private static boolean sIsInited = false;
	private static List<CustomRepoImpl> sRepoList;
	private static int sMaxRepoNum = 0;
	public static CustomRepoImpl getInstance()
	{

		if(!sIsInited) {
			sRepoList = new ArrayList<CustomRepoImpl>();
			sRepoProp = tryLoadProps();
			sMaxRepoNum = Integer.parseInt(sRepoProp.getProperty("tsdb.datasource.maxConnectionSize", "1"));
			for (int i = 0; i < sMaxRepoNum ;i++) {
				inst = new CustomRepoImpl();
				inst.initByProp(sRepoProp);
				sRepoList.add(inst);
			}
			sIsInited = true;
		}
		inst = sRepoList.get(sCurRepoChooseId);
		sCurRepoChooseId ++;
		if(sCurRepoChooseId > sMaxRepoNum-1){
			sCurRepoChooseId = 0;
		}

		return inst;
	}


	@PreDestroy
	public void Destroy()
	{
		if(influxDB != null) {
			influxDB.close();
		}
	}


	private static Properties tryLoadProps()  {

		InputStream input = null;
		List<String> filenames = null;
		Properties prop = null;

			//InputStream is = null;
			//InputStream is = this.getClassLoader().getResourceAsStream("tsdb.properties");
		InputStream is = CustomRepoImpl.class.getClassLoader().getResourceAsStream("tsdb.properties");
			if(is == null){
				try{

					//InputStream in = this.getClass().getResourceAsStream( "/" );
					InputStream in = CustomRepoImpl.class.getResourceAsStream( "/" );
					BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
					String resource;
					/*filenames = new ArrayList<>();
					while( (resource = br.readLine()) != null ) {
						filenames.add(resource);
					}*/
					while( (resource = br.readLine()) != null ) {
						if(resource.contains(".properties")){
							//is = this.getClass().getClassLoader().getResourceAsStream(resource);
							is = CustomRepoImpl.class.getClassLoader().getResourceAsStream(resource);
							break;
						}

					}

				}catch (IOException e) {
					e.printStackTrace();
				}
			}

			input = is;

		if(is == null){
			throw new InfluxDBMapperException("not find property file for tsdb");
		}


		prop = new Properties();
		try {
			prop.load(input);
		}
		catch (IOException e) {
			throw new InfluxDBMapperException("load property file error");
		}

		return prop;

	}
	@PostConstruct
	public void initByProp(Properties prop)
	{


		influxDBMapper = new InfluxDBResultMapper();


		//Properties prop = tryLoadProps();
		//System.out.println("=====++"+prop.getProperty("influxdb.dbName"));


		this.address = prop.getProperty("influxdb.address");
		this.user = prop.getProperty("influxdb.user", "root");
		this.password = prop.getProperty("influxdb.password", "root");
		this.dbName = prop.getProperty("influxdb.dbName");
		this.maxBatchSize = Integer.parseInt(prop.getProperty("influxdb.maxBatchSize", "10"));
		this.maxBatchInterval = Integer.parseInt(prop.getProperty("influxdb.maxBatchInterval", "30000"));
		this.enableGzip = Boolean.parseBoolean(prop.getProperty("influxdb.enableGzip", "false"));

		this.dataSourceType = prop.getProperty("tsdb.datasource.type", "influxDB");
		this.dataSourceMaxConnectionSize = Integer.parseInt(prop.getProperty("tsdb.datasource.maxConnectionSize", "1"));
		if(!this.dataSourceType.equals("influxDB")){
			throw new IllegalArgumentException("Invalid datasource type:"+this.dataSourceType);
		}

		System.out.println("connecting influxDB addr:" + address);
		influxDB = InfluxDBFactory.connect(address, user, password);
		influxDB.createDatabase(dbName);
		influxDB.enableBatch(maxBatchSize, maxBatchInterval, TimeUnit.MILLISECONDS);
		if(this.enableGzip) {
			influxDB.enableGzip();
		}

	}

	@PostConstruct
	public void initRef()
	{


		influxDBMapper = new InfluxDBResultMapper();


		Properties prop = tryLoadProps();
		//System.out.println("=====++"+prop.getProperty("influxdb.dbName"));


		this.address = prop.getProperty("influxdb.address");
		this.user = prop.getProperty("influxdb.user", "root");
		this.password = prop.getProperty("influxdb.password", "root");
		this.dbName = prop.getProperty("influxdb.dbName");
		this.maxBatchSize = Integer.parseInt(prop.getProperty("influxdb.maxBatchSize", "10"));
		this.maxBatchInterval = Integer.parseInt(prop.getProperty("influxdb.maxBatchInterval", "30000"));
		this.enableGzip = Boolean.parseBoolean(prop.getProperty("influxdb.enableGzip", "false"));

		this.dataSourceType = prop.getProperty("tsdb.datasource.type", "influxDB");
		this.dataSourceMaxConnectionSize = Integer.parseInt(prop.getProperty("tsdb.datasource.maxConnectionSize", "1"));
		if(!this.dataSourceType.equals("influxDB")){
			throw new IllegalArgumentException("Invalid datasource type:"+this.dataSourceType);
		}

		System.out.println("connecting influxDB addr:" + address);
		influxDB = InfluxDBFactory.connect(address, user, password);
		influxDB.createDatabase(dbName);
		influxDB.enableBatch(maxBatchSize, maxBatchInterval, TimeUnit.MILLISECONDS);
		if(this.enableGzip) {
			influxDB.enableGzip();
		}

	}
	@Deprecated
	@Override
	public void write(Point data)
	{

		influxDB.write(dbName, rpName, data);
	}
	@Deprecated
	@Override
	public QueryResult query(String queryLang)
	{


		QueryResult queryResult = influxDB.query(new Query(queryLang, dbName), TimeUnit.MILLISECONDS);
		return queryResult;

	}
	@Deprecated
	@Override
	public List<T> queryBeans(String queryLang)
	{


		QueryResult queryResult = query(queryLang);
		//InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<T> pojoList = null;

		try {
			pojoList = influxDBMapper.toPOJO(queryResult, innerClass);
		}
		catch (RuntimeException e){

		}

		return pojoList;
	}


	@Override
	public List<T> find(String queryLang)
	{


		QueryResult queryResult = query(queryLang);
		//InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<T> pojoList = null;

		try {
			pojoList = influxDBMapper.toPOJO(queryResult, innerClass);
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
	@Deprecated
	@Override
	public  void writeBean(T pojo)
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





	/**
	 * write bean data to influxdb
	 * @param pojo
	 * @param <T>
	 */
	@Override
	public  T save(T pojo)
	{
		Point data = null;
		try {
			data = influxDBMapper.pojoToPoint(pojo);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		write(data);
		return pojo;
	}

	@Override
	public T findLastOne()
	{

		T ret = null;

		String measurementName = getMeasurementName();
		String query = "select * from " + measurementName + " order by time desc limit 1";
		List<T> li = queryBeans(query);
		if(li != null && li.size() > 0){
			ret = li.get(0);
		}
		return ret;
	}

	@Override
	public long count() {

		long num = 0;
		String measurementName = getMeasurementName();
		String queryLang = "select count(*) from " + measurementName;
		QueryResult queryResult = query(queryLang);

		List<List<Object>> obj = queryResult.getResults().get(0).getSeries().get(0).getValues();
		//get(0) is time String
		Object objValue = obj.get(0).get(1);
		/*String strNum = String.valueOf(objValue.get(1));
		Double dnum = Double.parseDouble(strNum);
		num = dnum.longValue();*/
		num = ((Double)objValue).longValue();
		return num;
	}

	private String getMeasurementName()
	{
		String measurementName = null;
		Measurement measure = (Measurement) innerClass.getAnnotation(Measurement.class);
		if(measure != null){
			measurementName = measure.name();
		}
		//if null, use class name.
		if(StringUtils.isEmpty(measurementName)){
			String lastName = ClassUtils.getShortName(innerClass.getName());
			measurementName = Introspector.decapitalize(lastName);

		}
		Objects.requireNonNull(measurementName, "measurementName");
		return measurementName;
	}

	public <T> List<T> findByTime(String queryLang, final Class<T> clazz)
	{
		return null;
	}




}
