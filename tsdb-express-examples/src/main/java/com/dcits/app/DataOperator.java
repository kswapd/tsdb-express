package com.dcits.app;

import com.dcits.repo.memory.RepoTradeInfo;
import com.dcits.repo.models.TradeInfo;
import com.dcits.repo.othermetrics.RepoCpu;
import com.dcits.repo.othermetrics.RepoDisks;
import com.dcits.repo.memory.RepoMemory;
import com.dcits.repo.models.Cpu;
import com.dcits.repo.models.Disk;
import com.dcits.repo.models.Memory;
import com.dcits.tsdb.annotations.EnableRepoInterfaceScan;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import javax.annotation.Resource;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 6/28/18 w:26.
 */

//@EnableRepoInterfaceScan({"com.dcits.repo.othermetrics","com.dcits.repo.memory"})
@EnableRepoInterfaceScan
@Component
public class DataOperator {
	@Resource
	RepoCpu cpuExpress;
	@Resource
	RepoDisks diskExpress;
	@Resource
	RepoMemory memExpress;

	@Resource
	RepoTradeInfo tradeExpress;
	Cpu cpu = new Cpu();

	public void Oper() {


		boolean isStart = true;
		Long curMilliTime = System.currentTimeMillis();
		long saveNum = 0;
		Long startStat = System.currentTimeMillis();
		boolean isFind = false;
		while (isStart) {



			int randIdle = (int) (Math.random() * 30);
			int randUser = (int) (Math.random() * 20);
			int randSys = (int) (Math.random() * 10);
			int randDiskUsed = (int) (Math.random() * 50);
			int randDiskFree = (int) (Math.random() * 50);



			/*cpu.setHost("kxw_host");
			cpu.setIdle(90 + randIdle);
			cpu.setUser(9 + randUser);
			cpu.setSystem(1 + randSys);

			cpuExpress.save(cpu);


			Disk ds = new Disk();
			ds.setHost("kxw_disk");
			ds.setFree(210 + randDiskFree);
			ds.setUsed(300 + randDiskUsed);


			diskExpress.save(ds);
*/

			Memory mem = new Memory();
			mem.setIpAddr("192.168.1.100");
			mem.setPercent(Math.random());

			//mem.setTime(String.valueOf(System.currentTimeMillis()));
			//mem.setTime(String.valueOf(System.currentTimeMillis()));
			//	mem.setTime(curMilliTime);
			mem.setTime(System.currentTimeMillis());
			//mem.setTime(System.nanoTime());
			long before2Min = System.currentTimeMillis()  - 5 * 1000;		//utc time

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			//System.out.println(TimeZone.getTimeZone("GMT").toString());
			//System.out.println(dateFormat.format(new Date(before2Min)));	//default local time zone time




			memExpress.save(mem);
			saveNum ++;
			try {
				/*List<Cpu> cpuList = cpuExpress.queryBeans("SELECT * FROM cpu WHERE time > now() - 5s order by time desc limit 3");
				for(Cpu c:cpuList){
					System.out.println(c.toString());
				}

				List<Disk> diskList = diskExpress.queryBeans("SELECT * FROM disks WHERE time > now() - 5s order by time desc limit 3");
				for(Disk d:diskList){
					System.out.println(d.toString());
				}*/



				List<Memory> oo = memExpress.findByIpAddrIsOrderByTimeDescLimit("192.168.1.100",1);
				Memory o = null;
				if(oo.size() > 0){
					o = oo.get(0);
				}

				long num = memExpress.count();




				System.out.println(String.format("%s,%d",  o.toString(),num));


				//List<Memory> memList = memExpress.queryBeans("SELECT * FROM memory WHERE time > now() - 5h order by time desc limit 3");
				//List<Memory> memList = memExpress.find("SELECT mean(\"percent\") as \"percent\"  FROM memory WHERE time > now() - 50s and ip_addr='192.168.1.100' group by time(5s) limit 3");
				//List<Memory> memList = memExpress.find("SELECT *  FROM memory WHERE ip_addr='192.168.1.100' order by time desc limit 3");
				//List<Memory> memList = memExpress.findByOrderByTimeDescLimit(5);
				//List<Memory> memList = memExpress.findByLimit(5);
				//List<Memory> memList = memExpress.findBy();
				//List<Memory> memList = memExpress.findByOrderByTimeDesc();




				List<Memory> memList = memExpress.findByIpAddrIsOrderByTimeDescLimit("192.168.1.100",5);






				//List<Memory> memList = memExpress.findByIpAddrAndTimeBeforeOrderByTimeDescLimit("192.168.1.100", String.valueOf(before2Min), 5);
				//List<Memory> memList = memExpress.aggregateByIpAddrAndTimeBeforeOrderByTimeDescLimit("192.168.1.100", String.valueOf(before2Min), 5);
				//List<Memory> memList = memExpress.queryBeans("SELECT count(percent) FROM memory   where time > now()-1h group by time(5s),* order by time desc limit 3");

				//List<Memory> memList = memExpress.aggregateByPercentCountIpAddrIsAndTimeBeforeGroupByOrderByTimeDescLimit("192.168.1.100", String.valueOf(before2Min),"time(10s),*", 5);
				//QueryResult qr = memExpress.query("SELECT count(*) FROM memory  order by time desc group by time(5s),* limit 3");
				//List<Memory> memList = memExpress.aggregateByPercentMeanIpAddrIsAndTimeBeforeGroupByOrderByTimeDescLimit("192.168.1.100", String.valueOf(before2Min),"time(10s),*", 5);


				//List<Memory> memList = memExpress.aggregateByPercentMeanTimeAfterOrIpAddrIsAndTimeBeforeGroupByOrderByTimeDescLimit(String.valueOf(before2Min),"192.168.1.100", String.valueOf(before2Min),"5s", 5);
				//List<Memory> memList = memExpress.findByTimeAfterOrIpAddrIsAndTimeBeforeOrderByTimeDescLimit(String.valueOf(before2Min),"192.168.1.100", String.valueOf(before2Min), 5);

				//Memory memList = memExpress.findLastOne();
				for (Memory m : memList)
				{
					System.out.println(m.toString());
				}


			}
			catch (RuntimeException e) {
				System.out.println(e.getMessage());

			}

			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				isStart = false;
				System.out.println(e.getMessage());
			}


			/*Long endStat = System.currentTimeMillis();
			long elapsedStat = endStat - startStat;
			if (elapsedStat > 1000) {
				System.out.println(String.format("stat time:%d, num:%d",elapsedStat, saveNum));
				saveNum = 0;
				startStat = System.currentTimeMillis();
				List<Memory> memList = memExpress.findByIpAddrIsOrderByTimeDescLimit("192.168.1.100",5);


			}*/

		}
	}


	public void PerformTest() {
		boolean isStart = true;
		Long curMilliTime = System.currentTimeMillis();
		long saveNum = 0;
		Long startStat = System.currentTimeMillis();
		boolean isFind = false;

		while (isStart) {



			int randIdle = (int) (Math.random() * 30);
			int randUser = (int) (Math.random() * 20);
			int randSys = (int) (Math.random() * 10);
			int randDiskUsed = (int) (Math.random() * 50);
			int randDiskFree = (int) (Math.random() * 50);



			/*cpu.setHost("kxw_host");
			cpu.setIdle(90 + randIdle);
			cpu.setUser(9 + randUser);
			cpu.setSystem(1 + randSys);

			cpuExpress.save(cpu);


			Disk ds = new Disk();
			ds.setHost("kxw_disk");
			ds.setFree(210 + randDiskFree);
			ds.setUsed(300 + randDiskUsed);


			diskExpress.save(ds);*/


			Memory mem = new Memory();
			mem.setIpAddr("192.168.1."+saveNum);
			mem.setPercent(Math.random());

			//mem.setTime(String.valueOf(System.currentTimeMillis()));
			//mem.setTime(String.valueOf(System.currentTimeMillis()));
			//	mem.setTime(curMilliTime);
			mem.setTime(System.currentTimeMillis());
			//mem.setTime(System.nanoTime());
			long before2Min = System.currentTimeMillis()  - 5 * 1000;		//utc time

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			//System.out.println(TimeZone.getTimeZone("GMT").toString());
			//System.out.println(dateFormat.format(new Date(before2Min)));	//default local time zone time




			memExpress.save(mem);
			saveNum ++;


			/*if(saveNum % 200 == 0){

				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {
					isStart = false;
					System.out.println(e.getMessage());
				}
			}*/
			Long endStat = System.currentTimeMillis();
			long elapsedStat = endStat - startStat;

			if (elapsedStat > 1000) {
				System.out.println(String.format("stat time:%d, num:%d",elapsedStat, saveNum));
				saveNum = 0;
				startStat = System.currentTimeMillis();
				long allNum  = memExpress.count();
				System.out.println(String.format("total num:%d",allNum));
			}

		}
	}

	public void PerformTradeTest() {
		boolean isStart = true;
		Long curMilliTime = System.currentTimeMillis();
		long saveNum = 0;
		Long startStat = System.currentTimeMillis();
		boolean isFind = false;
		TradeInfo trade = new TradeInfo();
		Random a = new Random();
		Random b = new Random();
		while (isStart) {



			int randIdle = (int) (Math.random() * 30);
			int randUser = (int) (Math.random() * 20);
			int randSys = (int) (Math.random() * 10);
			int randDiskUsed = (int) (Math.random() * 50);
			int randDiskFree = (int) (Math.random() * 50);






			trade.setBranch("branch-"+ saveNum);
			trade.setFrom("from-"+saveNum);

			/*trade.setBranch("branch-"+ a.nextInt(10000));
			trade.setFrom("from-"+b.nextInt(1000));
*/
			trade.setHost("host-"+saveNum);
			trade.setSeqNo("seqno-"+saveNum);
			trade.setStatus("status-"+saveNum);
			trade.setTagA("taga-"+saveNum);
			trade.setTagB("tagb-"+saveNum);
			trade.setTagC("tagc-"+saveNum);
			trade.setTagD("tagd-"+saveNum);
			trade.setTagE("tage-"+saveNum);

			trade.setValue(Math.random());

			trade.setTime(System.currentTimeMillis());
			long before2Min = System.currentTimeMillis()  - 5 * 1000;		//utc time
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			tradeExpress.save(trade);
			saveNum ++;


			/*if(saveNum % 20 == 0){

				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {
					isStart = false;
					System.out.println(e.getMessage());
				}
			}*/
			Long endStat = System.currentTimeMillis();
			long elapsedStat = endStat - startStat;

			if (elapsedStat > 1000) {
				System.out.println(String.format("stat time:%d, num:%d",elapsedStat, saveNum));
				saveNum = 0;
				startStat = System.currentTimeMillis();
				long allNum  = tradeExpress.count();
				System.out.println(String.format("total num:%d",allNum));
			}

		}
	}
}
