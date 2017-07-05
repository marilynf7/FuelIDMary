package com.bim.usb.cmds;




import com.bimsdk.usb.MResource;
import com.bimsdk.usb.io.USBService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MyCmdDuTime extends MyCmd{
//	点击《读取条码测试时间》：1、输入法发送：{GB100}{G1012？}
//    2、扫描器返回：{G1012/X}       X表示1为开，0为关
//    3、输入法正确收到数据后弹出读取条码测试时间对话框返回{GB200}，每点击一下状态开关就却换一下开关状态，
//点击提交后返回：{G1012/Y}{GB204}  Y表示状态开关1或0
//输入法超时时间内没有收到数据，返回:{GB200} 




	String input;
	USBService context;

	public MyCmdDuTime(String input, USBService context) {
		this.input = input;
		this.context = context;
	};

	@Override
	public AlertDialog CreateAlertDialog() {// {G1012/X}
		// TODO Auto-generated method stub
		MyCmdid = 0;
//		MyCmdName = "读取条码测试时间";//
		viewSize = 2;// 输入框一个 提示信息一个
		views = new View[viewSize];

		TextView TextViewA = new TextView(context);
		ToggleButton ToggleButtonA = new ToggleButton(context);
		String A = input.substring(7, 8);
		TextViewA.setText(MResource.getIdByName(context, "string","command_dutime_switch"));
		ToggleButtonA.setChecked(Util.CheckStr(A));
		views[0] = TextViewA;
		views[1] = ToggleButtonA;


		return getAlertDialog(context);
	}

	public AlertDialog getAlertDialog(final USBService context) {

		AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		ScrollView sScrollView = new ScrollView(context); // view 太多无法全部显示用此
		
		
		LinearLayout lin = new LinearLayout(context);

		lin.setOrientation(1);
		for (int i = 0; i < viewSize; i++) {
			lin.addView(views[i]);
		}
		sScrollView.addView(lin);
		builder.setView(sScrollView);

		builder.setMessage(MResource.getIdByName(context, "string","command_dutime_name"))
				.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						EditText editText = (EditText) views[viewSize-1];// 
						String string1 = "";
		
							ToggleButton myoggleButton = (ToggleButton) views[1];
							string1 = string1
									+ "/"
									+ Util.Checkboolean(myoggleButton
											.isChecked());
							OutCmdStr = "{G1012"  + string1 
									+ "}{GB204}";// {G1000/YYYYY}{GB201}
							context.Sends(OutCmdStr.getBytes());
							System.out.println("OutCmdStr" + OutCmdStr + "");
							dialog.dismiss();
					}
				})
				.setNegativeButton(MResource.getIdByName(context, "string","command_button_cancel"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		ad = builder.create();
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false); // 点击外面区域不会让dialog消失
		return ad;

	}

}
