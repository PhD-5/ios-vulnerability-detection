package com.bupt.ios.scheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.activation.MimetypesFileTypeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.JDOMException;

import com.bupt.ios.analyzer.Analyzer;
import com.bupt.ios.analyzer.IdaResultSqlParser;
import com.bupt.ios.analyzer.IdaResultXmlParser;
import com.bupt.ios.analyzer.PlistAnalyzer;
import com.bupt.ios.commonData.ProjectParameters;
import com.bupt.ios.decompile.IdaAnalyse;
import com.bupt.ios.decompile.UnzipIpa;
import com.bupt.ios.decompile.VerifyisEncryption;

public class AnalyseScheduler {
	private static String fileSeparator = System.getProperty("file.separator");
	private static Logger logger = LogManager.getLogger(AnalyseScheduler.class);


	public void schdular(){
		//判断ipa路径是否合法
		//默认输入的是ipa文件或者mach-o文件
		String ipaPath = ProjectParameters.getIpaPath();
		if(!ipaPath.endsWith("ipa")){
			//如果是mach-o，验证是否加密
			logger.info("input is a mach-o!");
			ProjectParameters.setInput_type("macho");
//			boolean isEncrypt = new VerifyisEncryption().isEncryption(ipaPath);
			boolean isEncrypt = false;
			if(isEncrypt){
				logger.error("input mach-o is encrypted! sorry!");
				System.exit(-1);
			}
			ProjectParameters.setExeFilePath(ipaPath);
			ProjectParameters.setAppName(new File(ipaPath).getName());
			
		}else{
			logger.info("input is a ipa!");
			ProjectParameters.setInput_type("ipa");
			//首先解压ipa文件
			File unzipPath =new File( ProjectParameters.getDetempPath()+fileSeparator+"unzip");
			if(!unzipPath.exists())
				unzipPath.mkdirs();
			try {
				UnzipIpa.unzip(ProjectParameters.getIpaPath(), unzipPath.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.warn("unzip failed");
			}

			//定位可执行文件、plist文件
			String exeName = "";
			for(File payload:unzipPath.listFiles()){
				if(payload.getName().equals("Payload")){
					for(File pkg:payload.listFiles()){
						if(pkg.isHidden()) {
							continue;
						}
						exeName = pkg.getName().split("\\.app")[0];
						ProjectParameters.setAppName(exeName);
						int found = 0;
						for(File f:pkg.listFiles()){
							if(f.getName().equals(exeName)){
								if(new VerifyisEncryption().isEncryption(f.getAbsolutePath())){
									logger.error("input ipa is encrypted! sorry!");
									System.err.println("Input ipa is encrypted!");
									System.exit(-1);
								}
								ProjectParameters.setExeFilePath(f.getAbsolutePath());
								found += 1;
							}else if (f.getName().equals("Info.plist")) {
								ProjectParameters.setInfoPlistath(f.getAbsolutePath());
								found += 1;
							}
							if (found == 2){
								break;
							}
							
						}
					}
				}
			}
		}
		if(ProjectParameters.getInput_type().equals("ipa")) {
			PlistAnalyzer.InfoPlistAnalyze();
		}
		
		//使用ida对可执行文件进行解析
		new IdaAnalyse().genIDB();

		//解析IDAPython的分析结果
		try {
			
//			IdaResultXmlParser.parseBlXml(ProjectParameters.getDetempPath()+fileSeparator+"ida"+fileSeparator+"bl.xml");
			IdaResultSqlParser.parseBLSql(ProjectParameters.getDetempPath()+fileSeparator+"ida"+fileSeparator+"bl.db");
		} catch (Exception e) {
			e.printStackTrace();
		}

		//开启开启解析器，进行规则匹配
		new Analyzer().doAnalyse();
	}



}
