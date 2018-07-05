# TSDB-EXPRESS

-----------

![logo](http://dcits.com/statics/images/dcits/logo.png)

## Introduction
A time series database object-relational mapping(ORM) library, support **JPA**-compatible entity definition and method declaration. After the measurement models definition and corresponding generic type interface declaration, you can  query and save influxDB data as a normal java bean. 


## Requirement

*  `JDK version 1.7 or later`
*  `maven version 3.0+`
* `Spring Framework 4.0.0 or later`

## Features
* It aims to response queries in real-time, which means every data point is indexed as it comes in and is immediately available in queries that should return in < 100ms.
* JPA-compatible query syntax, support operator **AND,OR,NOT,BEFORE,AFTER,GREATERTHAN, LESSTHAN** etc.
* No redundant XML configuration, annotation type programming.
* Support data source connection pool, improve data access performance.
* Do not need to implement repository operator, **just declare corresponding interface and use it**.
* Minimum dependency installation.

## Prerequisites
* Install InfluxDB **version 1.5.2** and chronograf as InfluxDB web UI.

  **You can easily setup and run influxDB and chronograf as a docker container service, the deployment and configuration files can be found [here](https://gitee.com/kswapd/docker-devops/tree/master/influxdb)**.



* Create InfluxDB databases with retention policy.
```
>influx -precision rfc3339
>CREATE DATABASE "metrics_db3" WITH DURATION 3h REPLICATION 1 NAME "rp_3h"
```

* Add resource file tsdb.properties in your project.
```
#tsdb.properties
#influxdb url
influxdb.address=http://10.88.2.104:8086
influxdb.user=root
influxdb.password=root
influxdb.dbName=metrics_db3
influxdb.rpName=rp_3h
#All data will be flushed into database when data size is larger than maxBatchSize.
influxdb.maxBatchSize=10
#All data will be flushed into database when time interval of data timestamp from now is larger than maxBatchInterval(milliseconds).
influxdb.maxBatchInterval=100
```

## Compile && Install

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


### influxDB repository interface declaration

```
//Cpu measurement repository
package com.dcits.repo.othermetrics;
public interface RepoCpu extends CustomRepo<Cpu> {

}
```
Or more complex JPA-compatible interface declaration
```
package com.dcits.repo.memory;
public interface RepoMemory extends CustomRepo<Memory> {

	public List<Memory> findByIpAddrOrderByTimeDescLimit(String ip, int limit);
	public List<Memory> findByIpAddrLimit(String ip, int limit);
	public List<Memory> findByIpAddrAndTimeBeforeLimit(String ip, String time,int limit);
	public List<Memory> findByIpAddrAndTimeBeforeOrderByTimeDescLimit(String ip, String time,int limit);
	public List<Memory> aggregateByPercentMeanIpAddrAndTimeBeforeGroupByTimeOrderByTimeDescLimit(String ip, String time,String interval, int limit);
}

```

To make interface above be found and implemented, we add package that contain interfaces within annotation:
```
package com.dcits.app;


@EnableRepoInterfaceScan({"com.dcits.repo"});
public class Application {
...
...
}
```

To make this annotation acitve,  add this package in your xml:
```
<context:component-scan base-package="com.dcits.app"></context:component-scan>
```

### JPA method usage examples
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


 

