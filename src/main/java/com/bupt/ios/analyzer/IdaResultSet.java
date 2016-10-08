package com.bupt.ios.analyzer;

import java.util.*;

public class IdaResultSet {
	
	//MSG: KEY是函数体名称 VALUE是函数内部每次调用BL的信息
	private static Map<String, List<BlMsg>> MSG = new HashMap<String, List<BlMsg>>();
	
	public static Map<String, List<BlMsg>> getMSG() {
		return MSG;
	}
	public static void addMSG(String key, List<BlMsg> value) {
		MSG.put(key, value);
	}
	
	
}

