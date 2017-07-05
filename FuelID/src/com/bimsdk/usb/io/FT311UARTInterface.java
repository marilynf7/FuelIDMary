//User must modify the below package with their package name
package com.bimsdk.usb.io;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;


/******************************FT311 GPIO interface class******************************************/
public class FT311UARTInterface extends Activity
{

	private static final String ACTION_USB_PERMISSION =    "com.bimsdk.USB_PERMISSION";	
	public UsbManager usbmanager;
	public UsbAccessory usbaccessory;
	public PendingIntent mPermissionIntent;
	public ParcelFileDescriptor filedescriptor = null;
	public FileInputStream inputstream = null;
	public FileOutputStream outputstream = null;
	public boolean mPermissionRequestPending = false;
	public read_thread readThread;

	private byte [] usbdata; 
	private byte []	writeusbdata;
	private byte [] readBuffer; /*circular buffer*/
	private int readcount;
	private int totalBytes;
	private int writeIndex;
	private int readIndex;
	private byte status;
	final int  maxnumbytes = 65536;

	public boolean datareceived = false;
	public boolean READ_ENABLE = false;
	public boolean accessory_attached = false;

	public Context global_context;

	public static String ManufacturerString = "mManufacturer=FTDI";
	public static String ModelString1 = "mModel=FTDIUARTDemo";
	public static String ModelString2 = "mModel=Android Accessory FT312D";
	public static String VersionString = "mVersion=1.0";

	public SharedPreferences intsharePrefSettings;

	private Handler handler;
	public void SetHandler(Handler handler){
		this.handler = handler;
	}

	/*constructor*/
	public FT311UARTInterface(Context context, SharedPreferences sharePrefSettings){
		super();
		global_context = context;
		intsharePrefSettings = sharePrefSettings;
		/*shall we start a thread here or what*/
		usbdata = new byte[1024]; 
		writeusbdata = new byte[256];
		/*128(make it 256, but looks like bytes should be enough)*/
		readBuffer = new byte [maxnumbytes];


		readIndex = 0;
		writeIndex = 0;
		/***********************USB handling******************************************/

		usbmanager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		// Log.d("LED", "usbmanager" +usbmanager);
		mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		context.registerReceiver(mUsbReceiver, filter);

		inputstream = null;
		outputstream = null;
	}


	public boolean SetConfig(int baud, byte dataBits, byte stopBits,
			byte parity, byte flowControl)
	{

		System.out.println("baud"+baud);
		System.out.println("dataBits"+dataBits);
		System.out.println("stopBits"+stopBits);
		System.out.println("parity"+parity);
		System.out.println("flowControl"+flowControl);
		/*prepare the baud rate buffer*/
		writeusbdata[0] = (byte)baud;
		writeusbdata[1] = (byte)(baud >> 8);
		writeusbdata[2] = (byte)(baud >> 16);
		writeusbdata[3] = (byte)(baud >> 24);

		/*data bits*/
		writeusbdata[4] = dataBits;
		/*stop bits*/
		writeusbdata[5] = stopBits;
		/*parity*/
		writeusbdata[6] = parity;
		/*flow control*/
		writeusbdata[7] = flowControl;

		/*send the UART configuration packet*/
		return SendPacket((int)8);
	}


	/*write data*/ 
	public byte SendData(int numBytes, byte[] buffer) 					     
	{
		status = 0x00; /*success by default*/
		/*
		 * if num bytes are more than maximum limit
		 */
		if(numBytes < 1){
			/*return the status with the error in the command*/
			return status;
		}
	 		
		/*check for maximum limit*/
		if(numBytes > 256){
			numBytes = 256;
		}

		/*prepare the packet to be sent*/
		for(int count = 0;count<numBytes;count++)
		{	
			writeusbdata[count] = buffer[count];
		}

		if(numBytes != 64)
		{
			SendPacket(numBytes);
		}
		else
		{
			byte temp = writeusbdata[63];
			SendPacket(63);
			writeusbdata[0] = temp;
			SendPacket(1);
		}

		return status;
	}
  
	
	
	/*read data*/
	public byte ReadData(int numBytes,byte[] buffer, int [] actualNumBytes)
	{
		status = 0x00; /*success by default*/

		/*should be at least one byte to read*/
		if((numBytes < 1) || (totalBytes == 0)){
			actualNumBytes[0] = 0;
			status = 0x01;
			return status;
		}

		/*check for max limit*/
		if(numBytes > totalBytes)
			numBytes = totalBytes;

		/*update the number of bytes available*/
		totalBytes -= numBytes;

		actualNumBytes[0] = numBytes;	

		/*copy to the user buffer*/	
		for(int count = 0; count<numBytes;count++)
		{
			buffer[count] = readBuffer[readIndex];
			readIndex++;
			/*shouldnt read more than what is there in the buffer,
			 * 	so no need to check the overflow
			 */
			readIndex %= maxnumbytes;
		}
		return status;
	}

	

	public	int max = 60*1024;
	public byte[] GetDatabuffer = new byte[max];
//	public byte[] CGetDatabuffer =null;
	public int wirteindex = 0;
	/*read data*/
	public byte ReadData(int numBytes, int [] actualNumBytes)
	{
		status = 0x00; /*success by default*/

		/*should be at least one byte to read*/
		if((numBytes < 1) || (totalBytes == 0)){
			actualNumBytes[0] = 0;
			status = 0x01;
			return status;
		}

		/*check for max limit*/
		if(numBytes > totalBytes)
			numBytes = totalBytes;

		/*update the number of bytes available*/
		totalBytes -= numBytes;

		actualNumBytes[0] = numBytes;	

		/*copy to the user buffer*/	
		for(int count = 0; count<numBytes;count++)
		{
			GetDatabuffer[count] = readBuffer[readIndex];
			readIndex++;
			/*shouldnt read more than what is there in the buffer,
			 * 	so no need to check the overflow
			 */
			readIndex %= maxnumbytes;
		}
		return status;
	}
	/*method to send on USB*/
	private boolean SendPacket(int numBytes)
	{	
		boolean usbconflg = true;
		try {
			if(outputstream != null){
//				System.out.println("--SendPacket--writeusbdata------:"+new String(writeusbdata));
				outputstream.write(writeusbdata, 0,numBytes);
				usbconflg =true;
			}else {
				usbconflg =false;
//				System.out.println("outputstream null-------");
			}
		} catch (IOException e) {
			usbconflg =false;
			System.out.println("e:"+e.toString());
			e.printStackTrace();
		}
		return usbconflg;
	}

	/*resume accessory*/
	public int ResumeAccessory()
	{
		// Intent intent = getIntent();
		if (inputstream != null && outputstream != null) {
			return 1;
		}

		UsbAccessory[] accessories = usbmanager.getAccessoryList();
		if(accessories != null)
		{
			Toast.makeText(global_context, "Accessory Attached", Toast.LENGTH_SHORT).show();
		}		
		else
		{
			// return 2 for accessory detached case
			//Log.e(">>@@","ResumeAccessory RETURN 2 (accessories == null)");
			accessory_attached = false;

			return 2;
		}

		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if( -1 == accessory.toString().indexOf(ManufacturerString))
			{
				Toast.makeText(global_context, "Manufacturer is not matched!", Toast.LENGTH_SHORT).show();
				return 1;
			}

			if( -1 == accessory.toString().indexOf(ModelString1) && -1 == accessory.toString().indexOf(ModelString2))
			{
				Toast.makeText(global_context, "Model is not matched!", Toast.LENGTH_SHORT).show();
				return 1;
			}

			if( -1 == accessory.toString().indexOf(VersionString))
			{
				Toast.makeText(global_context, "Version is not matched!", Toast.LENGTH_SHORT).show();
				return 1;
			}

			Toast.makeText(global_context, "Manufacturer, Model & Version are matched!", Toast.LENGTH_SHORT).show();
			accessory_attached = true;

			if (usbmanager.hasPermission(accessory)) {
				OpenAccessory(accessory);
			} 
			else
			{
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						Toast.makeText(global_context, "Request USB Permission", Toast.LENGTH_SHORT).show();
						usbmanager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {}

		return 0;
	}

	/*destroy accessory*/
	public void DestroyAccessory(boolean bConfiged){

		if(true == bConfiged){
			READ_ENABLE = false;  // set false condition for handler_thread to exit waiting data loop
			writeusbdata[0] = 0;  // send dummy data for instream.read going
			SendPacket(1);
		}
		else
		{
			SetConfig(115200,(byte)1,(byte)8,(byte)0,(byte)0);  // send default setting data for config
			try{Thread.sleep(10);}
			catch(Exception e){}

			READ_ENABLE = false;  // set false condition for handler_thread to exit waiting data loop
			writeusbdata[0] = 0;  // send dummy data for instream.read going
			SendPacket(1);
			if(true == accessory_attached)
			{
				saveDefaultPreference();
			}
		}

		try{Thread.sleep(10);}
		catch(Exception e){}			
		CloseAccessory();
	}

	/*********************helper routines*************************************************/		

	public void OpenAccessory(UsbAccessory accessory)
	{	
		filedescriptor = usbmanager.openAccessory(accessory);
		if(filedescriptor != null){
			usbaccessory = accessory;

			FileDescriptor fd = filedescriptor.getFileDescriptor();

			inputstream = new FileInputStream(fd);
			outputstream = new FileOutputStream(fd);
			/*check if any of them are null*/
			if(inputstream == null || outputstream==null){
				System.out.println("OpenAccessory inputstream == null");
				return;
			}

			if(READ_ENABLE == false){
				READ_ENABLE = true;
				readThread = new read_thread(inputstream);
				readThread.start();
			}
		}else {
			System.out.println("filedescriptor===null");
		}
	}

	private void CloseAccessory()
	{
		try{
			if(filedescriptor != null)
				filedescriptor.close();

		}catch (IOException e){}

		try {
			if(inputstream != null)
				inputstream.close();
		} catch(IOException e){}

		try {
			if(outputstream != null)
				outputstream.close();

		}catch(IOException e){}
		/*FIXME, add the notfication also to close the application*/

		filedescriptor = null;
		inputstream = null;
		outputstream = null;
		handler.obtainMessage(0).sendToTarget();

//		System.exit(0);
	}

	protected void saveDetachPreference() {
		if(intsharePrefSettings != null)
		{
			intsharePrefSettings.edit()
			.putString("configed", "FALSE")
			.commit();
		}
	}

	protected void saveDefaultPreference() {
		if(intsharePrefSettings != null)
		{
			intsharePrefSettings.edit().putString("configed", "TRUE").commit();
			intsharePrefSettings.edit().putInt("baudRate", 115200).commit();
			intsharePrefSettings.edit().putInt("stopBit", 1).commit();
			intsharePrefSettings.edit().putInt("dataBit", 8).commit();
			intsharePrefSettings.edit().putInt("parity", 0).commit();			
			intsharePrefSettings.edit().putInt("flowControl", 0).commit();
		}
	}
//----------------
//----------------
	/***********USB broadcast receiver*******************************************/
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			System.out.println("mUsbReceiver=====action:"+action);
			
//			Toast.makeText(global_context, action, Toast.LENGTH_SHORT).show();
			
//			USBService.nameDialog(action, global_context);
			if (ACTION_USB_PERMISSION.equals(action)) 
			{
				System.out.println("mUsbReceiver1:");
				synchronized (this)
				{
					System.out.println("mUsbReceiver2:");
					UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
					{
						System.out.println("mUsbReceiver3:");
						Toast.makeText(global_context, "Allow USB Permission", Toast.LENGTH_SHORT).show();
						OpenAccessory(accessory);
					} 
					else 
					{
						System.out.println("mUsbReceiver4:");
						Toast.makeText(global_context, "Deny USB Permission", Toast.LENGTH_SHORT).show();
						Log.d("LED", "permission denied for accessory "+ accessory);

					}
					mPermissionRequestPending = false;
				}
			} 
			else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) 
			{
//				System.out.println("mUsbReceiver14:");
//				USBService.nameDialog("断开", context);
				saveDetachPreference();
				DestroyAccessory(true);
			
				//CloseAccessory();
			}else
			{
				System.out.println("mUsbReceiver15:");
				Log.d("LED", "....");
			}
		}	
	};

	/*usb input data handler*/
	private class read_thread  extends Thread 
	{
		FileInputStream instream;
//		int wirteindex = 0;
		read_thread(FileInputStream stream ){
			instream = stream;
			this.setPriority(Thread.MAX_PRIORITY);
//			wirteindex =0;
		}

		public  void run()
		{
			
			while(READ_ENABLE == true)
			{
//				while(totalBytes > (maxnumbytes - 1024))
//				{
//					try 
//					{
//						Thread.sleep(50);
//					}
//					catch (InterruptedException e) {e.printStackTrace();}
//				}

				try
				{
					if(instream != null)
					{
						usbdata = new byte[1024];
			
						readcount = instream.read(usbdata,0,1024);
//						System.out.println("==============2");
						if(readcount > 0)
						{
							
							//old
//							if ((wirteindex+readcount)>max) {// 
//								System.arraycopy(usbdata, 0, GetDatabuffer, 0, readcount);
//								wirteindex = readcount;
//							}else {
//								System.arraycopy(usbdata, 0, GetDatabuffer, wirteindex, readcount);
//								wirteindex =wirteindex+readcount;
//							}
							//old
							
							//old1
//							if ((wirteindex+readcount)>max) {// 
//							System.arraycopy(usbdata, 0, GetDatabuffer, 0, readcount);
//							wirteindex = readcount;
//							}else {
//								System.arraycopy(usbdata, 0, GetDatabuffer, wirteindex, readcount);
//								wirteindex =wirteindex+readcount;
//							}
//							cWiriteIndex.add(new CWiriteIndex(wirteindex,readcount));
							//old1
							
							byte[]	CGetDatabuffer =  new byte[readcount];
							System.arraycopy(usbdata, 0, CGetDatabuffer, 0, readcount);
							handler.obtainMessage(9, CGetDatabuffer).sendToTarget();
//							handler.obtainMessage(9).sendToTarget();
							
						}
					}
				}
				catch (IOException e){e.printStackTrace();}
			}				
		}
	}
ArrayList<CWiriteIndex> cWiriteIndex = new ArrayList<CWiriteIndex>();
	

public class  CWiriteIndex {
		int wirteindex = 0;
		int readcount = 0;
	public	CWiriteIndex(int wirteindex,int readcount){
			this.wirteindex = wirteindex;
			this.readcount =readcount;
		}
	}

}