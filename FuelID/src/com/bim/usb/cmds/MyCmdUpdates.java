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
import android.widget.TextView;

public class MyCmdUpdates extends MyCmd {
//	点击《同一条码解码纠错》：1、输入法发送：{GB100}{G1018？}
//    2、扫描器返回：{G1018/X}   X表示纠错次数，范围0~10
//    3、输入法正确收到数据后弹出无解码输出对话框并返回：
//       {GB200}，点击提交后返回：{G1018/Y} {GB207}Y表示修
//       改后的纠错次数
//    输入法超时时间内没有收到数据，返回:{GB200}

	
	String input;
	USBService context;

	public MyCmdUpdates(String input, USBService context) {
		this.input = input;
		this.context = context;
	};

	@Override
	public AlertDialog CreateAlertDialog() {// input :{G1000/XXXXX}
											// out:{G1000/YYYYY}{GB201}
		// TODO Auto-generated method stub
		MyCmdid = 16;
//		MyCmdName = "同一条码解码纠错";// 点击《SPP蓝牙名称》：
		viewSize = 2;// 输入框一个 提示信息一个
		views= new View[viewSize];
		
		TextView my1View = new TextView(context);
		final EditText editText = new EditText(context);
		
		my1View.setText(MResource.getIdByName(context, "string","command_update_input"));
		views[0] = my1View;

		String mycmd1 = input.substring(7);// 去除前面 {G1000/
		mycmd1 = mycmd1.substring(0, mycmd1.length() - 1);// 去除最后一个 }
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.addTextChangedListener(new TextWatcher() {
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
				if (c > 10) {
					editText.setText(String.valueOf(10));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
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
		builder.setMessage(MResource.getIdByName(context, "string","command_update_name"))
				.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText editText = (EditText) views[1];// 把数组index=1
																// view
																// 转化为EditText
						String string = String.valueOf(Integer.valueOf(editText.getText().toString()
								.trim()));
						if (string.length() > 0) {
							OutCmdStr = "{G1018/" + string + "}{GB207}";// {G1000/YYYYY}{GB201}
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
