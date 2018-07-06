package com.dcits.repo.memory;

import com.dcits.repo.models.Memory;
import com.dcits.tsdb.annotations.CustomRepoDeclared;
import com.dcits.tsdb.annotations.QueryMeasurement;
import com.dcits.tsdb.interfaces.CustomRepo;
import java.util.List;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */

public interface RepoMemory extends CustomRepo<Memory> {

	public List<Memory> findByIpAddrOrderByTimeDescLimit(String ip, int limit);
	public List<Memory> findByIpAddrLimit(String ip, int limit);
	public List<Memory>  findByIpAddrAndTimeBeforeLimit(String ip, String time,int limit);
	public List<Memory>  findByIpAddrAndTimeBeforeOrderByTimeDescLimit(String ip, String time,int limit);
	public List<Memory>   aggregateByIpAddrAndTimeBeforeOrderByTimeDescLimit(String ip, String time,int limit);


	//@QueryMeasurement(name="rp_1d.memory_mean_2m")
	public List<Memory>    aggregateByPercentMeanIpAddrIsAndTimeBeforeGroupByOrderByTimeDescLimit(String ip, String time,String groups, int limit);

	public List<Memory>    aggregateByPercentMeanIpAddrIsAndTimeBeforeOrTimeAfterGroupByOrderByTimeDescLimit(String ip, String time,String groups, String after, int limit);


	public List<Memory>    aggregateByPercentMeanTimeAfterOrIpAddrIsAndTimeBeforeGroupByOrderByTimeDescLimit(String after, String ip, String time,String groups,  int limit);
	public List<Memory>    findByTimeAfterOrIpAddrIsAndTimeBeforeOrderByTimeDescLimit(String after, String ip, String time, int limit);

	public List<Memory> findByIpAddrIsOrderByTimeDescLimit(String ipAddr, int limit);
}
