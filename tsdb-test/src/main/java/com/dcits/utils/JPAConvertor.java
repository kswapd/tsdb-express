package com.dcits.utils;

import com.dcits.tsdb.exceptions.SqlConvertorException;
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
					"Before", "Not", "Like","OrderBy"));

			//Supported operators: =   equal to <> not equal to != not equal to >   
			// greater than >= greater than or equal to <   less than <= less than or equal to

			Map<String, String> predicateSqlMap = new HashMap<String,String>();
			predicateSqlMap.put("LessThan", "<");
			predicateSqlMap.put("GreaterThan", ">");
			predicateSqlMap.put("After", ">");
			predicateSqlMap.put("Before", "<");
			predicateSqlMap.put("Not", "");
			predicateSqlMap.put("LessThan", "<");


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
			if(originalSubPred.contains(andSplit)){
				allAndSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
				allSubPreds.addAll(allAndSubPreds);
				//for(String perSubPred:allAndSubPreds){


				//}
			}else if(originalSubPred.contains(orSplit)){

				allOrSubPreds.addAll(Arrays.asList(originalSubPred.split(andSplit)));
				allSubPreds.addAll(allOrSubPreds);
			}else{
				allSubPreds.add(originalSubPred);
			}


			//subPred: AgeGreaterThan, NameLike, School,  TimeBetween, OrderByNameDesc
			for(String subPred: allSubPreds){
				//if(subPred.contains())
				String curSub = null;
				String curPred = null;
				String orderDirection = null;
				String condStr = null;
				int curCondNum = 0;
				for(String pred:allPredicates){

					if(subPred.contains(pred) && !pred.equals("OrderBy")){
						curSub = subPred.split(pred)[0];
						curPred = subPred;
					}else if(subPred.contains(pred) && pred.equals("OrderBy")){
						curSub = subPred.split(pred)[0];
						curPred = subPred;
						orderDirection = "Desc";
						if(curSub.contains("Desc")){
							curSub = curSub.substring(0, curSub.length() - 4);
						}else if(curSub.contains("Asc")){
							curSub = curSub.substring(0, curSub.length() - 3);
							orderDirection = "Asc";
						}
					}
				}
				if(StringUtils.isEmpty(curSub)){
					curSub = subPred;
				}
				if(curCondNum == 0){
					//condStr = curSub +
				}

				curCondNum ++;

			}
















			return sql;

		}
}
