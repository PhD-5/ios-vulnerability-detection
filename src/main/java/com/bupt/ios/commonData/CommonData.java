package com.bupt.ios.commonData;
import com.bupt.ios.rule.SecRule;
import java.util.*;
/**
 * 
 * @author yujianbo
 *	公共数据区：rulepool规则池
 */
public class CommonData {
	private static List<SecRule> rulePool1 = new ArrayList<SecRule>();
	private static List<SecRule> rulePool2 = new ArrayList<SecRule>();
	
	public static List<SecRule> getRulePool1(){
		return rulePool1;
	}
	
	public static void addSecRule1(SecRule rule){
		rulePool1.add(rule);
	}
	
	public static List<SecRule> getRulePool2(){
		return rulePool2;
	}
	
	public static void addSecRule2(SecRule rule){
		rulePool2.add(rule);
	} 
	
}
