package com.dcits.repo.models;

import com.dcits.tsdb.annotations.AggregatedColumn;
import com.dcits.tsdb.annotations.Column;
import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.annotations.Tag;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kongxiangwen on 6/26/18 w:26.
 */
 class PercentMeasuerment2 {

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

public class PercentMeasuerment {

	@Column(name = "time")
	protected Long time;

	@Tag(name = "ipAddr")
	protected String ipAddr;


	@Column(name = "percent")
	protected Double percent;


	@AggregatedColumn(name = "count")
	protected Long count;

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
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

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		//System.out.println(TimeZone.getTimeZone("GMT").toString());
		String strTime = dateFormat.format(new Date(time));


		return "PercentMeasuerment{" +
				"time=" + strTime +":"+time+
				", ipAddr='" + ipAddr + '\'' +
				", percent=" + percent +
				", count=" + count +
				'}';
	}
}
