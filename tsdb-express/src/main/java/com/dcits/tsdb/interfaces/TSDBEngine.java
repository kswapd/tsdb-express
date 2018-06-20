package com.dcits.tsdb.interfaces;

import java.util.List;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */
public interface TSDBEngine {
	void write(Point data);
	QueryResult query(String queryLang);
	<T> List<T> queryPOJOs(String queryLang, final Class<T> clazz);
}
