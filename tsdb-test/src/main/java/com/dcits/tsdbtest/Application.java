package com.dcits.tsdbtest;

import com.dcits.tsdb.impl.InfluxDBEngine;
import com.dcits.tsdb.interfaces.TSDBEngine;
import com.dcits.tsdbtest.Cpu;
import com.dcits.tsdbtest.CpuBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.influxdb.dto.Point;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by kongxiangwen on 5/28/18 w:22.
 */
public class Application {

	public static void main(String args[]){
		boolean isStart = true;
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"tsdb.xml"});
		context.start();
		TSDBEngine engine = (InfluxDBEngine)context.getBean("tsdbEngine");

		while(isStart) {
			int randIdle = (int)(Math.random()*30);
			int randUser = (int)(Math.random()*20);
			int randSys = (int)(Math.random()*10);
			int randDiskUsed = (int)(Math.random()*50);
			int randDiskFree = (int)(Math.random()*50);

			Cpu testcpu = CpuBuilder.aCpu().withHost("dd").withIdle(33).withSystem(33).withUser(22).build();
			/*Point.Builder pp = Point.measurement("cpu");
			pp = pp.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("idle", 90L + randIdle)
					.addField("user", 9L + randUser)
					.addField("system", 1L+randSys)
					.tag("host", "kxw_v3");
			engine.write(pp.build());*/


			Cpu cpu = new Cpu();
			cpu.setHost("kxw_host");
			cpu.setIdle(90 + randIdle);
			cpu.setUser(9 + randUser);
			cpu.setSystem(1 + randSys);
			engine.writePOJO(cpu);


			engine.write(Point.measurement("disk")
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("used", 80L +randDiskUsed)
					.addField("free", 300L + randDiskFree)
					.tag("host", "kxw_v2")
					.build());
			try {
				List<Cpu> pojoList = engine.queryPOJOs("SELECT * FROM cpu WHERE time > now() - 5s order by time desc limit 10", Cpu.class);
				for(Cpu c:pojoList){
					System.out.println(c.toString());
				}
				Thread.sleep(1000);
			}
			catch (RuntimeException e){
				System.out.println(e.getMessage());
			}
			catch(InterruptedException e){
				isStart = false;
				System.out.println(e.getMessage());
			}

		}
	}

}
