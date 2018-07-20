package com.dcits.repo.models;

import com.dcits.tsdb.annotations.Column;
import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.annotations.Tag;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */



@Measurement(name = "rp_3h.trade_test2")
public class TradeInfo {

	@Tag(name = "time")
	private Long time;

	@Tag(name = "host")
	private String host="aaa";


	@Tag(name = "branch")
	private String branch;

	@Tag(name = "status")
	private String status;

	@Tag(name = "seqNo")
	private String seqNo;


	@Tag(name = "from")
	private String from;


	@Tag(name = "to")
	private String to;




	@Tag(name = "tagA")
	private String tagA;
	@Tag(name = "tagB")
	private String tagB;
	@Tag(name = "tagC")
	private String tagC;
	@Tag(name = "tagD")
	private String tagD;
	@Tag(name = "tagE")
	private String tagE;

	@Column(name = "value")
	private double value;

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TradeInfo{" +
				"time=" + time +
				", host='" + host + '\'' +
				", branch='" + branch + '\'' +
				", status='" + status + '\'' +
				", seqNo='" + seqNo + '\'' +
				", from='" + from + '\'' +
				", to='" + to + '\'' +
				", tagA='" + tagA + '\'' +
				", tagB='" + tagB + '\'' +
				", tagC='" + tagC + '\'' +
				", tagD='" + tagD + '\'' +
				", tagE='" + tagE + '\'' +
				'}';
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTagA() {
		return tagA;
	}

	public void setTagA(String tagA) {
		this.tagA = tagA;
	}

	public String getTagB() {
		return tagB;
	}

	public void setTagB(String tagB) {
		this.tagB = tagB;
	}

	public String getTagC() {
		return tagC;
	}

	public void setTagC(String tagC) {
		this.tagC = tagC;
	}

	public String getTagD() {
		return tagD;
	}

	public void setTagD(String tagD) {
		this.tagD = tagD;
	}

	public String getTagE() {
		return tagE;
	}

	public void setTagE(String tagE) {
		this.tagE = tagE;
	}


	// getters (and setters if you need)
}
