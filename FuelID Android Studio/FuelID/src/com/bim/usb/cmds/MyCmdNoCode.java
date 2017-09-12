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

public class MyCmdNoCode extends MyCmd{
//	点击《无解码输出》：1、输入法发送：{GB100}{G1016？}
//    2、扫描器返回：{G1016/X/YYY}  X表示状态开关，YYY表示无
//       解码时需要输出的字符
//    3、输入法正确收到数据后弹出无解码输出对话框并返回：{GB200}
//      点击提交后返回：{G1016/Z/AAA} {GB206}   z表示状态开关 
//      AAA表示修改后的无解码时需要输出的字符
//      输入法超时时间内没有收到数据，返回:{GB200}


	String input;
	USBService context;

	public MyCmdNoCode(String input, USBService context) {
		this.input = input;
		this.context = context;
	};

	@Override
	public AlertDialog CreateAlertDialog() {// {G1016/X/YYY}
		// TODO Auto-generated method stub
		MyCmdid = 0;
//		MyCmdName = "无解码输出";//
		viewSize = 4;// 输入框一个 提示信息一个
		views = new View[viewSize];

		TextView TextViewA = new TextView(context);
		ToggleButton ToggleButtonA = new ToggleButton(context);

		TextView TextViewH = new TextView(context);
		EditText EditTextH = new EditText(context);
		
		String A = input.substring(7, 8);
		String H  = input.substring(9);
		H = H.substring(0, H.length()-1);
		
		TextViewA.setText(MResource.getIdByName(context, "string","command_nocode_switch"));
		ToggleButtonA.setChecked(Util.CheckStr(A));
		views[0] = TextViewA;
		views[1] = ToggleButtonA;

		TextViewH.setText(MResource.getIdByName(context, "string","command_nocode_output"));
		EditTextH.setText(H);
		views[2] = TextViewH;
		views[3] = EditTextH;

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

		builder.setMessage(MResource.getIdByName(context, "string","command_nocode_name"))
				.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText editText = (EditText) views[viewSize-1];// 
						String string1 = "";
						for (int i = 0; i < (viewSize/2-1); i++) {
							ToggleButton myoggleButton = (ToggleButton) views[i * 2 + 1];
							string1 = string1
									+ "/"
									+ Util.Checkboolean(myoggleButton
											.isChecked());
						}
						String string = editText.getText().toString().trim();
						if (string.length() > 0 && string.length() <= 3) {
							OutCmdStr = "{G1016" + string1 + "/" + string
									+ "}{GB206}";// {G1000/YYYYY}{GB201}
//							ATCommandOutput cmd =new ATCommandOutput(OutCmdStr,"","");
							context.Sends(OutCmdStr.getBytes());
							System.out.println("OutCmdStr" + OutCmdStr + "");
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
