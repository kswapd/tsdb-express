package com.dcits.tsdbtest;


import com.dcits.repo.interfaces.RepoCpu;
import com.dcits.repo.interfaces.RepoDisks;
import com.dcits.repo.models.Cpu;
import com.dcits.repo.models.Disk;
import com.dcits.tsdb.annotations.EnableRepoInterfaceScan;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by kongxiangwen on 5/28/18 w:22.
 */

@EnableRepoInterfaceScan("com.dcits.repo.interfaces")
public class Application {

	public static void main(String args[]){
		boolean isStart = true;
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"tsdb.xml"});
		context.start();

		//InfluxDBRepoReal express = (InfluxDBRepoReal)context.getBean("tsdbRepo"); okok
		RepoCpu cpuExpress = (RepoCpu)context.getBean("repoCpu");
		RepoDisks diskExpress = (RepoDisks)context.getBean("repoDisks");

		while(isStart) {
			int randIdle = (int)(Math.random()*30);
			int randUser = (int)(Math.random()*20);
			int randSys = (int)(Math.random()*10);
			int randDiskUsed = (int)(Math.random()*50);
			int randDiskFree = (int)(Math.random()*50);



			Cpu cpu = new Cpu();
			cpu.setHost("kxw_host");
			cpu.setIdle(90 + randIdle);
			cpu.setUser(9 + randUser);
			cpu.setSystem(1 + randSys);

			cpuExpress.writeBean(cpu);


			Disk ds = new Disk();
			ds.setHost("kxw_disk");
			ds.setFree(210 + randDiskFree);
			ds.setUsed(300+randDiskUsed);


			diskExpress.writeBean(ds);

			/*express.write(Point.measurement("disk")
					.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("used", 80L +randDiskUsed)
					.addField("free", 300L + randDiskFree)
					.tag("host", "kxw_v2")
					.build());*/


			try {
				List<Cpu> pojoList = cpuExpress.queryBeans("SELECT * FROM cpu WHERE time > now() - 5s order by time desc limit 3");
				for(Cpu c:pojoList){
					System.out.println(c.toString());
				}

				List<Disk> diskList = diskExpress.queryBeans("SELECT * FROM disks WHERE time > now() - 5s order by time desc limit 3");
				for(Disk d:diskList){
					System.out.println(d.toString());
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
