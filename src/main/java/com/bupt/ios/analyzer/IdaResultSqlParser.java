package com.bupt.ios.analyzer;

import java.sql.*;
import java.util.*;

import org.json.*;

public class IdaResultSqlParser {


	/**
	 * 解析idapython生成的db文件
	 * @param sqlPath path of bl.db
	 */
	public static void parseBLSql(String sqlPath){

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+sqlPath);
//			System.out.println("Opened database successfully");
			
			stmt = conn.createStatement();
			
			//获取所有的fun名称
			Set<String> funList = new HashSet<String>();
			ResultSet rs4fun = stmt.executeQuery( "SELECT * FROM BlTable;" );
			while ( rs4fun.next() ) {
				BlMsg blMsg = new BlMsg();
				String funName = rs4fun.getString("FUNC");
				funList.add(funName);
			}
			rs4fun.close();
			
			for(String Name : funList){
				ResultSet rs = stmt.executeQuery( "SELECT * FROM BlTable WHERE FUNC='"+Name+"';" );
				while ( rs.next() ) {
					BlMsg blMsg = new BlMsg();
					String funName = rs.getString("FUNC");
					String lable = rs.getString("BL");
					String regs = rs.getString("REGS");
					Map<String, String> regsMap = new HashMap<String , String>();
					if(regs!=null){
						JSONObject jsonObject = new JSONObject(regs);
						Iterator it = jsonObject.keys();
						while(it.hasNext()){
							String regsName = String.valueOf(it.next());
							String regsInfo = (String) jsonObject.get(regsName);
							regsMap.put(regsName, regsInfo);
							//						System.out.println(regsName+":"+regsInfo);
						}
					}
					blMsg.setName(lable);
					blMsg.setRegs(regsMap);
					
					if(IdaResultSet.getMSG().containsKey(funName)){//如果函数已经在结果集中
						IdaResultSet.getMSG().get(funName).add(blMsg);
					}else{//如果函数不在结果集中
						IdaResultSet.addMSG(funName, new ArrayList<BlMsg>());
					}
				}
				
				new Analyzer().doAnalyse();
				IdaResultSet.getMSG().clear();
			}
			
			
			stmt.close();
			conn.close();

//			System.out.println("Operation done successfully");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
