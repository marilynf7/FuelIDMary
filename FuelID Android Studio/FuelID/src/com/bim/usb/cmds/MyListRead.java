package com.bim.usb.cmds;

import com.bimsdk.usb.MResource;
import com.bimsdk.usb.UsbConnect;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;


public class MyListRead  {
	

	public static  int MyGetDeviceSelectIndex = 0;
	public static String[] gongnengs =null;
	public static String[] cmds = new String[]{  "{GB100}{G2012}", "{GB100}{G2014}", "{GB100}{G2016}"};
	Context context;
	private ButtonOnClick buttonOnClick = new ButtonOnClick(1);


	public MyListRead(Context context){
		this.context=context;
		gongnengs = new String[]{  context.getString(MResource.getIdByName(context, "string","send_18")), 
				context.getString(MResource.getIdByName(context, "string","send_19")),
				context.getString(MResource.getIdByName(context, "string","send_20"))};
	}



//	public String GetBluetoothAddress()
//	{
//		return prefs.getString(BLUETOOTH_LIST_KEY, null);
//	}
	
	
	public void showSingleChoiceDialog()
	{
		Builder sb =new Builder(context); 
		sb.setTitle(MResource.getIdByName(context, "string","get_device"));
		sb.setSingleChoiceItems(gongnengs, -1, buttonOnClick);
		Dialog dialog  =sb.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	
		
	}

	
	private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		private int index;
		public ButtonOnClick(int index)
		{
			this.index = index;
			System.out.println("index"+index);
		}

		@Override
		public synchronized void onClick(DialogInterface dialog, int whichButton)
		{
			//whichButton 表示单击的按钮索引，所有列表项的索引都大于或等于0,而按钮的索引都小于0
			if (whichButton >= 0)
			{
				String abcString =cmds[whichButton];
				UsbConnect.UsbSend(abcString.getBytes());
//				if (TestSetActivity.islive) {
//					index = whichButton;
//					MyGetDeviceSelectIndex =index;
//					TestSetActivity.getinput =1;
//			
//					 String abcString =cmds[whichButton];
//						ATCommandOutput cmd = new ATCommandOutput(abcString, "Start",
//								"开启扫描");
//						context.Sends(cmd);
//						
//						if (jingtian==null||!jingtian.isAlive()) {
//							jingtian = new Jingtian();
//							jingtian.start();
//						}
				}
		
				 dialog.cancel(); 	//单击列表项后立即关闭对话框
					
			}

		}
//	}
//public static	Jingtian jingtian ;
//	public class Jingtian extends Thread{
//           boolean stop =false;
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			try {
//				ATService.handler.obtainMessage(6).sendToTarget();
//					sleep(2000);	
//				ATService.handler.obtainMessage(7).sendToTarget();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		public	void Stop(){
//			stop =true;
//		}
//		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	Dialog mydialog = null;

	private void show() {
		Builder sb = new Builder(context);
		sb.setTitle("等待收取数据");
		mydialog = sb.create();
		mydialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mydialog.show();
	}

	private void cancel() {
		if (mydialog != null) {
			mydialog.cancel();
		}

	}
}
