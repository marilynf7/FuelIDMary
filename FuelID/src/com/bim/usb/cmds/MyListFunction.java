package com.bim.usb.cmds;






import com.bimsdk.usb.MResource;
import com.bimsdk.usb.UsbConnect;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;


public class MyListFunction {

	// public static final String BLUETOOTH_LIST_KEY =
	// "bluetooth_list_preference";
//	private String[] gongnengs = new String[] { "开始扫描", "关闭休眠时间", "打开读取数据",
//			"关闭读取数据", "还原默认设置" };
	private String[] gongnengs = null;
	Context context;
	private ButtonOnClick buttonOnClick = new ButtonOnClick(1);

	public MyListFunction(Context context) {
		this.context = context;
		gongnengs = new String[]{  context.getString(MResource.getIdByName(context, "string","send_21")), 
				context.getString(MResource.getIdByName(context, "string","send_22")),
				context.getString(MResource.getIdByName(context, "string","send_23")),
				context.getString(MResource.getIdByName(context, "string","send_24")),
				context.getString(MResource.getIdByName(context, "string","send_25"))};
	}


	public void showSingleChoiceDialog() {
		Builder sb = new Builder(context);
		sb.setTitle(MResource.getIdByName(context, "string","device_function"));
		sb.setSingleChoiceItems(gongnengs, -1, buttonOnClick);
		Dialog dialog = sb.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

	}

	private class ButtonOnClick implements DialogInterface.OnClickListener {
		private int index;

		public ButtonOnClick(int index) {
			this.index = index;
			System.out.println("index" + index);
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			// whichButton 表示单击的按钮索引，所有列表项的索引都大于或等于0,而按钮的索引都小于0
			if (whichButton >= 0) {
				index = whichButton;

				dialog.cancel(); // 单击列表项后立即关闭对话框
				String abcString = "{Start}";
				if (whichButton == 0) {
					abcString = "{Start}";
				} else if (whichButton == 1) {
					abcString = "{off/Sleep}";
				} else if (whichButton == 2) {
					abcString = "{on/Read}";
				} else if (whichButton == 3) {
					abcString = "{off/Read}";
				} else if (whichButton == 4) {
					abcString = "{Init}";
				}
				UsbConnect.UsbSend(abcString.getBytes());
			}

		}
	}


	
}
