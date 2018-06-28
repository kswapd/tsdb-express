# TSDB-EXPRESS

-----------
![logo](http://dcits.com/statics/images/dcits/logo.png)


## Introduction
A time series database middleware to connect InfluxDB, this is a **JPA**-compatible interface, what you need to do is to define your measurement models and declare a generic type interface within your measurements, this middleware will automatically implement your inerfaces! 


## Requirement

*  `JDK version 1.7 or later`
*  `maven version 3.0+`
* `Spring Framework 4.0.0 or later`

## Prerequisites
* Install InfluxDB **version 1.5.2** and chronograf as InfluxDB web UI.

use docker-compose to setup 
 ```
 version: "2"
 services:
   influxdb:
     image: influxdb:1.5.2
     network_mode: "bridge"
     volumes:
       - /home/kxw/datas/influxdb_data:/var/lib/influxdb
       - /home/kxw/dev/docker-devops/influxdb/influxdb.conf:/etc/influxdb/influxdb.conf:ro
     ports:
       - "8086:8086"
       - "8083:8083"
     environment:
       - INFLUXDB_ADMIN_ENABLED=true
   chronograf:
     image: quay.io/influxdb/chronograf:1.5.0.0
     network_mode: "bridge"
     ports:
       - "8888:8888"
 
 ```
 
* Create InfluxDB databases with retention policy
```
>influx -precision rfc3339
>CREATE DATABASE "metrics_db3" WITH DURATION 3h REPLICATION 1 NAME "rp_3h"
```


## Compile

 `mvn clean install`
 

## maven import
```
<dependency>
        <groupId>dcits</groupId>
        <artifactId>tsdb-express</artifactId>
        <version>1.0-SNAPSHOT</version>
</dependency>
```
## Usage

### influxDB measurement model definition
```
//Cpu.java
@Measurement(name = "cpu")
public class Cpu {

	@Column(name = "time")
	private String time;

	@Tag(name = "host")
	private String host="aaa";


	@Column(name = "idle")
	private Integer idle;

	@Column(name = "user")
	private Integer user;

	@Column(name = "system")
	private Integer system;


	@Override
	public String toString() {
		return "Cpu{" +
				"time='" + time + '\'' +
				", host='" + host + '\'' +
				", idle=" + idle +
				", user=" + user +
				", system=" + system +
				'}';
	}

...
...
```
or use abstract class

```
//Memory.java
@Measurement(name = "memory")
public class Memory extends PercentMeasuerment {
}
```


### influxDB repository definition

```
//Cpu measurement repository
@CustomRepoDeclared
public interface RepoCpu extends CustomRepo<Cpu> {

}
```
Or more complex JPA-compatible method declaration
```
@CustomRepoDeclared
public interface RepoMemory extends CustomRepo<Memory> {

	public List<Memory> findByIpAddrOrderByTimeDescLimit(String ip, int limit);
	public List<Memory> findByIpAddrLimit(String ip, int limit);
	public List<Memory> findByIpAddrAndTimeBeforeLimit(String ip, String time,int limit);
	public List<Memory> findByIpAddrAndTimeBeforeOrderByTimeDescLimit(String ip, String time,int limit);
	public List<Memory> aggregateByIpAddrAndTimeBeforeOrderByTimeDescLimit(String ip, String time,int limit);
	public List<Memory> aggregateByPercentMeanIpAddrAndTimeBeforeGroupByTimeOrderByTimeDescLimit(String ip, String time,String interval, int limit);
}

```



### JPA method usage
```

    //corresponding influxdb sql: select * from memory where "ipAddr"="192.168.1.100" order by time desc limit 5
    List<Memory> memList = memExpress.findByIpAddrOrderByTimeDescLimit("192.168.1.100",5);
	
    //corresponding influxdb sql: select * from memory where "ipAddr"="192.168.1.100" and time<'2018-06-28T05:34:04.920Z'  order by time desc limit 5
    List<Memory> memList = memExpress.findByIpAddrAndTimeBeforeOrderByTimeDescLimit("192.168.1.100", String.valueOf(before2Min), 5);
	
    //corresponding influxdb sql: select mean(percent) as percent from memory where "ipAddr"="192.168.1.100" and time<'2018-06-28T05:34:04.920Z' group by time(5m) order by time desc limit 5
    List<Memory> memList = memExpress.aggregateByPercentMeanIpAddrAndTimeBeforeGroupByTimeOrderByTimeDescLimit("192.168.1.100", String.valueOf(before2Min),"5m", 5);

```


## Development Instructions
**MUST** follow the instructions of **NOTE** section.
* Please install `JDK 1.7` before build the project.
* **MUST NOT** add any domain logics to this project.
* **MUST NOT** push any jar files, use maven dependency instead.
* **MUST NOT** push any unnecessary binary files.
* **MUST** push source code with meaningful message `git commit -m "meaningful message"`.
* **MUST** import `codequality/codestyle-formatter.xml`, and **format source code** (CTRL+SHIFT+F) and **organize imports** (CTRL+SHIFT+O) before commit.
* **MUST** use standard `JavaDoc Tags` on all java source code.
* **SHOULD** use `English` in JavaDoc, comments and any source code related resources **as possible**.
* **SHOULD** follow [Java Coding Conventions](http://www.oracle.com/technetwork/java/codeconventions-150003.pdf) and [Java Style Guide](https://google.github.io/styleguide/javaguide.html) if you haven't to improve code quality.


 

