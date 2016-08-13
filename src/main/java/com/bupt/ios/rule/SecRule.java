package com.bupt.ios.rule;

import com.bupt.ios.rule.Function;

import java.util.*;
/**
 * 
 * @author yujianbos
 *
 */
public class SecRule {
	private String ruleId;
	private String ruleName;
	private String description;
	private int ruleType;
	private List<Function> content;
	private int riskLevel;
	private String solution;
	private boolean isEnable;
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getRuleType() {
		return ruleType;
	}
	public void setRuleType(int ruleType) {
		this.ruleType = ruleType;
	}
	public List<Function> getContent() {
		return content;
	}
	public void setContent(List<Function> content) {
		this.content = content;
	}
	public int getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}
	public String getSolution() {
		return solution;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
	public boolean isEnable() {
		return isEnable;
	}
	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	

}
