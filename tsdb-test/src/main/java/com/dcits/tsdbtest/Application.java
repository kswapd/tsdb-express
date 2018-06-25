package com.dcits.tsdbtest;


import com.dcits.inters.BarInterCustom;
import com.dcits.inters.FooInterCustom;
import com.dcits.inters.FooInterCustomAnother;
import com.dcits.myrepo.RepoCpu;
import com.dcits.tsdb.annotations.EnableRepoInterfaceScan;
import com.dcits.tsdb.impl.Cpu;
import com.dcits.tsdb.impl.InfluxDBRepoReal;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by kongxiangwen on 5/28/18 w:22.
 */

@Configuration
@EnableRepoInterfaceScan("com.dcits.myrepo")
public class Application {

	public static void main(String args[]){
		boolean isStart = true;
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"tsdb.xml"});
		context.start();



		//TSDBExpress express = (TSDBExpress)context.getBean("tsdbRepo");

		//RepoCpu express = (RepoCpu)context.getBean("customRepoImpl");
		//InfluxDBRepoReal express = (InfluxDBRepoReal)context.getBean("tsdbRepo"); okok
		//CustomRepo cr = (CustomRepo)context.getBean(CustomRepo.class);

		RepoCpu express = (RepoCpu)context.getBean("repoCpu");
		//cr.sayHi();

		/*FooInterCustom fooInterCustom = (FooInterCustom)context.getBean("fooInterCustom");
		System.out.println(fooInterCustom.Foo());

		FooInterCustomAnother fooInterCustomAnother = (FooInterCustomAnother)context.getBean("fooInterCustomAnother");
		System.out.println(fooInterCustomAnother.Foo());
*/



		/*
		BarInterCustom barInterCustom = (BarInterCustom)context.getBean("barInterCustom");
		Cpu ccpu = new Cpu();
		ccpu.setHost("dddasdf");
		ccpu.setIdle(90);
		ccpu.setUser(9);
		ccpu.setSystem(1);

		barInterCustom.setT(ccpu);
		System.out.println(barInterCustom.Bar());
		System.out.println(barInterCustom.getT().getHost());
*/

		//return;
		while(isStart) {
			int randIdle = (int)(Math.random()*30);
			int randUser = (int)(Math.random()*20);
			int randSys = (int)(Math.random()*10);
			int randDiskUsed = (int)(Math.random()*50);
			int randDiskFree = (int)(Math.random()*50);

			//Cpu testcpu = CpuBuilder.aCpu().withHost("dd").withIdle(33).withSystem(33).withUser(22).build();
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

			express.writeBean(cpu);


			/*express.write(Point.measurement("disk")
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("used", 80L +randDiskUsed)
					.addField("free", 300L + randDiskFree)
					.tag("host", "kxw_v2")
					.build());*/


			try {
				List<Cpu> pojoList = express.queryBeans("SELECT * FROM cpu WHERE time > now() - 5s order by time desc limit 10");
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
