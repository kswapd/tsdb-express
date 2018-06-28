package com.dcits.repo.interfaces;

import com.dcits.repo.models.Cpu;
import com.dcits.repo.models.Memory;
import com.dcits.tsdb.annotations.CustomRepoDeclared;
import com.dcits.tsdb.interfaces.CustomRepo;
import java.util.List;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */

@CustomRepoDeclared
public interface RepoMemory extends CustomRepo<Memory> {

	public List<Memory> findByIpAddrOrderByTimeDescLimit(String ip, int limit);
	public List<Memory> findByIpAddrLimit(String ip, int limit);
}
