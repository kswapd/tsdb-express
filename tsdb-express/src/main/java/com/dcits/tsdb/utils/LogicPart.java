package com.dcits.tsdb.utils;

/**
 * Created by kongxiangwen on 6/29/18 w:26.
 */
public class LogicPart{

	private String subjectPredicate;
	private String logicConjection;

	public String getSubjectPredicate() {
		return subjectPredicate;
	}

	public void setSubjectPredicate(String subjectPredicate) {
		this.subjectPredicate = subjectPredicate;
	}

	public String getLogicConjection() {
		return logicConjection;
	}

	public void setLogicConjection(String logicConjection) {
		this.logicConjection = logicConjection;
	}

	public LogicPart(String subjectPredicate, String logicConjection) {
		this.subjectPredicate = subjectPredicate;
		this.logicConjection = logicConjection;
	}
}
