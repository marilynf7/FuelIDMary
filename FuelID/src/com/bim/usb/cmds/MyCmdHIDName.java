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
import android.widget.TextView;

public class MyCmdHIDName extends MyCmd {
//	点击《HID蓝牙名称》：1、输入法发送：{GB100}{G1002？}，
//    2、扫描器返回：{G1002/XXXXX}   XXXXX表示HID名称
//3、输入法正确收到数据后弹出修改蓝牙名称对话框并返回{GB200}，修改完蓝牙名称确认后返回：{G1002/YYYYY}{GB201}     YYYYY表示修改后的蓝牙名称
//   输入法超时时间内没有收到数据，返回:{GB200}

	
	String input;
	USBService context;

	public MyCmdHIDName(String input, USBService context) {
		this.input = input;
		this.context = context;
	};

	@Override
	public AlertDialog CreateAlertDialog() {// input :{G1000/XXXXX}
											// out:{G1000/YYYYY}{GB201}
		// TODO Auto-generated method stub
		MyCmdid = 16;
//		MyCmdName = "HID蓝牙名称";// 点击《SPP蓝牙名称》：
		viewSize = 2;// 输入框一个 提示信息一个
		views= new View[viewSize];
		TextView my1View = new TextView(context);
		my1View.setText(MResource.getIdByName(context, "string","command_hid_input"));
		views[0] = my1View;
		EditText editText = new EditText(context);
		// {G1000/XXXXX}
		// string=string.substring(7);
		String mycmd1 = input.substring(7);// 去除前面 {G1000/
		mycmd1 = mycmd1.substring(0, mycmd1.length() - 1);// 去除最后一个 }
		editText.setText(mycmd1);
		views[1] = editText;

		return getAlertDialog(context);
	}

	public AlertDialog getAlertDialog(final USBService context) {

		AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LinearLayout lin = new LinearLayout(context);
		lin.setOrientation(1);
		for (int i = 0; i < viewSize; i++) {
			lin.addView(views[i]);
		}

		builder.setView(lin);

		builder.setMessage(MResource.getIdByName(context, "string","command_hid_name"))
				.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText editText = (EditText) views[1];// 把数组index=1
																// view
																// 转化为EditText
						String string = editText.getText().toString().trim();
						if (string.length() > 0) {
							OutCmdStr = "{G1002/" + string + "}{GB201}";// {G1000/YYYYY}{GB201}
//							ATCommandOutput cmd =new ATCommandOutput(OutCmdStr,"","");
							context.Sends(OutCmdStr.getBytes());
							System.out.println("OutCmdStr"+OutCmdStr+"");
							dialog.dismiss();
						}

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
