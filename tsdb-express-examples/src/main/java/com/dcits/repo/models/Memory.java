package com.dcits.repo.models;

import com.dcits.tsdb.annotations.EnableRepoInterfaceScan;
import com.dcits.tsdb.annotations.Measurement;

/**
 * Created by kongxiangwen on 6/26/18 w:26.
 */

//@Measurement(name = "memory")
@Measurement
public class Memory extends PercentMeasuerment {
}
