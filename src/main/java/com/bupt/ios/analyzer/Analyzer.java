package com.bupt.ios.analyzer;

import java.util.*;

import org.apache.logging.log4j.*;

import com.bupt.ios.commonData.CommonData;
import com.bupt.ios.report.ReportSet;
import com.bupt.ios.rule.Function;
import com.bupt.ios.rule.SecRule;

public class Analyzer {
	private static Logger logger = LogManager.getLogger(Analyzer.class);
	
	/**
	 * 对IdaResultSet中的信息进行规则匹配，规则在CommonData中rulePool中
	 */
	public void doAnalyse(){
		logger.info("start analyse rule pool [1]");
		parsePool1();
		logger.info("start analyse rule pool [2]");
		parsePool2();
		
	}
	
	/**
	 * 针对规则库1匹配IdaResultSet
	 * 如果匹配上，则加入report
	 * 解析步骤：1、选取一个rule  2、选取IdaResult中的一个方法体  3、选择rule中的一个function，挨个去匹配方法体中的每个b信息
	 */
	private void parsePool1(){
		for(SecRule rule1 :CommonData.getRulePool1()){//choose one rule
			boolean isRuleMatch = false;//this rule1 is match in result set
			List<Function> content = rule1.getContent();
			//遍历IdaResultSet，以function为单位，去匹配rule
			for(String reFunName:IdaResultSet.getMSG().keySet()){ //choose one function in result set
				boolean isRuleMatchInThisFun = true;
				//在funciont的BL信息中挨个匹配rule中的信息
				for(Function fun:content){//choose one function in rule
					boolean ruleFunIsMatch = false;
					String ruleFunName = fun.getFunctionName();
					Map<String,String> ruleReg = fun.getParameters();//规则参数表
					
					for(BlMsg bl:IdaResultSet.getMSG().get(reFunName)){
						if(ruleFunName.equals("unknown")||
								ruleFunName.equals(bl.getName())){
							if(cmpMap(ruleReg, bl.getRegs())){
								//rule的fun匹配上
								ruleFunIsMatch=true;
//								System.out.println("["+reFunName+"] "+bl.getAddr()+": "+ruleFunName);
							}
							
						}
					}
					ruleFunIsMatch = !(ruleFunIsMatch^fun.getFuncType());
					isRuleMatchInThisFun &= ruleFunIsMatch;
				}
				if(isRuleMatchInThisFun){
					isRuleMatch = true;
					ReportSet.addRe1(reFunName, rule1);
//					System.out.println("in function:"+reFunName+" match the rule "+rule1.getRuleId()+":"+rule1.getDescription());
				}
			}
			
			
		}
	}

	/**
	 * 针对规则库2匹配IdaResultSet
	 * 如果最后没匹配上，则加入report
	 */
	private void parsePool2(){
		for(SecRule rule2 :CommonData.getRulePool2()){//choose one rule
			boolean isRuleMatch = false;//this rule1 is match in result set
			List<Function> content = rule2.getContent();
			//遍历IdaResultSet，以function为单位，去匹配rule
			for(String reFunName:IdaResultSet.getMSG().keySet()){ //choose one function in result set
				boolean isRuleMatchInThisFun = true;
				//在funciont的BL信息中挨个匹配rule中的信息
				for(Function fun:content){//choose one function in rule
					boolean ruleFunIsMatch = false;
					String ruleFunName = fun.getFunctionName();
					Map<String,String> ruleReg = fun.getParameters();//规则参数表
					
					for(BlMsg bl:IdaResultSet.getMSG().get(reFunName)){
						if(ruleFunName.equals("unknown")||ruleFunName.equals(bl.getName())){
							if(cmpMap(ruleReg, bl.getRegs())){
								//rule的fun匹配上
								ruleFunIsMatch=true;
							}
							
						}
					}
					ruleFunIsMatch = !(ruleFunIsMatch^fun.getFuncType());
					isRuleMatchInThisFun &=ruleFunIsMatch;
//					isRuleMatchInThisFun = !(ruleFunIsMatch^isRuleMatchInThisFun);
				}
				if(isRuleMatchInThisFun){
					isRuleMatch = true;
					ReportSet.addProInfo(reFunName, rule2);
				}
			}
			
			if(!isRuleMatch){
				ReportSet.addRe2(rule2);
			}
			
			
		}
	
	}
	
	/**
	 * match rule map and ida result map
	 */
	private boolean cmpMap(Map<String, String>rule, Map<String, String>result){
		for(String ruleKey:rule.keySet())
		{
			if(result.containsKey(ruleKey)&&rule.get(ruleKey).equals(result.get(ruleKey))){
				continue;
			}else{
				return false;
			}
		}
		return true;
	}
}
