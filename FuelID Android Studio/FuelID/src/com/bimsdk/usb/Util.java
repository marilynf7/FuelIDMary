package com.bimsdk.usb;






import com.bimsdk.usb.io.USBService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Util {

	/**
	 * @param args
	 */
	public static String DealString(int i) {

		String abcString = String.valueOf(i);
		int c = abcString.length();
		if (c == 1) {
			abcString = "0" + abcString;
		}
		return abcString;
	}

	
	public static AlertDialog getAlertDialog1(USBService myservice) {
		AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(myservice);
		builder.setMessage(MResource.getIdByName(myservice, "string","textView_con_2")).setPositiveButton(MResource.getIdByName(myservice, "string","dialog_button_ok"),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		ad = builder.create();
		// ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		// //系统中关机对话框就是这个属性
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false); // 点击外面区域不会让dialog消失
		return ad;

	}

//	public static AlertDialog getAlertDialog2(ATService myservice,
//			String[] gongnengs, final int index, final OpenWnn openWnn, final String str) {
//	
//		String myStr=str.substring(7);
//		myStr = myStr.replace("}", "");
//		AlertDialog ad;
//		AlertDialog.Builder builder = new AlertDialog.Builder(myservice);
//		LinearLayout lin = new LinearLayout(myservice);
//		lin.setOrientation(1);
//		TextView mytesTextView = new TextView(myservice);
//		mytesTextView.setText("请输入" + gongnengs[index] + "，使用英文或者数字");
//		final EditText editText = new EditText(myservice);
//		editText.setText(myStr);
//		lin.addView(mytesTextView);
//		lin.addView(editText);
//		builder.setView(lin);
//	
//		builder.setMessage(gongnengs[index])
//				.setPositiveButton("提交", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//					String string =	editText.getText().toString().trim();
//					if (string.length()>0) {
//						String newcmd = str.substring(0,6);
//						final ATCommandOutput cmd = new ATCommandOutput(newcmd+"/"+string+"}"+MySendCMD.cmds[index], "Start",
//								"开启扫描");
//						openWnn.Sends(cmd);
//						dialog.dismiss();
//					}
//					
//					}
//				})
//				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//		ad = builder.create();
//		// ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
//		// //系统中关机对话框就是这个属性
//		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		ad.setCanceledOnTouchOutside(false); // 点击外面区域不会让dialog消失
//		return ad;
//
//	}

	public static AlertDialog getProgressDialog(USBService myservice) {
		AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(myservice);
		LinearLayout lin = new LinearLayout(myservice);
		TextView mytesTextView = new TextView(myservice);
		mytesTextView.setHeight(80);
		mytesTextView.setGravity(Gravity.CENTER);

		mytesTextView.setText(MResource.getIdByName(myservice, "string","textView_con_1"));
		ProgressBar ProgressBar1 = new ProgressBar(myservice);
		lin.addView(ProgressBar1);
		lin.addView(mytesTextView);

		builder.setView(lin);

		ad = builder.create();
		// ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		// //系统中关机对话框就是这个属性
		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		ad.setCanceledOnTouchOutside(false); // 点击外面区域不会让dialog消失
		return ad;

	}
}
