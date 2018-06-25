package com.dcits.inters;

import com.dcits.tsdb.interfaces.BarInter;
import com.dcits.tsdb.interfaces.FooInter;
import com.dcits.tsdb.interfaces.MyCustomBean;
import com.dcits.tsdb.impl.Cpu;
/**
 * Created by kongxiangwen on 6/24/18 w:26.
 */

@MyCustomBean
public interface BarInterCustom extends BarInter<Cpu> {

	//public String Bar();


}
