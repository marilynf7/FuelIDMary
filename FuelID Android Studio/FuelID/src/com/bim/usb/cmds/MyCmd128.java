package com.bim.usb.cmds;




import com.bimsdk.usb.MResource;
import com.bimsdk.usb.io.USBService;

import android.R.raw;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbDevice;
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

public class MyCmd128 extends MyCmd{
//	点击《128码设置》：1、输入法发送：{GB100}{G1030？}
//    2、扫描器返回：{G1030/X/YY}   X表示128码状态   YY表示128码
//       条码的最小字符长度，范围0~32
//   3、输入法正确收到数据后弹出128码设置对话框并返回:{GB200}，设
//     置好相关参数点击提交后返回：{G1030/X/YY}{GB213}里面的参数同上
//     输入法超时时间内没有收到数据，返回:{GB200}

   


	String input;
	USBService context;

	public MyCmd128(String input, USBService context) {
		this.input = input;
		this.context = context;
	
	};

	@Override
	public AlertDialog CreateAlertDialog() {// {G1030/X/YY} 
		// TODO Auto-generated method stub
		MyCmdid = 0;
//		MyCmdName = "128码设置";//
		viewSize = 4;// 输入框一个 提示信息一个
		views = new View[viewSize];


		TextView TextViewA = new TextView(context);
		ToggleButton ToggleButtonA = new ToggleButton(context);


		TextView TextViewE = new TextView(context);
		final EditText EditTextE = new EditText(context);
		EditTextE.setInputType(InputType.TYPE_CLASS_NUMBER);
		String A = input.substring(7, 8);
		String E = input.substring(9);
		EditTextE.addTextChangedListener(new TextWatcher() {
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
					EditTextE.setText(String.valueOf(32));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		E = E.substring(0, E.length()-1);
		
		TextViewA.setText(MResource.getIdByName(context, "string", "command_128_state"));
		ToggleButtonA.setChecked(Util.CheckStr(A));
		views[0] = TextViewA;
		views[1] = ToggleButtonA;

		TextViewE.setText(MResource.getIdByName(context, "string", "command_128_minlength"));
		EditTextE.setText(E);
		views[2] = TextViewE;
		views[3] = EditTextE;




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

		builder.setMessage(MResource.getIdByName(context, "string", "command_128_name"))
				.setPositiveButton(MResource.getIdByName(context, "string", "command_button_submit"), new DialogInterface.OnClickListener() {
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
						if (string.length() > 0) {
							OutCmdStr = "{G1030" + string1 + "/" + string
									+ "}{GB213}";// {G1000/YYYYY}{GB201}
//							TestSetActivity.getinput =2;
//							ATCommandOutput cmd =new ATCommandOutput(OutCmdStr,"","");
							context.Sends(OutCmdStr.getBytes());
							System.out.println("OutCmdStr" + OutCmdStr + "");
							dialog.dismiss();
						}
					}
				})
				.setNegativeButton(MResource.getIdByName(context, "string", "command_button_cancel"), new DialogInterface.OnClickListener() {
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
