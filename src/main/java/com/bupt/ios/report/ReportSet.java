package com.bupt.ios.report;

import java.util.*;

import com.bupt.ios.rule.SecRule;
/**
 * 存放规则匹配结果
 * @author yujianbo
 *
 */
public class ReportSet {
	private static Map<String,List<SecRule>> re1 = new HashMap<String, List<SecRule>>();
	private static List<SecRule>re2 = new ArrayList<SecRule>();
	private static Map<String,List<SecRule>> protectInfoMap = new HashMap<String, List<SecRule>>();
	
	
	public static Map<String,List<SecRule>> getRe1(){
		return re1;
	}
	public static void addRe1(String funName,SecRule rule){
		if (re1.containsKey(funName)){
			re1.get(funName).add(rule);
		}else{
			re1.put(funName, new ArrayList<SecRule>(Arrays.asList(rule)));
		}
	}
	
	public static List<SecRule> getRe2(){
		return re2;
	}
	public static void addRe2(SecRule rule){
		re2.add(rule);
	}
	
	public static Map<String,List<SecRule>> getProtectInfoMap(){
		return protectInfoMap;
	}
	public static void addProInfo(String funName,SecRule rule){
		if (protectInfoMap.containsKey(funName)){
			protectInfoMap.get(funName).add(rule);
		}else{
			List<SecRule> list = new ArrayList<SecRule>();
			list.add(rule);
			protectInfoMap.put(funName, list);
		}
	}

}

