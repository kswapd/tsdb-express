package com.dcits.app;


import com.dcits.repo.interfaces.RepoCpu;
import com.dcits.repo.interfaces.RepoDisks;
import com.dcits.repo.interfaces.RepoMemory;
import com.dcits.repo.models.Cpu;
import com.dcits.repo.models.Disk;
import com.dcits.repo.models.Memory;
import com.dcits.tsdb.annotations.EnableRepoInterfaceScan;
import com.dcits.tsdb.utils.JPAConvertor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
		RepoCpu cpuExpress = (RepoCpu)context.getBean("repoCpu");
		RepoDisks diskExpress = (RepoDisks)context.getBean("repoDisks");
		RepoMemory memExpress = (RepoMemory)context.getBean("repoMemory");
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

			//ds.setTime;
			diskExpress.save(ds);


			Memory mem = new Memory();
			mem.setIpAddr("192.168.1.100");
			mem.setPercent(Math.random());

			//mem.setTime(String.valueOf(System.currentTimeMillis()));
			mem.setTime(String.valueOf(System.currentTimeMillis()));

			long before2Min = System.currentTimeMillis() - 8*60*60* 1000 - 5*1000;
			System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
					.format(new Date(before2Min)));

			memExpress.save(mem);
			try {
				/*List<Cpu> cpuList = cpuExpress.queryBeans("SELECT * FROM cpu WHERE time > now() - 5s order by time desc limit 3");
				for(Cpu c:cpuList){
					System.out.println(c.toString());
				}

				List<Disk> diskList = diskExpress.queryBeans("SELECT * FROM disks WHERE time > now() - 5s order by time desc limit 3");
				for(Disk d:diskList){
					System.out.println(d.toString());
				}*/

				Memory o = memExpress.findLastOne();
				long num = memExpress.count();
				System.out.println(String.format("%s,%d", o.toString(), num));
				//List<Memory> memList = memExpress.queryBeans("SELECT * FROM memory WHERE time > now() - 5h order by time desc limit 3");
				//List<Memory> memList = memExpress.find("SELECT mean(\"percent\") as \"percent\"  FROM memory WHERE time > now() - 50s and ip_addr='192.168.1.100' group by time(5s) limit 3");
				//List<Memory> memList = memExpress.find("SELECT *  FROM memory WHERE ip_addr='192.168.1.100' order by time desc limit 3");
				//List<Memory> memList = memExpress.findByIpAddrOrderByTimeDescLimit("192.168.1.100",5);
				List<Memory> memList = memExpress.findByIpAddrAndTimeBeforeOrderByTimeDescLimit("192.168.1.100",String.valueOf(before2Min),5);
				//Memory memList = memExpress.findLastOne();
				for(Memory m:memList)
				{
					System.out.println(m.toString());
				}


			}
			catch (RuntimeException e){
				System.out.println(e.getMessage());

			}

			try{
				Thread.sleep(1000);
			}
			catch(InterruptedException e){
				isStart = false;
				System.out.println(e.getMessage());
			}

		}
	}

}
