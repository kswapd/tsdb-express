# TSDB-EXPRESS

-----------
![logo](http://dcits.com/statics/images/dcits/logo.png)


##Introduction
A time series database middleware to connect InfluxDB.


## Requirement

*  `JDK version 1.7 or later`
*  `maven version 3.0+`
* `Spring Framework 4.0.0 or later`

##Prerequisites
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

### Compile

 `mvn clean install`
 
### maven import
```
<dependency>
        <groupId>dcits</groupId>
        <artifactId>tsdb-express</artifactId>
        <version>1.0-SNAPSHOT</version>
</dependency>
```


 

