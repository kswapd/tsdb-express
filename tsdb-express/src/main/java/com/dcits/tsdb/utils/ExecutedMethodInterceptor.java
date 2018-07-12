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
import java.util.TimeZone;
import org.springframework.util.StringUtils;

/**
 * Created by kongxiangwen on 6/26/18 w:26.
 */


//kxw todo ugly interceptor :)
public class ExecutedMethodInterceptor {



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
				"Mean","Max","Min", "Count"));
		Map<String, String> predicateSqlMap = new HashMap<String,String>();
		predicateSqlMap.put("Max", "max");
		predicateSqlMap.put("Min", "min");
		predicateSqlMap.put("Mean", "mean");
		predicateSqlMap.put("Count", "count");

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
		String curQuery = null;
		if(aggMethod.equals("count")) {
			curQuery = "select " + aggMethod + "(" + aggMeasurement + ")" + " from " + measurementName + " where ";

		}else{
			curQuery = "select " + aggMethod + "(" + aggMeasurement + ") as " + aggMeasurement + " from " + measurementName + " where ";
		}
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
				"Mean","Max","Min", "GroupBy"));

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
/*		predicateSqlMap.put("Max", "max");
		predicateSqlMap.put("Min", "min");
		predicateSqlMap.put("Mean", "mean");*/
		predicateSqlMap.put("GroupBy", "group by");


		ArrayList<LogicPart> allSubPreds = new ArrayList<LogicPart>();
		ArrayList<LogicPart> lastSubPreds = new ArrayList<LogicPart>();


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
				lastSubPreds.add(new LogicPart(orderByStr, null));
			}else if(orderByStr.contains("Limit")) {

				limitStr = orderByStr.substring(orderByStr.indexOf("Limit"), orderByStr.length());
				String finalOrderByStr = orderByStr.substring(0, orderByStr.indexOf("Limit"));
				lastSubPreds.add(new LogicPart(finalOrderByStr,null));
				lastSubPreds.add(new LogicPart(limitStr, null));
			}

		}else
		if(originalSubPred.contains("Limit")){

			limitStr = originalSubPred.substring(originalSubPred.indexOf("Limit"), originalSubPred.length());
			originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("Limit"));
			limitStr ="Limit";
			lastSubPreds.add(new LogicPart(limitStr, null));
		}


		if(originalSubPred.contains("GroupBy")){

			originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("GroupBy"));
			String groupStr ="GroupBy";
			//lastSubPreds.add(groupStr);
			lastSubPreds.add(0, new LogicPart(groupStr,null));
		}


		String[] splits = originalSubPred.split(andSplit+"|"+orSplit);
		if(splits.length > 0){
			//first element don't use "and" or "or" to join
			allSubPreds.add(new LogicPart(splits[0], null));
			for(int i = 1; i < splits.length; i ++){
				int refIndex = originalSubPred.indexOf(splits[i]);
				String dd = originalSubPred.substring(refIndex-3, refIndex);
				if(originalSubPred.substring(refIndex-3, refIndex).equals(andSplit)){
					allSubPreds.add(new LogicPart(splits[i], andSplit.toLowerCase()));
				}else if(originalSubPred.substring(refIndex-2, refIndex).equals(orSplit)){
					allSubPreds.add(new LogicPart(splits[i], orSplit.toLowerCase()));
				}
			}
		}else{
			allSubPreds.add(new LogicPart(originalSubPred, null));
		}

		allSubPreds.addAll(lastSubPreds);









		String consSql = sql;
		int curCondNum = 0;
		//subPred: AgeGreaterThan, NameLike, School,  TimeBetween, OrderByNameDesc
			//for(LogicPart lp: allSubPreds){
			for(int index = 0;  index < allSubPreds.size(); index ++){
				LogicPart lp = allSubPreds.get(index);
			String subPred = lp.getSubjectPredicate();
			String logicConjection = lp.getLogicConjection();
			//if(subPred.contains())
			String curSub = null;
			String curPred = null;
			String orderDirection = null;
			String condStr = "";

			for(String pred:allPredicates){

				if(subPred.contains(pred) && !pred.equals("OrderBy") && !pred.equals("Limit")  && !pred.equals("GroupBy")){
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
				else if(subPred.contains(pred) && pred.equals("GroupBy")){
					curSub = "GroupBy";
					curPred = "group by";
				}
			}
			if(StringUtils.isEmpty(curSub)){
				curSub = subPred;
				curPred = "=";
			}

			if(!curPred.equals("order by") && !curPred.equals("limit") && !curPred.equals("group by")){
				String varName = Introspector.decapitalize(curSub);
				if(curPred.equals("=")){
					condStr =  "\"" + varName +"\" " + curPred + " '" + String.valueOf(args[curCondNum]) + "'";
				}else{
					if(!varName.equals("time")) {
						condStr = varName + " " + curPred + " " + Double.valueOf(String.valueOf(args[curCondNum]));
					}else{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
						String rfcTime = sdf.format(new Date(Long.valueOf(String.valueOf(args[curCondNum]))));


						condStr = varName + " " + curPred + " " + "'" + rfcTime + "'";
					}
				}
				curCondNum ++;

			}else if(curPred.equals("group by")){
				//String varName = Introspector.decapitalize(curSub);
				condStr = " " + curPred + " " + String.valueOf(args[curCondNum]);
				if(consSql.endsWith("and ")){
					consSql = consSql.substring(0, consSql.length() - 4);
				}
				if(consSql.endsWith("or ")){
					consSql = consSql.substring(0, consSql.length() - 3);
				}
				//consSql += " " + condStr;
				curCondNum ++;
			}else if(curPred.equals("order by")){
				String varName = Introspector.decapitalize(curSub);
				condStr = " " + curPred + " " + varName + " " + orderDirection;

				if(consSql.endsWith("and ")){
					consSql = consSql.substring(0, consSql.length() - 4);
				}
				if(consSql.endsWith("or ")){
					consSql = consSql.substring(0, consSql.length() - 3);
				}
				//consSql += " " + condStr;
				//break;
			}else if(curPred.equals("limit")){
				//String varName = Introspector.decapitalize(curSub);
				condStr = " " + curPred + " " + Long.valueOf(String.valueOf(args[curCondNum]));
				if(consSql.endsWith("and ")){
					consSql = consSql.substring(0, consSql.length() - 4);
				}
				if(consSql.endsWith("or ")){
					consSql = consSql.substring(0, consSql.length() - 3);
				}
				consSql += " " + condStr;
				curCondNum ++;
				break;
			}


			if(curCondNum == 1){
				consSql += condStr;
			}else{
				if(!condStr.contains("order by") && !condStr.contains("group by")) {
					//consSql += " and " + condStr;
					consSql += " " + allSubPreds.get(index).getLogicConjection().toLowerCase() + " " + condStr;
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



			String sql = "select * from " + measurementName;
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




			ArrayList<LogicPart> allSubPreds = new ArrayList<LogicPart>();
			ArrayList<LogicPart> lastSubPreds = new ArrayList<LogicPart>();


			String originalSubPred = methodName.substring(startStr.length());
			String orderByStr = null;
			String limitStr = null;


			/*if(originalSubPred.contains("OrderBy")){

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
*/





			if(originalSubPred.contains("OrderBy")){

				orderByStr = originalSubPred.substring(originalSubPred.indexOf("OrderBy"), originalSubPred.length());
				originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("OrderBy"));


				if(!orderByStr.contains("Limit")) {
					lastSubPreds.add(new LogicPart(orderByStr, null));
				}else if(orderByStr.contains("Limit")) {

					limitStr = orderByStr.substring(orderByStr.indexOf("Limit"), orderByStr.length());
					String finalOrderByStr = orderByStr.substring(0, orderByStr.indexOf("Limit"));
					lastSubPreds.add(new LogicPart(finalOrderByStr,null));
					lastSubPreds.add(new LogicPart(limitStr, null));
				}

			}else
			if(originalSubPred.contains("Limit")){

				limitStr = originalSubPred.substring(originalSubPred.indexOf("Limit"), originalSubPred.length());
				originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("Limit"));
				limitStr ="Limit";
				lastSubPreds.add(new LogicPart(limitStr, null));
			}


			if(originalSubPred.contains("GroupBy")){

				originalSubPred = originalSubPred.substring(0,originalSubPred.indexOf("GroupBy"));
				String groupStr ="GroupBy";
				//lastSubPreds.add(groupStr);
				lastSubPreds.add(0, new LogicPart(groupStr,null));
			}

			String[] splits = null;
			// have condition
			if(!StringUtils.isEmpty(originalSubPred)) {
				splits = originalSubPred.split(andSplit + "|" + orSplit);

				if(splits != null && splits.length > 0){
					//first element don't use "and" or "or" to join
					allSubPreds.add(new LogicPart(splits[0], null));
					for(int i = 1; i < splits.length; i ++){
						int refIndex = originalSubPred.indexOf(splits[i]);
						String dd = originalSubPred.substring(refIndex-3, refIndex);
						if(originalSubPred.substring(refIndex-3, refIndex).equals(andSplit)){
							allSubPreds.add(new LogicPart(splits[i], andSplit.toLowerCase()));
						}else if(originalSubPred.substring(refIndex-2, refIndex).equals(orSplit)){
							allSubPreds.add(new LogicPart(splits[i], orSplit.toLowerCase()));
						}
					}
				}else{
					allSubPreds.add(new LogicPart(originalSubPred, null));
				}

				sql = sql + " where ";

			}

			allSubPreds.addAll(lastSubPreds);




			String consSql = sql;
			int curCondNum = 0;
			//subPred: AgeGreaterThan, NameLike, School,  TimeBetween, OrderByNameDesc
			//for(String subPred: allSubPreds){
			for(int index = 0;  index < allSubPreds.size(); index ++){
				LogicPart lp = allSubPreds.get(index);
				String subPred = lp.getSubjectPredicate();
				String logicConjection = lp.getLogicConjection();
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
							//String rfcTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
							//		.format(new Date(Long.valueOf(String.valueOf(args[curCondNum]))));

							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
							String rfcTime = sdf.format(new Date(Long.valueOf(String.valueOf(args[curCondNum]))));


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
					if(consSql.endsWith("or ")){
						consSql = consSql.substring(0, consSql.length() - 3);
					}
					//consSql += " " + condStr;
					//break;
				}else if(curPred.equals("limit")){
					//String varName = Introspector.decapitalize(curSub);
					condStr = " " + curPred + " " + Long.valueOf(String.valueOf(args[curCondNum]));
					if(consSql.endsWith("and ")){
						consSql = consSql.substring(0, consSql.length() - 4);
					}
					if(consSql.endsWith("or ")){
						consSql = consSql.substring(0, consSql.length() - 3);
					}
					consSql += " " + condStr;
					curCondNum ++;
					break;
				}


				if(curCondNum == 1){
					consSql += condStr;
				}else{
					if(!condStr.contains("order by")) {
						//consSql += " and " + condStr;
						consSql += " " + allSubPreds.get(index).getLogicConjection().toLowerCase() + " " + condStr;
					}else{
						consSql += " " + condStr;
					}
				}




			}


			return consSql;



		}
}
