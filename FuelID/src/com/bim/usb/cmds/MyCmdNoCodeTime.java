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

public class MyCmdNoCodeTime extends MyCmd {
//	点击《无解码超时》：1、输入法发送：{GB100}{G1014？}
//    2、扫描器返回：{G1014/XXXX}      XXXX表示1~4095的数字
//    3、输入法正确收到数据后弹出无解码超时对话框并返回：{B200}
//       点击提交后返回：{G1014/YYYY}{GB205}  YYYY表示修改后的超
//       时时间
//       输入法超时时间内没有收到数据，返回:{GB200}

	
	String input;
	USBService context;

	public MyCmdNoCodeTime(String input, USBService context) {
		this.input = input;
		this.context = context;
	};

	@Override
	public AlertDialog CreateAlertDialog() {// input :{G1000/XXXXX}
											// out:{G1000/YYYYY}{GB201}
		// TODO Auto-generated method stub
		MyCmdid = 16;
//		MyCmdName = "无解码超时";// 点击《SPP蓝牙名称》：
		viewSize = 2;// 输入框一个 提示信息一个
		views= new View[viewSize];
		TextView my1View = new TextView(context);
		my1View.setText(MResource.getIdByName(context, "string","command_nocodetime_input"));
		views[0] = my1View;
		final EditText editText = new EditText(context);
		// {G1000/XXXXX}
		// string=string.substring(7);
		String mycmd1 = input.substring(7);// 去除前面 {G1000/
		mycmd1 = mycmd1.substring(0, mycmd1.length() - 1);// 去除最后一个 }
		editText.setText(mycmd1);
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
				if (c > 4095) {
					editText.setText(String.valueOf(4095));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
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

		builder.setMessage(MResource.getIdByName(context, "string","command_nocodetime_name"))
				.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText editText = (EditText) views[1];// 把数组index=1
																// view
																// 转化为EditText
						String string = String.valueOf(Integer.valueOf(editText.getText().toString()
								.trim()));
						if (string.length() > 0) {
							OutCmdStr = "{G1014/" + string + "}{GB205}";// {G1000/YYYYY}{GB201}
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
