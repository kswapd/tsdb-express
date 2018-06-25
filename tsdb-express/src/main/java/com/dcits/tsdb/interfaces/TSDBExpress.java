package com.dcits.tsdb.interfaces;

import java.util.List;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */
public interface TSDBExpress <T>{
	@Deprecated
	void write(Point data);

	@Deprecated
	QueryResult query(String queryLang);


	/**
	 * queries by sql like language
	 * @param queryLang, this is a sql like query language, such as 'select * from cpu limit 10'
	 * @param clazz, bean class
	 * @return	bean list
	 */
	List<T> queryBeans(String queryLang);
	/*List<T> queryBeans(String queryLang, final Class<T> clazz);*/
	/**
	 * insert bean
	 * @param pojo	write bean to database, the bean property must have annotation @column
	 */
	void writeBean(T pojo);





}
