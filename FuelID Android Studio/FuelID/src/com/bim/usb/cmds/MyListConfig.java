package com.bim.usb.cmds;







import com.bimsdk.usb.MResource;
import com.bimsdk.usb.UsbConnect;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class MyListConfig {
	public static int MySendCMDindex = 0;
//	public static String[] gongnengs = new String[] { "SPP蓝牙名称", "HID蓝牙名称",
//			"前缀字符", "后缀字符", "触发模式", "读取条码时间测试", "无解码超时", "无解码输出", "同一条解码纠错",
//			"蜂鸣器开关", "LED开关", "输出方式", "休眠时间", "39码设置", "128码设置", "Codabar码设置",
//			"i25码设置", "UPC/EAN码设置" };
	
//   	<string name="send_1">SPP蓝牙名称</string>
//   	<string name="send_2">HID蓝牙名称</string>
//   	<string name="send_3">前缀字符</string>
//   	<string name="send_4">后缀字符</string>
	
//   	<string name="send_5">触发模式</string>
//   	<string name="send_6">读取条码测试时间</string>
//   	<string name="send_7">无解码超时</string>
//   	<string name="send_75">无解码输出</string>
//   	<string name="send_8">同一条解码纠错</string>
	
//   	<string name="send_9">蜂鸣器声音</string>
	
//   	<string name="send_10">LED开关</string>
	
//   	<string name="send_11">输出方式</string>
//   	<string name="send_12">休眠时间</string>
	
//   	<string name="send_13">39码设置</string>
//	<string name="send_14">128码设置</string>
//   	<string name="send_15">Codabar码设置</string>
//   	<string name="send_16">i25码设置</string>
//	<string name="send_17">UPC/EAN码设置</string>
	public static String[] gongnengs = null;
	public static String[] cmds = new String[] {
			"{GB100}{G1000?}",
			"{GB100}{G1002?}",
			"{GB100}{G1004?}",
			"{GB100}{G1006?}", 
//			"{GB100}{G1010?}",
//			"{GB100}{G1012?}",
//			"{GB100}{G1014?}", 
//			"{GB100}{G1016?}",
//			"{GB100}{G1018?}",
			"{GB100}{G1020?}",
//			"{GB100}{G1022?}",
			"{GB100}{G1024?}",
			"{GB100}{G1026?}"
//			"{GB100}{G1028?}",
//			"{GB100}{G1030?}",
//			"{GB100}{G1032?}", 
//			"{GB100}{G1034?}",
//			"{GB100}{G1036?}" 
			};
	Context context;
	private ButtonOnClick buttonOnClick = new ButtonOnClick(1);

	public MyListConfig(Context context) {
		this.context = context;
		gongnengs = new String[]{  
				context.getString(MResource.getIdByName(context, "string","send_1")), 
				context.getString(MResource.getIdByName(context, "string","send_2")),
				context.getString(MResource.getIdByName(context, "string","send_3")),
				context.getString(MResource.getIdByName(context, "string","send_4")),
//				context.getString(MResource.getIdByName(context, "string","send_5")),
//				context.getString(MResource.getIdByName(context, "string","send_6")),
//				context.getString(MResource.getIdByName(context, "string","send_7")),
//				context.getString(MResource.getIdByName(context, "string","send_75")),
//				context.getString(MResource.getIdByName(context, "string","send_8")),
				context.getString(MResource.getIdByName(context, "string","send_9")),
//				context.getString(MResource.getIdByName(context, "string","send_10")),
				context.getString(MResource.getIdByName(context, "string","send_11")),
				context.getString(MResource.getIdByName(context, "string","send_12"))
//				context.getString(MResource.getIdByName(context, "string","send_13")),
//				context.getString(MResource.getIdByName(context, "string","send_14")),
//				context.getString(MResource.getIdByName(context, "string","send_15")),
//				context.getString(MResource.getIdByName(context, "string","send_16")),
//				context.getString(MResource.getIdByName(context, "string","send_17"))
				};
	}

	public void showSingleChoiceDialog() {

		Builder sb = new Builder(context);
		sb.setTitle(MResource.getIdByName(context, "string","send_device"));
		sb.setSingleChoiceItems(gongnengs, -1, buttonOnClick);
		Dialog dialog = sb.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

	}

	private class ButtonOnClick implements DialogInterface.OnClickListener {

		private int index;

		public ButtonOnClick(int index) {
			this.index = index;
			System.out.println("index" + index);
		}

		@Override
		public synchronized void onClick(DialogInterface dialog, int whichButton) {
			// whichButton 表示单击的按钮索引，所有列表项的索引都大于或等于0,而按钮的索引都小于0
			if (whichButton >= 0) {
//				
				index = whichButton;
				dialog.cancel(); // 单击列表项后立即关闭对话框
				String abcString = cmds[whichButton];
				MySendCMDindex = index;
				UsbConnect bluetoothConnect = new UsbConnect();
				bluetoothConnect.UsbSend(abcString,true);
//				TestSetActivity.getinput = 2;
//				ATCommandOutput cmd = new ATCommandOutput(abcString, "Start",
//						"开启扫描");
//				context.Sends(cmd);
//				if (!jingtian.isAlive()) {
//					jingtian = new Jingtian();
//					jingtian.start();
//				}
//				// OpenDialog( index);

			}

		}
	}

//	Jingtian jingtian = new Jingtian();
//
//	class Jingtian extends Thread {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			try {
//				ATService.handler.obtainMessage(6).sendToTarget();
//				sleep(2000);
//				ATService.handler.obtainMessage(7).sendToTarget();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//	}

}
