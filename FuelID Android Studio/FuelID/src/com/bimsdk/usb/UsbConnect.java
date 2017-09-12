package com.bimsdk.usb;


import com.bimsdk.usb.io.OnDataReceive;
import com.bimsdk.usb.io.USBService;


import android.content.Context;
import android.content.Intent;

public class UsbConnect {


	public static void Connect(Context context) {
	//	System.out.println("=======Connect==========");
		Intent	 intent = new Intent(context, USBService.class);
		context.startService(intent);
	}
	public static boolean isConnect(Context context) {
		 return USBService.usbstaicflg;
	}

	
	public static void Stop(Context context) {
		
		if(USBService.myUSBService!=null){
			USBService.myUSBService.stopSelf();
		}
		
	}
	/**
	 * 方法概述：透过手持设备usb 发送字符串数据到外接usb设备
	 * 参数：Context context为activity或service
	 * 返回：viod
	 * */
	public static void UsbSend(byte[] data) {
		if (data!=null &&data.length>0) {
			USBService.Sends(data);
		}
		
	}
	/**
	 * 方法概述：监听外接设备发送到手机上的数据
	 * 参数：OnDataReceive onDataReceive里面的activity或service
	 * 返回：viod
	 * */

	public static void SetOnDataReceive(OnDataReceive onDataReceive) {
		USBService.addListener(onDataReceive);
	}
	
	public  void UsbSend(String context,boolean DialogShow) {
//		context = "abcd";
//		ATCommandOutput cmd = new ATCommandOutput(context, context, context);
		if (DialogShow) {
			if (!jingtian.isAlive()) {
				jingtian = new Jingtian();
				jingtian.start();
			}
			UsbSend(context.getBytes());
		}else {
			UsbSend(context.getBytes());
		}
	
	}
//	
	 Jingtian jingtian = new Jingtian();
//
	class Jingtian extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				USBService.handler.obtainMessage(USBService.HANDLER_OUTPUT_CREATEIALOG).sendToTarget();// 显示Dialog
				sleep(2200);
				USBService.handler.obtainMessage(USBService.HANDLER_OUTPUT_CLOSEDIALOG).sendToTarget();// 关闭Dialog
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
