package com.dcits.tsdb.utils;

import com.dcits.tsdb.exceptions.SqlConvertorException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.util.StringUtils;

/**
 * Created by kongxiangwen on 6/26/18 w:26.
 */
public class JPAConvertor {


	public static String getInfluxDBSqlFromStr(String measurementName, String methodName, Object[] args)
	{



		String sql = "select * from " + measurementName + " where ";
		//String methodName = method.getName();
		//Objects.requireNonNull(method, "method");
		String startStr = "findBy";
		String andSplit = "And";
		String orSplit = "Or";


		ArrayList<String> allPredicates = new ArrayList<String>(Arrays.asList("LessThan", "GreaterThan", "After",
				"Before", "Not", "Like","OrderBy", "Is"));

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


		ArrayList<String> subjects = new ArrayList<String>();
		ArrayList<String> predicates = new ArrayList<String>();

		ArrayList<String> allAndSubPreds = new ArrayList<String>();
		ArrayList<String> allOrSubPreds = new ArrayList<String>();
		ArrayList<String> allSubPreds = new ArrayList<String>();
		if(!methodName.startsWith(startStr)){
			throw new SqlConvertorException("Not support this method:"+methodName);
		}

		String originalSubPred = methodName.substring(startStr.length());


		//originalSubPred: AgeGreaterThanAgeLessThanAndNameLike
		if(!originalSubPred.contains("OrderBy")) {
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
		}else{

			String orderByStr = originalSubPred.substring(originalSubPred.indexOf("OrderBy"), originalSubPred.length());
			String subOrderByStr = originalSubPred.substring(0,originalSubPred.indexOf("OrderBy"));
			if (subOrderByStr.contains(andSplit)) {
				allAndSubPreds.addAll(Arrays.asList(subOrderByStr.split(andSplit)));
				allSubPreds.addAll(allAndSubPreds);
				//for(String perSubPred:allAndSubPreds){


				//}
			}
			else if (subOrderByStr.contains(orSplit)) {

				allOrSubPreds.addAll(Arrays.asList(subOrderByStr.split(andSplit)));
				allSubPreds.addAll(allOrSubPreds);
			}
			else {
				allSubPreds.add(subOrderByStr);
			}

			allSubPreds.add(orderByStr);

		}

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

				if(subPred.contains(pred) && !pred.equals("OrderBy")){
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
				}
			}
			if(StringUtils.isEmpty(curSub)){
				curSub = subPred;
				curPred = "=";
			}
			if(!curPred.equals("order by")){
				String varName = Introspector.decapitalize(curSub);
				if(curPred.equals("=")){
					condStr =  "\"" + varName +"\" " + curPred + " '" + String.valueOf(args[curCondNum]) + "'";
				}else{
					if(!varName.equals("time")) {
						condStr = varName + " " + curPred + " " + Double.valueOf(String.valueOf(args[curCondNum]));
					}else{
						condStr = varName + " " + curPred + " " + Long.valueOf(String.valueOf(args[curCondNum]));
					}
				}

			}else{
				String varName = Introspector.decapitalize(curSub);
				condStr = " " + curPred + " " + varName + " " + orderDirection;

				if(consSql.endsWith("and ")){
					consSql = consSql.substring(0, consSql.length() - 4);
				}
				consSql += " " + condStr;
				break;
			}


			if(curCondNum == 0){
				consSql += condStr;
			}else{
				consSql += " and " + condStr;
			}

			curCondNum ++;


		}


		return consSql;



	}

		//SELECT mean("percent") AS "mean_percent" FROM "metrics_db3"."rp_3h"."memory"
		// WHERE time > now() - 1h GROUP BY time(:interval:) FILL(null)
		public static String getInfluxDBSql(String measurementName, Method method, Object[] args)
		{



			String sql = "select * from " + measurementName + " where ";
			String methodName = method.getName();
			Objects.requireNonNull(method, "method");
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
			if(!methodName.startsWith(startStr)){
				throw new SqlConvertorException("Not support this method:"+methodName);
			}

			String originalSubPred = methodName.substring(startStr.length());
			String orderByStr = null;
			String limitStr = null;

			//originalSubPred: AgeGreaterThanAgeLessThanAndNameLike
			if(!originalSubPred.contains("OrderBy") && !originalSubPred.contains("Limit")) {
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
				String subOrderByStr = originalSubPred.substring(0,originalSubPred.indexOf("OrderBy"));
				if (subOrderByStr.contains(andSplit)) {
					allAndSubPreds.addAll(Arrays.asList(subOrderByStr.split(andSplit)));
					allSubPreds.addAll(allAndSubPreds);
					//for(String perSubPred:allAndSubPreds){


					//}
				}
				else if (subOrderByStr.contains(orSplit)) {

					allOrSubPreds.addAll(Arrays.asList(subOrderByStr.split(andSplit)));
					allSubPreds.addAll(allOrSubPreds);
				}
				else {
					allSubPreds.add(subOrderByStr);
				}






				if(!orderByStr.contains("Limit")) {
					allSubPreds.add(orderByStr);
				}else if(orderByStr.contains("Limit")) {

					limitStr = orderByStr.substring(orderByStr.indexOf("Limit"), orderByStr.length());
					String finalOrderByStr = orderByStr.substring(0, orderByStr.indexOf("Limit"));
					allSubPreds.add(finalOrderByStr);
					allSubPreds.add(limitStr);
				}

			}




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
							condStr = varName + " " + curPred + " " + Long.valueOf(String.valueOf(args[curCondNum]));
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
					consSql += " and " + condStr;
				}




			}


			return consSql;



		}
}
