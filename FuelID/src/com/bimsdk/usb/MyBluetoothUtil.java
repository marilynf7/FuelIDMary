package com.bimsdk.usb;

import java.util.Iterator;
import java.util.Set;



import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.WindowManager;



@SuppressLint("NewApi")
public class MyBluetoothUtil  {
	

	public static final String BLUETOOTH_LIST_KEY = "bluetooth_list_preference";
	private String[] provinces =null;
	private String[] devicemacs =null;
 
	SharedPreferences prefs;
	Context context;
	private ButtonOnClick buttonOnClick = new ButtonOnClick(1);


	public MyBluetoothUtil(Context context){
		this.context=context;
		 prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}



	public String GetBluetoothAddress()
	{
		return prefs.getString(BLUETOOTH_LIST_KEY, null);
	}
	
	
	public void showSingleChoiceDialog()
	{
		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		Set<BluetoothDevice> BluetoothDevices =mBluetoothAdapter.getBondedDevices();
		int length =BluetoothDevices.size();
		Iterator<BluetoothDevice> it = BluetoothDevices.iterator(); 
		provinces = new String[length];
		devicemacs= new String[length];
		for (int i = 0; i < length; i++) {
			BluetoothDevice device = it.next();
			provinces[i] =device.getName()+"\n"+device.getAddress();
			devicemacs[i]=device.getAddress();
		}

	
		Builder sb =new Builder(context); 
		sb.setTitle(MResource.getIdByName(context, "string", "textView_con_6"));
		sb.setSingleChoiceItems(provinces, prefs.getInt("index", 0), buttonOnClick);
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
		public void onClick(DialogInterface dialog, int whichButton)
		{
			//whichButton 表示单击的按钮索引，所有列表项的索引都大于或等于0,而按钮的索引都小于0
			if (whichButton >= 0)
			{
				index = whichButton;
				prefs.edit()
				.putString(BLUETOOTH_LIST_KEY, devicemacs[whichButton]).commit();
				prefs.edit()
				.putInt("index", whichButton).commit();
				
				 dialog.cancel(); 	//单击列表项后立即关闭对话框
		
			}

		}
	}
}
