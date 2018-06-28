package com.dcits.tsdb.utils;

import com.dcits.tsdb.exceptions.SqlConvertorException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.util.StringUtils;

/**
 * Created by kongxiangwen on 6/26/18 w:26.
 */
public class ExecutedMethodParser {



		//SELECT mean("percent") AS "mean_percent" FROM "metrics_db3"."rp_3h"."memory"
		// WHERE time > now() - 1h GROUP BY time(:interval:) FILL(null)

	public static String getInfluxDBSql(String measurementName, Method method, Object[] args)
	{
		String methodName = method.getName();
		String startFindStr = "findBy";
		String startAggregateStr = "aggregateBy";
		String sqlStr = null;
		Objects.requireNonNull(method, "method");
		if(!methodName.startsWith(startFindStr) && !methodName.startsWith(startAggregateStr)){
			throw new SqlConvertorException("Not support this method:"+methodName);
		}
		if(methodName.startsWith(startFindStr)) {
			sqlStr = parseFindSql(measurementName, methodName, args);
		}
		if(methodName.startsWith(startAggregateStr)) {
			sqlStr = parseAggregateSql(measurementName, methodName, args);
		}

		return sqlStr;
	}
	//aggregateByAgeMeanTimeBeforeAndAgeGGroupByTimeOrderByTimeDesc('5m', '1111111')
	private static String parseAggregateSubQuery(String measurementName, StringBuilder methodName){


		String originalStr = methodName.toString();

		ArrayList<String> allPredicates = new ArrayList<String>(Arrays.asList(
				"Mean","Max","Min"));
		Map<String, String> predicateSqlMap = new HashMap<String,String>();
		predicateSqlMap.put("Max", "max");
		predicateSqlMap.put("Min", "min");
		predicateSqlMap.put("Mean", "mean");


		String aggMeasurement = null;
		String aggMethod = null;
		for(String curAgg:allPredicates){
			if(originalStr.contains(curAgg)){
				aggMeasurement = Introspector.decapitalize(originalStr.substring(0, originalStr.indexOf(curAgg)));
				aggMethod = predicateSqlMap.get(curAgg);
				methodName.delete(0, originalStr.indexOf(curAgg)+curAgg.length());
				break;
			}
		}

		String curQuery = "select " + aggMethod + "("+aggMeasurement+") as " +aggMeasurement+ " from " + measurementName + " where ";
		return curQuery;
	}
	//aggregateByAgeMeanTimeBeforeAndAgeGGroupByTimeOrderByTimeDesc('5m', '1111111')
	public static String parseAggregateSql(String measurementName, String methodName, Object[] args)
	//public static String getInfluxDBSql(String measurementName, String methodName, Object[] args)
	{



		String sql = "select * from " + measurementName + " where ";
		//String methodName = method.getName();
		//Objects.requireNonNull(method, "method");
		String startStr = "aggregateBy";
		String andSplit = "And";
		String orSplit = "Or";


		ArrayList<String> allPredicates = new ArrayList<String>(Arrays.asList("LessThan", "GreaterThan", "After",
				"Before", "Not", "Like","OrderBy", "Is", "Limit",
				"Mean","Max","Min", "GroupByTime"));

		//Supported operators: =   equal to <> not equal to != not equal to >   
		// greater than >= greater than or equal to <   less than <= less than or equal to

		Map<String, String> predicateSqlMap = new HashMap<String,String>();
		predicateSqlMap.put("LessThan", "<");
		predicateSqlMap.put("GreaterThan", ">");
		predicateSqlMap.put("After", ">");
		predicateSqlMap.put("Before", "<");
		predicateSqlMap.put("Not", "<>");
		predicateSqlMap.put("LessThan", "<");
		predicateSqlMap.put("OrderBy", "order by");
		predicateSqlMap.put("Is", "=");
		predicateSqlMap.put("Limit", "limit");
		predicateSqlMap.put("Max", "max");
		predicateSqlMap.put("Min", "min");
		predicateSqlMap.put("Mean", "mean");
		predicateSqlMap.put("GroupByTime", "group by time");


		ArrayList<String> subjects = new ArrayList<String>();
		ArrayList<String> predicates = new ArrayList<String>();

		ArrayList<String> allAndSubPreds = new ArrayList<String>();
		ArrayList<String> allOrSubPreds = new ArrayList<String>();
		ArrayList<String> allSubPreds = new ArrayList<String>();
		ArrayList<String> lastSubPreds = new ArrayList<String>();


		String originalSubPred = methodName.substring(startStr.length());
		String orderByStr = null;
		String limitStr = null;



		StringBuilder sb = new StringBuilder(originalSubPred);
		sql = parseAggregateSubQuery(measurementName,sb);
		originalSubPred = sb.toString();

		if(originalSubPred.contains("OrderBy")){

			orderByStr = originalSubPred.substring(originalSubPred.indexOf("OrderBy"), originalSubPred.length());
			originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("OrderBy"));


			if(!orderByStr.contains("Limit")) {
				lastSubPreds.add(orderByStr);
			}else if(orderByStr.contains("Limit")) {

				limitStr = orderByStr.substring(orderByStr.indexOf("Limit"), orderByStr.length());
				String finalOrderByStr = orderByStr.substring(0, orderByStr.indexOf("Limit"));
				lastSubPreds.add(finalOrderByStr);
				lastSubPreds.add(limitStr);
			}




		}else
		if(originalSubPred.contains("Limit")){

			limitStr = originalSubPred.substring(originalSubPred.indexOf("Limit"), originalSubPred.length());
			originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("Limit"));
			limitStr ="Limit";
			lastSubPreds.add(limitStr);
		}


		if(originalSubPred.contains("GroupByTime")){

			originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("GroupByTime"));
			String groupStr ="GroupByTime";
			//lastSubPreds.add(groupStr);
			lastSubPreds.add(0, groupStr);
		}









		if (originalSubPred.contains(andSplit)) {
			allAndSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
			allSubPreds.addAll(allAndSubPreds);
			//for(String perSubPred:allAndSubPreds){


			//}
		}
		else if (originalSubPred.contains(orSplit)) {

			allOrSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
			allSubPreds.addAll(allOrSubPreds);
		}
		else {
			allSubPreds.add(originalSubPred);
		}














		allSubPreds.addAll(lastSubPreds);





		String consSql = sql;
		int curCondNum = 0;
		//subPred: AgeGreaterThan, NameLike, School,  TimeBetween, OrderByNameDesc
		for(String subPred: allSubPreds){
			//if(subPred.contains())
			String curSub = null;
			String curPred = null;
			String orderDirection = null;
			String condStr = "";

			for(String pred:allPredicates){

				if(subPred.contains(pred) && !pred.equals("OrderBy") && !pred.equals("Limit")  && !pred.equals("GroupByTime")){
					curSub = subPred.split(pred)[0];
					curPred = predicateSqlMap.get(pred);
				}else if(subPred.contains(pred) && pred.equals("OrderBy")){
					String [] allSubs = subPred.split(pred);
					curSub = allSubs[1];
					curPred = predicateSqlMap.get(pred);
					orderDirection = "desc";
					if(curSub.contains("Desc")){
						curSub = curSub.substring(0, curSub.length() - 4);
					}else if(curSub.contains("Asc")){
						curSub = curSub.substring(0, curSub.length() - 3);
						orderDirection = "asc";
					}
				}else if(subPred.contains(pred) && pred.equals("Limit")){
					curSub = "Limit";
					curPred = "limit";
				}
				else if(subPred.contains(pred) && pred.equals("GroupByTime")){
					curSub = "GroupByTime";
					curPred = "group by time";
				}
			}
			if(StringUtils.isEmpty(curSub)){
				curSub = subPred;
				curPred = "=";
			}

			if(!curPred.equals("order by") && !curPred.equals("limit") && !curPred.equals("group by time")){
				String varName = Introspector.decapitalize(curSub);
				if(curPred.equals("=")){
					condStr =  "\"" + varName +"\" " + curPred + " '" + String.valueOf(args[curCondNum]) + "'";
				}else{
					if(!varName.equals("time")) {
						condStr = varName + " " + curPred + " " + Double.valueOf(String.valueOf(args[curCondNum]));
					}else{
						String rfcTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
								.format(new Date(Long.valueOf(String.valueOf(args[curCondNum]))));


						condStr = varName + " " + curPred + " " + "'" + rfcTime + "'";
					}
				}
				curCondNum ++;

			}else if(curPred.equals("group by time")){
				//String varName = Introspector.decapitalize(curSub);
				condStr = " " + curPred + "(" + String.valueOf(args[curCondNum]) + ")";
				if(consSql.endsWith("and ")){
					consSql = consSql.substring(0, consSql.length() - 4);
				}
				//consSql += " " + condStr;
				curCondNum ++;
			}else if(curPred.equals("order by")){
				String varName = Introspector.decapitalize(curSub);
				condStr = " " + curPred + " " + varName + " " + orderDirection;

				if(consSql.endsWith("and ")){
					consSql = consSql.substring(0, consSql.length() - 4);
				}
				//consSql += " " + condStr;
				//break;
			}else if(curPred.equals("limit")){
				//String varName = Introspector.decapitalize(curSub);
				condStr = " " + curPred + " " + Long.valueOf(String.valueOf(args[curCondNum]));
				if(consSql.endsWith("and ")){
					consSql = consSql.substring(0, consSql.length() - 4);
				}
				consSql += " " + condStr;
				curCondNum ++;
				break;
			}


			if(curCondNum == 1){
				consSql += condStr;
			}else{
				if(!condStr.contains("order by") && !condStr.contains("group by time")) {
					consSql += " and " + condStr;
				}else{
					consSql += " " + condStr;
				}
			}




		}


		return consSql;



	}

		public static String parseFindSql(String measurementName, String methodName, Object[] args)
		//public static String getInfluxDBSql(String measurementName, String methodName, Object[] args)
		{



			String sql = "select * from " + measurementName + " where ";
			//String methodName = method.getName();
			//Objects.requireNonNull(method, "method");
			String startStr = "findBy";
			String andSplit = "And";
			String orSplit = "Or";


			ArrayList<String> allPredicates = new ArrayList<String>(Arrays.asList("LessThan", "GreaterThan", "After",
					"Before", "Not", "Like","OrderBy", "Is", "Limit"));

			//Supported operators: =   equal to <> not equal to != not equal to >   
			// greater than >= greater than or equal to <   less than <= less than or equal to

			Map<String, String> predicateSqlMap = new HashMap<String,String>();
			predicateSqlMap.put("LessThan", "<");
			predicateSqlMap.put("GreaterThan", ">");
			predicateSqlMap.put("After", ">");
			predicateSqlMap.put("Before", "<");
			predicateSqlMap.put("Not", "<>");
			predicateSqlMap.put("LessThan", "<");
			predicateSqlMap.put("OrderBy", "order by");
			predicateSqlMap.put("Is", "=");
			predicateSqlMap.put("Limit", "limit");


			ArrayList<String> subjects = new ArrayList<String>();
			ArrayList<String> predicates = new ArrayList<String>();

			ArrayList<String> allAndSubPreds = new ArrayList<String>();
			ArrayList<String> allOrSubPreds = new ArrayList<String>();
			ArrayList<String> allSubPreds = new ArrayList<String>();
			ArrayList<String> lastSubPreds = new ArrayList<String>();


			String originalSubPred = methodName.substring(startStr.length());
			String orderByStr = null;
			String limitStr = null;


			/*if(!originalSubPred.contains("OrderBy") && !originalSubPred.contains("Limit")) {
				if (originalSubPred.contains(andSplit)) {
					allAndSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
					allSubPreds.addAll(allAndSubPreds);
					//for(String perSubPred:allAndSubPreds){


					//}
				}
				else if (originalSubPred.contains(orSplit)) {

					allOrSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
					allSubPreds.addAll(allOrSubPreds);
				}
				else {
					allSubPreds.add(originalSubPred);
				}
			}else


			if(originalSubPred.contains("OrderBy")){

				orderByStr = originalSubPred.substring(originalSubPred.indexOf("OrderBy"), originalSubPred.length());
				originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("OrderBy"));




				if (originalSubPred.contains(andSplit)) {
					allAndSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
					allSubPreds.addAll(allAndSubPreds);
				}
				else if (originalSubPred.contains(orSplit)) {

					allOrSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
					allSubPreds.addAll(allOrSubPreds);
				}
				else {
					allSubPreds.add(originalSubPred);
				}




				if(!orderByStr.contains("Limit")) {
					allSubPreds.add(orderByStr);
				}else if(orderByStr.contains("Limit")) {

					limitStr = orderByStr.substring(orderByStr.indexOf("Limit"), orderByStr.length());
					String finalOrderByStr = orderByStr.substring(0, orderByStr.indexOf("Limit"));
					allSubPreds.add(finalOrderByStr);
					allSubPreds.add(limitStr);
				}




			}else
			if(originalSubPred.contains("Limit")){

				limitStr = originalSubPred.substring(originalSubPred.indexOf("Limit"), originalSubPred.length());
				originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("Limit"));




				if (originalSubPred.contains(andSplit)) {
					allAndSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
					allSubPreds.addAll(allAndSubPreds);
				}
				else if (originalSubPred.contains(orSplit)) {

					allOrSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
					allSubPreds.addAll(allOrSubPreds);
				}
				else {
					allSubPreds.add(originalSubPred);
				}
				limitStr ="Limit";
				allSubPreds.add(limitStr);
			}*/


			if(originalSubPred.contains("OrderBy")){

				orderByStr = originalSubPred.substring(originalSubPred.indexOf("OrderBy"), originalSubPred.length());
				originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("OrderBy"));


				if(!orderByStr.contains("Limit")) {
					lastSubPreds.add(orderByStr);
				}else if(orderByStr.contains("Limit")) {

					limitStr = orderByStr.substring(orderByStr.indexOf("Limit"), orderByStr.length());
					String finalOrderByStr = orderByStr.substring(0, orderByStr.indexOf("Limit"));
					lastSubPreds.add(finalOrderByStr);
					lastSubPreds.add(limitStr);
				}




			}else
			if(originalSubPred.contains("Limit")){

				limitStr = originalSubPred.substring(originalSubPred.indexOf("Limit"), originalSubPred.length());
				originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("Limit"));
				limitStr ="Limit";
				lastSubPreds.add(limitStr);
			}


			if (originalSubPred.contains(andSplit)) {
				allAndSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
				allSubPreds.addAll(allAndSubPreds);
				//for(String perSubPred:allAndSubPreds){


				//}
			}
			else if (originalSubPred.contains(orSplit)) {

				allOrSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
				allSubPreds.addAll(allOrSubPreds);
			}
			else {
				allSubPreds.add(originalSubPred);
			}

			allSubPreds.addAll(lastSubPreds);




			String consSql = sql;
			int curCondNum = 0;
			//subPred: AgeGreaterThan, NameLike, School,  TimeBetween, OrderByNameDesc
			for(String subPred: allSubPreds){
				//if(subPred.contains())
				String curSub = null;
				String curPred = null;
				String orderDirection = null;
				String condStr = "";

				for(String pred:allPredicates){

					if(subPred.contains(pred) && !pred.equals("OrderBy") && !pred.equals("Limit")){
						curSub = subPred.split(pred)[0];
						curPred = predicateSqlMap.get(pred);
					}else if(subPred.contains(pred) && pred.equals("OrderBy")){
						String [] allSubs = subPred.split(pred);
						curSub = allSubs[1];
						curPred = predicateSqlMap.get(pred);
						orderDirection = "desc";
						if(curSub.contains("Desc")){
							curSub = curSub.substring(0, curSub.length() - 4);
						}else if(curSub.contains("Asc")){
							curSub = curSub.substring(0, curSub.length() - 3);
							orderDirection = "asc";
						}
					}else if(subPred.contains(pred) && pred.equals("Limit")){
						curSub = "Limit";
						curPred = "limit";
					}
				}
				if(StringUtils.isEmpty(curSub)){
					curSub = subPred;
					curPred = "=";
				}

				if(!curPred.equals("order by") && !curPred.equals("limit")){
					String varName = Introspector.decapitalize(curSub);
					if(curPred.equals("=")){
						condStr =  "\"" + varName +"\" " + curPred + " '" + String.valueOf(args[curCondNum]) + "'";
					}else{
						if(!varName.equals("time")) {
							condStr = varName + " " + curPred + " " + Double.valueOf(String.valueOf(args[curCondNum]));
						}else{
							String rfcTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
									.format(new Date(Long.valueOf(String.valueOf(args[curCondNum]))));


							condStr = varName + " " + curPred + " " + "'" + rfcTime + "'";
						}
					}
					curCondNum ++;

				}else if(curPred.equals("order by")){
					String varName = Introspector.decapitalize(curSub);
					condStr = " " + curPred + " " + varName + " " + orderDirection;

					if(consSql.endsWith("and ")){
						consSql = consSql.substring(0, consSql.length() - 4);
					}
					//consSql += " " + condStr;
					//break;
				}else if(curPred.equals("limit")){
					//String varName = Introspector.decapitalize(curSub);
					condStr = " " + curPred + " " + Long.valueOf(String.valueOf(args[curCondNum]));
					if(consSql.endsWith("and ")){
						consSql = consSql.substring(0, consSql.length() - 4);
					}
					consSql += " " + condStr;
					curCondNum ++;
					break;
				}


				if(curCondNum == 1){
					consSql += condStr;
				}else{
					if(!condStr.contains("order by")) {
						consSql += " and " + condStr;
					}else{
						consSql += " " + condStr;
					}
				}




			}


			return consSql;



		}
}
