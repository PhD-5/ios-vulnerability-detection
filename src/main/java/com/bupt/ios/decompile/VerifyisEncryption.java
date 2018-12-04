package com.bupt.ios.decompile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VerifyisEncryption {
	private static Logger LOGGER = LogManager.getLogger(VerifyisEncryption.class);

	public boolean isEncryption(String exefile) {
		boolean isEncrypt = false;
		String extractCmd = " otool -l " + exefile + " | grep cryptid";
		String[] exe = new String[] { "/bin/sh", "-c", extractCmd };
		ProcessBuilder pb = new ProcessBuilder(exe);
		Process ps;
		try {
			ps = pb.start();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ps.getInputStream()), 1024);
			String lineString;
			while ((lineString = bufferedReader.readLine()) != null) {
				if (lineString.contains("1")) {
					LOGGER.info("ipa已加密");
					isEncrypt = true;
				} else {
					LOGGER.info("ipa文件未加密");
					isEncrypt = false;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isEncrypt;

	}
}
