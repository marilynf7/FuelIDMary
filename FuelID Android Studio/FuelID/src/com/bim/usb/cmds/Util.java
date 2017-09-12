package com.bim.usb.cmds;

public class Util {

	public static boolean CheckStr(String str) {
		if ("1".equals(str)) {
			return true;
		}
		return false;
	}

	public static String Checkboolean(boolean str) {
		if (str) {
			return "1";
		}
		return "0";
	}

}
