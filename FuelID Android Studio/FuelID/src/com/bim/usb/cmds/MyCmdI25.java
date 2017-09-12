package com.bim.usb.cmds;


import com.bimsdk.usb.MResource;
import com.bimsdk.usb.io.USBService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MyCmdI25 extends MyCmd{
//	点击《i25码设置》：1、输入法发送：{GB100}{G1034？}
//    2、扫描器返回：{G1034/X/Y/Z/AA}    X表示i25码状态   Y表示i25
//       码效验码状态    Z表示i25码效验码显示使能   AA表示i25码条
//       码最小字符长度，范围0~32
//    3、输入法正确收到数据后弹出I25码设置对话框并返回：{GB200},设置
//       好相关参数点击提交后返回：{G1034/X/Y/Z/AA}{GB215}参数同上
//       输入法超时时间内没有收到数据，返回:{GB200}


	String input;
	USBService context;

	public MyCmdI25(String input, USBService context) {
		this.input = input;
		this.context = context;
	};

	@Override
	public AlertDialog CreateAlertDialog() {// {G1034/X/Y/Z/AA} 
		// TODO Auto-generated method stub
		MyCmdid = 0;
//		MyCmdName = "i25码设置";//
		viewSize = 8;// 输入框一个 提示信息一个
		views = new View[viewSize];

		TextView TextViewA = new TextView(context);
		ToggleButton ToggleButtonA = new ToggleButton(context);

		TextView TextViewB = new TextView(context);
		ToggleButton ToggleButtonB = new ToggleButton(context);

		TextView TextViewC = new TextView(context);
		ToggleButton ToggleButtonC = new ToggleButton(context);

		TextView TextViewH = new TextView(context);
		final EditText EditTextH = new EditText(context);
		EditTextH.setInputType(InputType.TYPE_CLASS_NUMBER);
		EditTextH.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				int c=0;
				try {
					c = Integer.valueOf(s.toString());
				} catch (Exception e2) {
					// TODO: handle exception
				}
				if (c > 32) {
					EditTextH.setText(String.valueOf(32));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		String A = input.substring(7, 8);
		String B = input.substring(9, 10);
		String C = input.substring(11, 12);
		String H  = input.substring(13);
		H = H.substring(0, H.length()-1);
		
		TextViewA.setText(MResource.getIdByName(context, "string","command_i25_state"));
		ToggleButtonA.setChecked(Util.CheckStr(A));
		views[0] = TextViewA;
		views[1] = ToggleButtonA;


		TextViewB.setText(MResource.getIdByName(context, "string","command_i25_checkcode_state"));
		ToggleButtonB.setChecked(Util.CheckStr(B));
		views[2] = TextViewB;
		views[3] = ToggleButtonB;


		TextViewC.setText(MResource.getIdByName(context, "string","command_i25_checkcode_display"));
		ToggleButtonC.setChecked(Util.CheckStr(C));
		views[4] = TextViewC;
		views[5] = ToggleButtonC;


	
		TextViewH.setText(MResource.getIdByName(context, "string","command_i25_minlength"));
		EditTextH.setText(H);
		views[6] = TextViewH;
		views[7] = EditTextH;

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

		builder.setMessage(MResource.getIdByName(context, "string","command_i25_name"))
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
						String string = String.valueOf(Integer.valueOf(editText.getText().toString()
								.trim()));
						if (string.length() > 0 ) {
							OutCmdStr = "{G1034" + string1 + "/" + string
									+ "}{GB215}";// {G1000/YYYYY}{GB201}
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
