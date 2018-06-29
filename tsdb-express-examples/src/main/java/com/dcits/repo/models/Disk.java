package com.dcits.repo.models;

import com.dcits.tsdb.annotations.Column;
import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.annotations.Tag;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */


@Measurement(name = "disks")
public class Disk {

	@Column(name = "time")
	private String time;

	@Tag(name = "host")
	private String host="aaa";


	@Column(name = "free")
	private Integer free;

	@Column(name = "used")
	private Integer used;


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getFree() {
		return free;
	}

	public void setFree(Integer free) {
		this.free = free;
	}

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}

	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public Disk() {

	}

	@Override
	public String toString() {
		return "Disk{" +
				"time='" + time + '\'' +
				", host='" + host + '\'' +
				", free=" + free +
				", used=" + used +
				'}';
	}

	// getters (and setters if you need)
}
