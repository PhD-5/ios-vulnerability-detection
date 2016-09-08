package com.bupt.ios.analyzer;
import java.util.*;

public class BlMsg {
	private String addr;
	private String name;
	private Map<String,String> regs = new HashMap<String,String>();
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, String> getRegs() {
		return regs;
	}
	public void setRegs(Map<String, String> regs) {
		this.regs = regs;
	}
	
	
}
