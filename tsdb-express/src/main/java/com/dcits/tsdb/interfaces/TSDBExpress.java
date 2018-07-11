package com.dcits.tsdb.interfaces;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */
public interface TSDBExpress <T>{
	@Deprecated
	void write(Point data);
	@Deprecated
	void write(Point data, String retentionPolicy);

	@Deprecated
	QueryResult query(String queryLang);

	@Deprecated
	QueryResult query(String queryLang, TimeUnit tu);

	/**
	 * queries by sql like language
	 * @param queryLang, this is a sql like query language, such as 'select * from cpu limit 10'
	 * @return	bean list
	 */
	@Deprecated
	List<T> queryBeans(String queryLang);


	/**
	 * queries by sql like language
	 * @param queryLang, this is a sql like query language, such as 'select * from cpu limit 10'
	 * @return	bean list
	 */
	List<T> find(String queryLang);


	/**
	 * queries by sql like language
	 * @param queryLang, this is a sql like query language, such as 'select * from cpu limit 10'
	 * @return	bean list
	 */
	List<T> find(String queryLang, TimeUnit tu);


	/*List<T> queryBeans(String queryLang, final Class<T> clazz);*/
	/**
	 * insert bean
	 * @param pojo	write bean to database, the bean property must have annotation @column
	 */
	@Deprecated
	void writeBean(T pojo);


	/**
	 * get last tsdb bean from measurement
	 * @return
	 */
	T findLastOne();

	/**
	 * count all num
	 * @return
	 */
	long count();


	/**
	 * save one bean
	 * @param pojo
	 * @return
	 */
	T save(T pojo);






}
