package com.bupt.ios.analyzer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.bupt.ios.commonData.ProjectParameters;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;


public class PlistAnalyzer {

	private static Map<String, Object> pinfo;

	public PlistAnalyzer() {
		// TODO Auto-generated constructor stub
	}
	
	public static void InfoPlistAnalyze(){
			try {
				NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(ProjectParameters.getInfoPlistath());
				NSString parameters = (NSString)rootDict.objectForKey("CFBundleIdentifier"); 
				System.out.print(parameters);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PropertyListFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		System.out.println("Here");
	}

}
