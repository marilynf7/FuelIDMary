package com.bim.usb.cmds;




import com.bimsdk.usb.MResource;
import com.bimsdk.usb.io.USBService;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog.Builder;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MyCmdInputType extends MyCmd {
//	点击《输出方式》：1、输入法发送：{GB100}{G1024？}
//    2、扫描器返回：{G1024/X}    X表示输出方式：“串口方式”，“USB 
//HID键盘方式”“蓝牙接口”“USB背夹模式”
//    3、输入法正确收到数据后弹出输出方式对话框并返回：{GB200}
//      选择输出方式后点击提交返回：{G1024/Y}{GB210}   Y表示修改后
//      的输出方式
//      输入法超时时间内没有收到数据，返回:{GB200}


	private String[] gongnengs = null;
//			new String[] { "串口方式", "USBHID键盘方式", "蓝牙接口",
//			"USB背夹模式"};

	private ButtonOnClick buttonOnClick = new ButtonOnClick(1);

	String input;
	USBService context;

	public MyCmdInputType(String input, USBService context) {
		this.input = input;
		this.context = context;
//		gongnengs = new String[]{context.getString(MResource.getIdByName(context, "string","command_inputtype_mode1),context.getString(MResource.getIdByName(context, "string","command_inputtype_mode2),
//				context.getString(MResource.getIdByName(context, "string","command_inputtype_mode3),context.getString(MResource.getIdByName(context, "string","command_inputtype_mode4)};
		gongnengs = new String[]{context.getString(MResource.getIdByName(context, "string","command_inputtype_mode1")),
				context.getString(MResource.getIdByName(context, "string","command_inputtype_mode2")),
				context.getString(MResource.getIdByName(context, "string","command_inputtype_mode3"))};
	
	
	};

	@Override
	public Dialog CreateAlertDialog() {// {G1030/X/YY}
		// TODO Auto-generated method stub
		MyCmdid = 0;
//		MyCmdName = "输出方式";//
		viewSize = 2;// 输入框一个 提示信息一个
		views = new View[viewSize];

		TextView TextViewA = new TextView(context);
		ToggleButton ToggleButtonA = new ToggleButton(context);



		String E= input.substring(7, 8);//{G1010/1/X}
//		String E = input.substring(9);
//		E = E.substring(0, E.length() - 1);
		myselect = Integer.valueOf(E)-1;

		views[0] = TextViewA;
		views[1] = ToggleButtonA;


		return getAlertDialog(context);
	}

	public Dialog getAlertDialog(final USBService context) {

		Dialog ad;
		Builder builder = new Builder(context);
		ScrollView sScrollView = new ScrollView(context); // view 太多无法全部显示用此
		
		LinearLayout lin = new LinearLayout(context);
		lin.setOrientation(1);
		for (int i = 0; i < viewSize; i++) {
			lin.addView(views[i]);
		}
		sScrollView.addView(lin);
		builder.setSingleChoiceItems(gongnengs, myselect, buttonOnClick);
//		builder.setView(sScrollView);
		builder.setTitle(MResource.getIdByName(context, "string","command_inputtype_name"));
	

		ad = builder.create();
//		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false); // 点击外面区域不会让dialog消失
		return ad;

	}

	int myselect=0;
	private class ButtonOnClick implements DialogInterface.OnClickListener {
		private int index;

		public ButtonOnClick(int index) {
			this.index = index;
			System.out.println("index" + index);
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			// whichButton 表示单击的按钮索引，所有列表项的索引都大于或等于0,而按钮的索引都小于0
			if (whichButton >= 0) {
				index = whichButton;
				dialog.cancel(); // 单击列表项后立即关闭对话框
				myselect =whichButton+1;
				OutCmdStr ="{G1024/"+myselect+"}{GB210}";
//				ATCommandOutput cmd =new ATCommandOutput(OutCmdStr,"","");
				context.Sends(OutCmdStr.getBytes());
			}

		}
	}

}
