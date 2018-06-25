package com.dcits.myrepo;

import com.dcits.tsdb.impl.Cpu;
import com.dcits.tsdb.impl.InfluxDBRepo;
import com.dcits.tsdb.interfaces.CustomRepo;
import com.dcits.tsdb.interfaces.MyCustomBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */

//@Repository("tsdbRepo")
public class RepoCpuDB extends InfluxDBRepo<Cpu> {
}
