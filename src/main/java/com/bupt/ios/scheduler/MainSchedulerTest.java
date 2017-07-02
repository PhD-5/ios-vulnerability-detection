/**
 * 
 */
package com.bupt.ios.scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author yujianbo
 *
 * 2017年2月2日
 */
public class MainSchedulerTest {

	@Test
	public void test() {
		String[] args = {"C:\\Users\\huge\\Desktop\\static\\temp\\com.example.BlockSample","D:\\workList\\ios_test"};
//		String[] args = {"D:\\workList\\ios_test\\DVIA-2.1\\com.yjb.dvia","D:\\workList\\ios_test"};
		MainScheduler.main(args);
		
	}

}
