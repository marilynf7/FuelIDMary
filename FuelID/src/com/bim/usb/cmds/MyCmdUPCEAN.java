package com.bim.usb.cmds;

//
//import com.bim.command.ATCommandOutput;
//import com.bim.io.USBService;
//import com.bimsdk.R;

import com.bimsdk.usb.MResource;
import com.bimsdk.usb.io.USBService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MyCmdUPCEAN extends MyCmd {
	//
	// 1、输入法发送：{GB100}{G1036？}
	// 2、扫描器返回：{G1036/A/B/C/D/E/F/G/HH} A表示UPC-A码状态
	// B表示UPC-E码状态 C表示EAN-8码状态 D表示EAN-13码状
	// 态 E表示UPC/EAN码附加码状态 F表示UPC/EAN码2字符
	// 附加码状态 G表示UPC/EAN吗5字符附加码开关 ＨＨ表示
	// UPC/EAN码最小条码长度，范围0~32
	// 3、输入法正确收到数据后弹出UPC/EAN码设置对话框并返回:{GB
	// 200}，设置好相关参数点击提交后返回：{G1036/A/B/C/D/E/F/G/HH}
	// {GB216} 相关参数同上

	String input;
	USBService context;

	public MyCmdUPCEAN(String input, USBService context) {
		this.input = input;
		this.context = context;
	};

	@Override
	public AlertDialog CreateAlertDialog() {// {G1036/A/B/C/D/E/F/G/HH}
		// TODO Auto-generated method stub
		MyCmdid = 0;
//		MyCmdName = "UPC/EAN码设置";//
		viewSize = 16;// 输入框一个 提示信息一个
		views = new View[viewSize];

		TextView TextViewA = new TextView(context);
		ToggleButton ToggleButtonA = new ToggleButton(context);

		TextView TextViewB = new TextView(context);
		ToggleButton ToggleButtonB = new ToggleButton(context);

		TextView TextViewC = new TextView(context);
		ToggleButton ToggleButtonC = new ToggleButton(context);

		TextView TextViewD = new TextView(context);
		ToggleButton ToggleButtonD = new ToggleButton(context);

		TextView TextViewE = new TextView(context);
		ToggleButton ToggleButtonE = new ToggleButton(context);

		TextView TextViewF = new TextView(context);
		ToggleButton ToggleButtonF = new ToggleButton(context);

		TextView TextViewG = new TextView(context);
		ToggleButton ToggleButtonG = new ToggleButton(context);

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
		String D = input.substring(13, 14);
		String E = input.substring(15, 16);
		String F = input.substring(17, 18);
		String G = input.substring(19, 20);
		String H = input.substring(21);
		H = H.substring(0, H.length()-1);
		
		TextViewA.setText(MResource.getIdByName(context, "string","command_upcean_state_upca"));
		ToggleButtonA.setChecked(Util.CheckStr(A));
		views[0] = TextViewA;
		views[1] = ToggleButtonA;


		TextViewB.setText(MResource.getIdByName(context, "string","command_upcean_state_upce"));
		ToggleButtonB.setChecked(Util.CheckStr(B));
		views[2] = TextViewB;
		views[3] = ToggleButtonB;


		TextViewC.setText(MResource.getIdByName(context, "string","command_upcean_state_ean8"));
		ToggleButtonC.setChecked(Util.CheckStr(C));
		views[4] = TextViewC;
		views[5] = ToggleButtonC;


		TextViewD.setText(MResource.getIdByName(context, "string","command_upcean_state_ean13"));
		ToggleButtonD.setChecked(Util.CheckStr(D));
		views[6] = TextViewD;
		views[7] = ToggleButtonD;


		TextViewE.setText(MResource.getIdByName(context, "string","command_upcean_state_upcean_add"));
		ToggleButtonE.setChecked(Util.CheckStr(E));
		views[8] = TextViewE;
		views[9] = ToggleButtonE;

		TextViewF.setText(MResource.getIdByName(context, "string","command_upcean_state_upcean2_add"));
		ToggleButtonF.setChecked(Util.CheckStr(F));
		views[10] = TextViewF;
		views[11] = ToggleButtonF;

		TextViewG.setText(MResource.getIdByName(context, "string","command_upcean_switch_upcean5_add"));
		ToggleButtonG.setChecked(Util.CheckStr(G));
		views[12] = TextViewG;
		views[13] = ToggleButtonG;

		TextViewH.setText(MResource.getIdByName(context, "string","command_upcean_minlength"));
		EditTextH.setText(H);
		views[14] = TextViewH;
		views[15] = EditTextH;

		return getAlertDialog(context);
	}

	public AlertDialog getAlertDialog(final USBService context) {

		AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		ScrollView sScrollView = new ScrollView(context); // view 太多无法全部显示用此
	
		LayoutParams params = new LayoutParams(-1, 100);
		sScrollView.setLayoutParams(params);
		LinearLayout lin = new LinearLayout(context);
	
		lin.setOrientation(1);
		for (int i = 0; i < viewSize; i++) {
			lin.addView(views[i]);
		}
		sScrollView.addView(lin);
		builder.setView(sScrollView);
//		builder.setTitle(MResource.getIdByName(context, "string","command_upcean_name);
		builder.setMessage(MResource.getIdByName(context, "string","command_upcean_name"))
				.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText editText = (EditText) views[15];// 
						String string1 = "";
						for (int i = 0; i < 7; i++) {
							ToggleButton myoggleButton = (ToggleButton) views[i * 2 + 1];
							string1 = string1
									+ "/"
									+ Util.Checkboolean(myoggleButton
											.isChecked());
						}
						String string = String.valueOf(Integer.valueOf(editText.getText().toString()
								.trim()));
						if (string.length() > 0 ) {
							OutCmdStr = "{G1036" + string1 + "/" + string
									+ "}{GB216}";// {G1000/YYYYY}{GB201}
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
