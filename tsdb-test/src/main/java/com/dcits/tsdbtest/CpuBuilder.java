package com.dcits.tsdbtest;

import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 6/21/18 w:25.
 */
public final class CpuBuilder {
	private String time;
	private String host;
	private Integer idle;
	private Integer user;
	private Integer system;

	public CpuBuilder() {
	}

	public static CpuBuilder aCpu() {
		return new CpuBuilder();
	}

	public CpuBuilder withTime(String time) {
		this.time = time;
		return this;
	}

	public CpuBuilder withHost(String host) {
		this.host = host;
		return this;
	}

	public CpuBuilder withIdle(Integer idle) {
		this.idle = idle;
		return this;
	}

	public CpuBuilder withUser(Integer user) {
		this.user = user;
		return this;
	}

	public CpuBuilder withSystem(Integer system) {
		this.system = system;
		return this;
	}

	public Cpu build() {
		Cpu cpu = new Cpu();
		cpu.setTime(time);
		cpu.setHost(host);
		cpu.setIdle(idle);
		cpu.setUser(user);
		cpu.setSystem(system);
		return cpu;
	}
}
