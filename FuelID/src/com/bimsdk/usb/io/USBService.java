package com.bimsdk.usb.io;



import com.bim.usb.cmds.MyCMDOperate;

//import com.bimsdk.bluetooth.MResource;
import com.bimsdk.usb.UsbConnect;
import com.bimsdk.usb.Util;
import com.bimsdk.usb.io.FT311UARTInterface.CWiriteIndex;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

public class USBService extends Service {

//	public static Activity clientActivity;
	public static USBService myUSBService;
	

	
	public static FT311UARTInterface uartInterface;
	public SharedPreferences sharePrefSettings;
	public boolean bConfiged = false;
	int baudRate =115200;; /* baud rate */
	byte stopBit=1; /* 1:1stop bits, 2:2 stop bits */
	byte dataBit=8; /* 8:8bit, 7: 7bit */
	byte parity=0; /* 0: none, 1: odd, 2: even, 3: mark, 4: space */
	byte flowControl=0; /* 0:none, 1: flow control(CTS,RTS) */
	
	public final static int HANDLER_OUTPUT_CLOSEDIALOG =14;
	public final static int HANDLER_OUTPUT_CREATEIALOG=13;
	public static final int HANDLER_INPUT_CMD =8;
	static AlertDialog progressDialog = null;
	private static String cmdDataStr="";
	static MyCMDOperate myCMDOperate = new MyCMDOperate();
	public static Handler handler = new Handler() {
		@Override
		public   void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 10://
				break;
			case 9:  //接收数据
				//old  old1
//				if (uartInterface.GetDatabuffer!=null) {
//				
//					if (MyCMDOperate.checkMyCmd(new String(readData))) {
//						cmdDataStr =new String(readData);
//						handler.obtainMessage(HANDLER_INPUT_CMD).sendToTarget();
//					}else {
//						mDataReceive.DataReceive(readData, true);
//					}
//				}else {
//					System.out.println("uartInterface.GetDatabuffer null");
//				}
				//old  old1
				byte[] data  =(byte[]) msg.obj;
				System.out.println("accept new String(data):"+new String(data));
//				if (uartInterface.CGetDatabuffer!=null) {
//					byte[] data =  new byte[uartInterface.CGetDatabuffer.length];
//					System.arraycopy(uartInterface.CGetDatabuffer, 0, data, 0, uartInterface.CGetDatabuffer.length);
					if (MyCMDOperate.checkMyCmd(new String(data))) {
					cmdDataStr =new String(data);
					handler.obtainMessage(HANDLER_INPUT_CMD).sendToTarget();
					}else {
						mDataReceive.DataReceive(data, true);
					}
//				}
				
				break;
			case HANDLER_INPUT_CMD:
				if (progressDialog!=null) {
					progressDialog.dismiss();
				}
			
				Dialog myDialog=myCMDOperate.export(cmdDataStr,myUSBService);
				
				if (myDialog!=null) {
					if (progressDialog!=null&&progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					myDialog.show();
					UsbConnect.UsbSend("{GB200}".getBytes());
				}
				break;
				case HANDLER_OUTPUT_CREATEIALOG:
					progressDialog =Util.getProgressDialog(myUSBService);
					progressDialog.show();
				break;
				case HANDLER_OUTPUT_CLOSEDIALOG:
					if (progressDialog!=null&&progressDialog.isShowing()) {
						progressDialog.dismiss();
						UsbConnect.UsbSend("{GB200}".getBytes());
					}
					break;
			case 0:// 关闭
				
				
				notifyMan.cancel(OBD_SERVICE_RUNNING_NOTIFY);
				//Toast.makeText(myUSBService, "", Toast.LENGTH_SHORT).show();
				readData = new byte[0];
				mDataReceive.DataReceive(readData, false);
//				nameDialog("guanbi", myUSBService);
				usbstaicflg = false;
				myUSBService.stopSelf();
				break;
			default: 
			
			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	/* thread to read the data */
	private static NotificationManager notifyMan = null;
	private Intent notificationIntent = null;
	private static PendingIntent contentIntent = null;
	public static final int OBD_SERVICE_RUNNING_NOTIFY = 4;
	public static final int OBD_SERVICE_ERROR_NOTIFY = 5;
	@Override
	public void onCreate() {  
		// TODO Auto-generated method stub
		super.onCreate();
		myUSBService =this;
		notifyMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationIntent = new Intent(this, USBService.class);
		contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				0);
		sharePrefSettings = getSharedPreferences("UARTLBPref", 0);
	

	}

	public static boolean usbstaicflg = false;
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		uartInterface = new FT311UARTInterface(myUSBService, sharePrefSettings);
		uartInterface.SetHandler(handler);
		savePreference();
		int c = uartInterface.ResumeAccessory();
		
		if( 2 == c )
		{
			cleanPreference();
			restorePreference();
			usbstaicflg = false;
			stopSelf();
		
		}else if (0 == c) {

			long when = System.currentTimeMillis();

			usbstaicflg =uartInterface.SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
			if (usbstaicflg&&(readThread==null||!readThread.isAlive())) {
//				readThread = new readThread();
//				readThread.setPriority(Thread.NORM_PRIORITY+3);
//				readThread.start();
				Notification notification1 = new Notification(R.drawable.btn_default, "Get data from usb", when);
				notification1.setLatestEventInfo(myUSBService, "usb connected", "", contentIntent);
				notification1.flags |= Notification.FLAG_NO_CLEAR;
				notification1.flags |= Notification.FLAG_ONGOING_EVENT;
				notifyMan.notify(OBD_SERVICE_RUNNING_NOTIFY, notification1);
			}else  {
				usbstaicflg = false;
				stopSelf();
			}
			
		

		}else if (1==c) {
			usbstaicflg = false;
			stopSelf();
		}
	

		
	}  

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		notifyMan.cancel(OBD_SERVICE_RUNNING_NOTIFY);
		usbstaicflg = false;
		if (readThread!=null&&readThread.isAlive()) {
			readThread.Stop();
		}
		if (uartInterface!=null) {
			uartInterface.DestroyAccessory(bConfiged);
//			USBService.nameDialog("stop", this);
		}
	
	}

	
	
	protected void cleanPreference(){
		SharedPreferences.Editor editor = sharePrefSettings.edit();
		editor.remove("configed");
		editor.remove("baudRate");
		editor.remove("stopBit");
		editor.remove("dataBit");
		editor.remove("parity");
		editor.remove("flowControl");
		editor.commit();
	}

	protected void savePreference() {
		if(true == bConfiged){
			sharePrefSettings.edit().putString("configed", "TRUE").commit();
			sharePrefSettings.edit().putInt("baudRate", baudRate).commit();
			sharePrefSettings.edit().putInt("stopBit", stopBit).commit();
			sharePrefSettings.edit().putInt("dataBit", dataBit).commit();
			sharePrefSettings.edit().putInt("parity", parity).commit();			
			sharePrefSettings.edit().putInt("flowControl", flowControl).commit();			
		}
		else{
			sharePrefSettings.edit().putString("configed", "FALSE").commit();
		}
	}
	
	protected void restorePreference() {
		String key_name = sharePrefSettings.getString("configed", "");
		if(true == key_name.contains("TRUE")){
			bConfiged = true;
		}
		else{
			bConfiged = false;
        }
		
		baudRate = sharePrefSettings.getInt("baudRate", 115200);
		stopBit = (byte)sharePrefSettings.getInt("stopBit", 1);
		dataBit = (byte)sharePrefSettings.getInt("dataBit", 8);
		parity = (byte)sharePrefSettings.getInt("parity", 0);
		flowControl = (byte)sharePrefSettings.getInt("flowControl", 0);

	}

	byte status;
	int[] actualNumBytes = new int[4096];

	
	
	// 发送数据
	public static void Sends(byte[] data) {

		if (uartInterface==null) {
			System.out.println("----send error------:"+new String(data));
		}else {
			System.out.println("----send success------:"+new String(data));
			uartInterface.SendData(data.length, data);
		}
		
	}

	static OnDataReceive mDataReceive;

	public static void addListener(OnDataReceive onDataReceive) {
		mDataReceive = onDataReceive;
	}
	
   
	static byte[] readData =null;
	readThread readThread =null;
	int readindex =0;
	class readThread  extends Thread{
		boolean runflg = true;
		readThread(){
			runflg = true;
		}
		@Override
		public  void run() {
			// TODO Auto-generated method stub
			super.run();
			// old
//			while (runflg) {
//				if (uartInterface.wirteindex > readindex) {
//					int length = uartInterface.wirteindex-readindex;
//					readData  = new byte[length];
//					System.arraycopy(uartInterface.GetDatabuffer, readindex, readData, 0, length);
//					handler.obtainMessage(9).sendToTarget();
//					readindex =readindex+length;
//
//				}else if (readindex>=uartInterface.wirteindex) {
//					readindex =uartInterface.wirteindex;
//				}
//		
//			}
			// old
//			while (runflg) {
//				while (uartInterface.cWiriteIndex.size()>0) {
//					CWiriteIndex cwIndex =  uartInterface.cWiriteIndex.remove(0);
//					readData  = new byte[cwIndex.readcount];
//					System.arraycopy(uartInterface.GetDatabuffer, (cwIndex.wirteindex-cwIndex.readcount), readData, 0, cwIndex.readcount);
//						handler.obtainMessage(9).sendToTarget();
//				}
//				try {
//					sleep(50);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			
		}
		public void Stop(){
			runflg =false;
		}	
		
	}


//  public static void nameDialog(String str,Context context) {
// 
//      
//  	AlertDialog.Builder builer = new AlertDialog.Builder(context);
//  	builer.setTitle(str);
//  	builer.setPositiveButton("ss", new OnClickListener() {
//		
//		@Override
//		public void onClick(DialogInterface arg0, int arg1) {
//			// TODO Auto-generated method stub
//			arg0.cancel();
//		}
//	});
//	Dialog dialog  =builer.create();
//	dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//	dialog.show();
//}

}
