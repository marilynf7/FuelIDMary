package com.bim.usb.cmds;


import com.bimsdk.usb.MResource;
import com.bimsdk.usb.io.USBService;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MyCmdXMTime extends MyCmd {
	// 点击《休眠时间》：1、输入法发送：{GB100}{G1026？}
	// 2、扫描器返回：{G1026/XXX} XXX表示休眠时间，范围0~240
	// 3、输入法正确收到数据后弹出休眠时间对话框并返回：{GB200}
	// 输入休眠时间后点击提交返回：{G1026/YYY}{GB211} YYY表示
	// 输入的休眠时间值

	String input;
	USBService context;

	public MyCmdXMTime(String input, USBService context) {
		this.input = input;
		this.context = context;
	};

	@Override
	public AlertDialog CreateAlertDialog() {// {G1026/XXX}
		// TODO Auto-generated method stub
		MyCmdid = 0;
		// MyCmdName = "休眠时间";//
		viewSize = 2;// 输入框一个 提示信息一个
		views = new View[viewSize];

		TextView TextViewE = new TextView(context);
		final EditText EditTextE = new EditText(context);

		String E = input.substring(7);
		E = E.substring(0, E.length() - 1);
		System.out.println(E);
		TextViewE.setText(MResource.getIdByName(context, "string","command_xmtime_time"));
		EditTextE.setText(E);
		EditTextE.setSelection(E.length());
		EditTextE.setInputType(InputType.TYPE_CLASS_NUMBER);
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
					System.out.println("s"+s);
					c = Integer.valueOf(s.toString());
				} catch (Exception e2) {
					c =0;
					// TODO: handle exception
//					EditTextE.setText("");
				}
				if (c > 240) {
					c=240;
					EditTextE.setText(String.valueOf(240));
					EditTextE.setSelection(String.valueOf(c).length());
				}
//				if (c!=0) {
//					EditTextE.setSelection(String.valueOf(c).length());	
//				}
	
			
			
			}  
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				EditTextE.setSelection(String.valueOf(c).length());
			}
		});
		EditTextE.setSelection(E.length());
		views[0] = TextViewE;
		views[1] = EditTextE;

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

		builder.setMessage(MResource.getIdByName(context, "string","command_xmtime_name"))
				.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								EditText editText = (EditText) views[viewSize - 1];//
								String string1 = "";
								for (int i = 0; i < (viewSize / 2 - 1); i++) {

									ToggleButton myoggleButton = (ToggleButton) views[i * 2 + 1];
									string1 = string1
											+ "/"
											+ Util.Checkboolean(myoggleButton
													.isChecked());
								}
								String string="";
								try {
									 string = String.valueOf(Integer.valueOf(editText.getText().toString()
											.trim()));
								} catch (Exception e) {
									// TODO: handle exception
									 string ="0";
								}
								
								
								if (string.length() > 0 ) {
									OutCmdStr = "{G1026" + string1 + "/"
											+ string + "}{GB211}";// {G1000/YYYYY}{GB201}
//									ATCommandOutput cmd = new ATCommandOutput(
//											OutCmdStr, "", "");
									context.Sends(OutCmdStr.getBytes());
									System.out.println("OutCmdStr" + OutCmdStr
											+ "");
									dialog.dismiss();
								}
							}
						})
				.setNegativeButton(MResource.getIdByName(context, "string","command_button_cancel"),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		ad = builder.create();
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false); // 点击外面区域不会让dialog消失
		return ad;

	}

}
