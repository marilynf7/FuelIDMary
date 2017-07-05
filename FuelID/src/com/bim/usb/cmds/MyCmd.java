package com.bim.usb.cmds;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;


public abstract class MyCmd {
	int MyCmdid;
	String MyCmdName;
	int viewSize;// view 的个数  包含提示的view
	View[] views; // view 的数组 包含提示的view
	String[] titles; // 每个view上面的填入信息，当这个view不需要填入信息的时候请填写null
	String OutCmdStr; // 每个view上面的填入信息，当这个view不需要填入信息的时候请填写null

	public abstract Dialog  CreateAlertDialog();//  处理消息体，定义几个view,view的种类
	
}
