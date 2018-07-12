package com.dcits.repo.models;

import com.dcits.tsdb.annotations.EnableRepoInterfaceScan;
import com.dcits.tsdb.annotations.Measurement;
import java.util.concurrent.TimeUnit;

/**
 * Created by kongxiangwen on 6/26/18 w:26.
 */

//@Measurement(name = "memory")
@Measurement(timeUnit = TimeUnit.MILLISECONDS)
public class Memory extends PercentMeasuerment {
}
