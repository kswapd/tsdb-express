package com.dcits.repo.models;

import com.dcits.tsdb.annotations.Column;
import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.annotations.Tag;

/**
 * Created by kongxiangwen on 6/26/18 w:26.
 */
public class PercentMeasuerment {

	@Column(name = "time")
	protected String time;

	@Tag(name = "ipAddr")
	protected String ipAddr;


	@Column(name = "percent")
	protected Double percent;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Double getPercent() {
		return percent;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	@Override
	public String toString() {
		return "PercentMeasuerment{" +
				"time=" + time +
				", ipAddr='" + ipAddr + '\'' +
				", percent=" + percent +
				'}';
	}
}
