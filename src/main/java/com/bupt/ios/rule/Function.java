package com.bupt.ios.rule;
import java.util.*;

public class Function {
	private boolean FuncType;
	private String FunctionName;
	private Map<String, String> Parameters = new HashMap<String, String>();
	
	
	public boolean getFuncType() {
		return FuncType;
	}
	public void setFuncType(boolean funcType) {
		FuncType = funcType;
	}
	public String getFunctionName() {
		return FunctionName;
	}
	public void setFunctionName(String functionName) {
		FunctionName = functionName;
	}
	public Map<String, String> getParameters() {
		return Parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		Parameters = parameters;
	}
	
	
}
