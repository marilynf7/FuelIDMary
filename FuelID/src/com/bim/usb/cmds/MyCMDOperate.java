package com.bim.usb.cmds;





import com.bimsdk.usb.io.USBService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

public class MyCMDOperate {

	public Dialog export(String input, USBService context) {
		Dialog myAlertDialog = null;
		MyCmd myCmd = factoryMethodMyCmd(input, context);
		if (myCmd == null) {// 没有定义该命令
		// System.out.println("exportnull");
			return myAlertDialog;
		}
		myAlertDialog = myCmd.CreateAlertDialog();
		// System.out.println("exp1ortnull");
		return myAlertDialog;
	}

	private MyCmd factoryMethodMyCmd(String input, USBService context) {
		MyCmd myCmd = null;
		String toustrStr = input.substring(0, 6);// 获取消息体的头部 知道是哪一个命令
//		System.out.println("toustrStr:" + toustrStr);
		 if ("{G1000".equals(toustrStr)) {
		 myCmd = new MyCmdSppName(input, context);
		 }else if ("{G1002".equals(toustrStr)) {
		 myCmd = new MyCmdHIDName(input, context);
		 }else if ("{G1004".equals(toustrStr)) {
		 myCmd = new MyCmdPrefix(input, context);
		 }else if ("{G1006".equals(toustrStr)) {
		 myCmd = new MyCmdSuffix(input, context);
		 }else if ("{G1008".equals(toustrStr)) {
		 // 占时未定
		 }else if ("{G1010".equals(toustrStr)) {
		 myCmd = new MyCmdChuFa2(input, context);
		 }else if ("{G1012".equals(toustrStr)) {
		 myCmd = new MyCmdDuTime(input, context);
		 }else if ("{G1014".equals(toustrStr)) {
		 myCmd = new MyCmdNoCodeTime(input, context);
		 }else if ("{G1016".equals(toustrStr)) {
		 myCmd = new MyCmdNoCode2(input, context);
		 }else if ("{G1018".equals(toustrStr)) {
		 myCmd = new MyCmdUpdates(input, context);
		 }else if ("{G1020".equals(toustrStr)) {
		 myCmd = new MyCmdVioce(input, context);
		 }else if ("{G1022".equals(toustrStr)) {
		 myCmd = new MyCmdLED(input, context);
		 }else if ("{G1024".equals(toustrStr)) {
		 myCmd = new MyCmdInputType(input, context);
		 }else if ("{G1026".equals(toustrStr)) {
		 myCmd = new MyCmdXMTime(input, context);
		 }else if ("{G1028".equals(toustrStr)) {
		 myCmd = new MyCmd39(input, context);
		 }else

		if ("{G1030".equals(toustrStr)) {
			myCmd = new MyCmd128(input, context);
		}

		 else if ("{G1032".equals(toustrStr)) {
		 myCmd = new MyCmdCodabar(input, context);
		 }else if ("{G1034".equals(toustrStr)) {
		 myCmd = new MyCmdI25(input, context);
		 }else if ("{G1036".equals(toustrStr)) {
		 myCmd = new MyCmdUPCEAN(input, context);
		 }else if ("{G1038".equals(toustrStr)) {
		
		 }else if ("{G1040".equals(toustrStr)) {
		
		 }

		return myCmd;
	}

	public static boolean checkMyCmd(String input) {
		boolean result = true;
		if (input.length() < 7) {
			return false;
		}
		String toustrStr = input.substring(0, 6);// 获取消息体的头部 知道是哪一个命令
		System.out.println("toustrStr:" + toustrStr);
		if ("{G1000".equals(toustrStr)) {

		} else if ("{G1002".equals(toustrStr)) {

		} else if ("{G1004".equals(toustrStr)) {

		} else if ("{G1006".equals(toustrStr)) {

		} else if ("{G1008".equals(toustrStr)) {
			// 占时未定
			result = false;
		} else if ("{G1010".equals(toustrStr)) {

		} else if ("{G1012".equals(toustrStr)) {

		} else if ("{G1014".equals(toustrStr)) {

		} else if ("{G1016".equals(toustrStr)) {

		} else if ("{G1018".equals(toustrStr)) {

		} else if ("{G1020".equals(toustrStr)) {

		} else if ("{G1022".equals(toustrStr)) {

		} else if ("{G1024".equals(toustrStr)) {

		} else if ("{G1026".equals(toustrStr)) {

		} else if ("{G1028".equals(toustrStr)) {

		} else if ("{G1030".equals(toustrStr)) {
			// myCmd = new MyCmd128(input, context);
		} else if ("{G1032".equals(toustrStr)) {

		} else if ("{G1034".equals(toustrStr)) {

		} else if ("{G1036".equals(toustrStr)) {

		} else {
			result = false;
		}

		return result;
	}


	public static String CreateMyCmd(int arg1) {
		String outCMd = null;
		switch (arg1) {
		case SendConstant.F_StartScan:
			outCMd="{Start}";
			break;
		case SendConstant.F_CloseHibernate:
			outCMd="{off/Sleep}";
			break;
		case SendConstant.SendCloseRead:
			outCMd="{off/Read}";
			break;
		case SendConstant.SendOpenRead:
			outCMd="{on/Read}";
			break;
		case SendConstant.SendDefaultSetting:
			outCMd="{Init}";
			break;
		case SendConstant.GetCipher:
			outCMd="{GB100}{G2010}";
			break;
		case SendConstant.GetScanerID:
			outCMd="{GB100}{G2012}";
			break;
		case SendConstant.ReadBattery:
			outCMd="{GB100}{G2014}";
			break;
		case SendConstant.ReadVersion:
			outCMd="{GB100}{G2016}";
			break;
		case SendConstant.SetSleepTime:
			outCMd="{GB100}{G1026?}";
			break;
		case SendConstant.SetSPPBluetoothName:
			outCMd="{GB100}{G1000?}";
			break;
		case SendConstant.SetHIDBluetoothName:
			outCMd="{GB100}{G1002?}";
			break;
		case SendConstant.SetPostambleCharacter:
			outCMd="{GB100}{G1006?}";
			break;
		case SendConstant.SetPreambleCharacter:
			outCMd="{GB100}{G1004?}";
			break;
		case SendConstant.SetOutputMode:
			outCMd="{GB100}{G1024?}";
			break;
		case SendConstant.SetLED:
			outCMd="{GB100}{G1022?}";
			break;
		case SendConstant.SetBuzzer:
			outCMd="{GB100}{G1020?}";
			break;
		case SendConstant.NoReadOutput:
			outCMd="{GB100}{G1016?}";
			break;
		case SendConstant.TriggerMode:
			outCMd="{GB100}{G1010?}";
			break;
		case SendConstant.NoDecodeTimeOut:
			outCMd="{GB100}{G1014?}";
			break;
		case SendConstant.TTR:
			outCMd="{GB100}{G1012?}";
			break;
		case SendConstant.DecodesBeforeOutput:
			outCMd="{GB100}{G1018?}";
			break;
		case SendConstant.SetVolume:
			outCMd="{GB100}{G1020?}";
			break;
		case SendConstant.Code39:
			outCMd="{GB100}{G1028?}";
			break;
		case SendConstant.Code128:
			outCMd="{GB100}{G1030?}";
			break;
		case SendConstant.Codabar:
			outCMd="{GB100}{G1032?}";
			break;
		case SendConstant.Interleaved2of5:
			outCMd="{GB100}{G1034?}";
			break;
		case SendConstant.UPCEAN:
			outCMd="{GB100}{G1036?}";
			break;
	
		default:
			break;
		}
		return outCMd;
	}

}
