package com.bim.usb.cmds;


import com.bimsdk.usb.MResource;
import com.bimsdk.usb.io.USBService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MyCmdNoCode2 extends MyCmd{
//	点击《无解码输出》：1、输入法发送：{GB100}{G1016？}
//    2、扫描器返回：{G1016/X/YYY}  X表示状态开关，YYY表示无
//       解码时需要输出的字符
//    3、输入法正确收到数据后弹出无解码输出对话框并返回：{GB200}
//      点击提交后返回：{G1016/Z/AAA} {GB206}   z表示状态开关 
//      AAA表示修改后的无解码时需要输出的字符
//      输入法超时时间内没有收到数据，返回:{GB200}

	String[] asciistr={"NUL","SOH","STX","ETX","EOT","ENQ","ACK","BEL","BS","HT","LF","VT","FF","CR","SO","SI","DEL",
			"DC1","DC2","DC3","DC4","NAK","SYN","ETB","CAN","EM","SUB","ESC","FS","GS","RS","US","SP",
			"","","","","","","","","","",
			"","","","","","","","","","",
			"","","","","","","","","","",
			"","","","","","","","","","",
			"","","","","","","","","","",
			"","","","","","","","","","",
			"","","","","","","","","","",
			"","","","","","","","","","",
			"","","","","","","","","","",
			"","","","",""};

		String input;
		USBService context;
		int mylength =0;
		
		int selectIndex =0;
		public MyCmdNoCode2(String input, USBService context) {
			this.input = input;
			this.context = context;
			for (int i = 33; i < 128; i++) {
				asciistr[i] = new String(""+(char)(i));
			}
			 gongnengs = new String[]{ context.getString(MResource.getIdByName(context, "string","command_prefix_delete")), context.getString(MResource.getIdByName(context, "string","command_prefix_set"))};
				
			gongnengs2=	 new String[]{ context.getString(MResource.getIdByName(context, "string","command_prefix_delete")), context.getString(MResource.getIdByName(context, "string","command_prefix_set"))};
				
		};
	 
		@Override
		public AlertDialog CreateAlertDialog() {// {G1004/1/XXX}
			// TODO Auto-generated method stub
			MyCmdid = 0;
//			MyCmdName = "后缀字符";//
			// 输入框一个 提示信息一个
			

			TextView TextViewA = new TextView(context);
			ToggleButton ToggleButtonA = new ToggleButton(context);

			TextView TextViewH = new TextView(context);
//			EditText EditTextH = new EditText(context);
			
			String A = input.substring(7, 8);
			String H  = input.substring(9);
			H = H.substring(0, H.length()-1);
			

			char[] chas =H.toCharArray();
			int length =chas.length;
			viewSize = 3+length;
			views = new View[viewSize];
			TextViewA.setText(MResource.getIdByName(context, "string","command_nocode_switch"));
			ToggleButtonA.setChecked(Util.CheckStr(A));
			views[0] = TextViewA;
			views[1] = ToggleButtonA;

			TextViewH.setText(MResource.getIdByName(context, "string","command_nocode_output"));
			views[2] = TextViewH;
			mylength =length;
			for (int i = 0; i < length; i++) {
				Button ButtonH = new Button(context);
				ButtonH.setText(asciistr[(int)chas[i]]);
				ButtonH.setTag(i);
				initButton(ButtonH);
				views[3+i]= ButtonH;
			}
			

			
		

			return getAlertDialog(context);
		}
		ScrollView sScrollView ; // view 太多无法全部显示用此
		LinearLayout lin ;
		LinearLayout linall ;
		HorizontalScrollView sScrollView1;
		LinearLayout linal1 ;
		
		
		public AlertDialog getAlertDialog(final USBService context) {
			 sScrollView = new ScrollView(context); // view 太多无法全部显示用此
			 lin = new LinearLayout(context);
			 linall = new LinearLayout(context);
			 sScrollView1 = new HorizontalScrollView(context);
			 linal1 = new LinearLayout(context);
			AlertDialog ad;
			AlertDialog.Builder builder = new AlertDialog.Builder(context);

			lin.setOrientation(1);
			for (int i = 0; i < 3; i++) {
				lin.addView(views[i]);
			}
			Button ButtonH = new Button(context);
			ButtonH.setText("+");		ButtonH.setTextSize(20);
			ButtonH.setTextColor(Color.GREEN);
			ButtonH.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mylength=linal1.getChildCount()+1;
					Button ButtonH = new Button(context);
					ButtonH.setTag(mylength-1);
					initButton(ButtonH);
					ButtonH.setWidth(80);
					linal1.addView(ButtonH);
				}
			});
			lin.addView(ButtonH);
		
			for (int i = 3; i < viewSize; i++) {
				linal1.addView(views[i]);
			}
			sScrollView1.addView(linal1);
			linall.addView(sScrollView1);
			lin.addView(linall);
			sScrollView.addView(lin);
			builder.setView(sScrollView);

			builder.setMessage(MResource.getIdByName(context, "string","command_nocode_name"))
					.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ToggleButton myoggleButton = (ToggleButton) views[1];
						String	string1 = Util.Checkboolean(myoggleButton
											.isChecked());
							mylength =linal1.getChildCount();
							String string ="";
							for (int i = 0; i < mylength; i++) {
								Button button =	(Button)linal1.getChildAt(i);
								int c =Check(button.getText().toString());
								if (c !=-1) {
									string =string+(new String((char)(c)+""));
								}
							
							}
							OutCmdStr = "{G1016"  + "/"+ string1 +"/"+ string
									+ "}{GB206}";// {G1000/YYYYY}{GB201}
							
//							ATCommandOutput cmd =new ATCommandOutput(OutCmdStr,"","");
							context.Sends(OutCmdStr.getBytes());
							System.out.println("OutCmdStr" + OutCmdStr + "");
							dialog.dismiss();
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
		
		
		//-----------------------------------------------------------
		private ButtonOnClick buttonOnClick = new ButtonOnClick(1);
		private String[] gongnengs =null;
		
		public void showSingleChoiceDialog1()
		{
			Builder sb =new Builder(context); 
			sb.setTitle(MResource.getIdByName(context, "string","command_button_operation"));
			sb.setSingleChoiceItems(gongnengs, -1, buttonOnClick);

			Dialog dialog  =sb.create();
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
			
		}
		private class ButtonOnClick implements DialogInterface.OnClickListener
		{
			private int index;
			public ButtonOnClick(int index)
			{
				this.index = index;
				System.out.println("index"+index);
			}

			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				//whichButton 表示单击的按钮索引，所有列表项的索引都大于或等于0,而按钮的索引都小于0
				if (whichButton >= 0)
				{
					index = whichButton;
					 	//单击列表项后立即关闭对话框
					
					 if (whichButton==0) {
						 System.out.println("removeViewAt"+selectIndex+"mylength"+mylength);
//						 linal1.removeViewAt(selectIndex);
						
						 mylength =linal1.getChildCount();
						 for (int i = 0; i < mylength; i++) {
							Button button =(Button) linal1.getChildAt(i);
							
							if (selectIndex==(Integer)button.getTag() ) {
								 linal1.removeViewAt(i);
								 break;
							}
						}
						 mylength =linal1.getChildCount();
					 }
					 
					if (whichButton==1) {
						getbianjiAlertDialog(context).show();
					}

					 dialog.cancel();
				}

			}
		}

		//--------------------------------------------------------------
		
		
		
		//-----------------------------------------------------------
		private ButtonOnClick1 buttonOnClick1 = new ButtonOnClick1(1);
		private String[] gongnengs2 = null;
				public void showSingleChoiceDialog2(int i)
		{
			Builder sb =new Builder(context); 
			sb.setTitle(MResource.getIdByName(context, "string","command_button_string"));
			sb.setSingleChoiceItems(asciistr, i, buttonOnClick1);

			Dialog dialog  =sb.create();
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
			
		}
		private class ButtonOnClick1 implements DialogInterface.OnClickListener
		{
			private int index;
			public ButtonOnClick1(int index)
			{
				this.index = index;
				System.out.println("index"+index);
			}

			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				//whichButton 表示单击的按钮索引，所有列表项的索引都大于或等于0,而按钮的索引都小于0
				if (whichButton >= 0)
				{
				;
				System.out.println("whichButton::"+whichButton);
				 mylength =linal1.getChildCount();
				 for (int i = 0; i < mylength; i++) {
						Button button =(Button) linal1.getChildAt(i);
						
						if (selectIndex==(Integer)button.getTag() ) {
							button.setText(	asciistr[whichButton]);
							 break;
						}
					}
		
			
//				System.out.println("whichButton::"+button.getText());
				 dialog.cancel();
				}

			}
		}

		//--------------------------------------------------------------
		
		
		private void initButton(Button button) {
		
			button.setWidth(80);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("selectIndex"+selectIndex+"mylength"+mylength);
					Button button = (Button)v;
					selectIndex=(Integer)(v.getTag());
					showSingleChoiceDialog2(Check(button.getText().toString()));

				}
			});
			button.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					selectIndex=(Integer)(v.getTag());
					System.out.println("selectIndex"+selectIndex+"mylength"+mylength);
					showSingleChoiceDialog1();
					return false;
				}
			});

		}

		
		
		LinearLayout bianjilin ;

		private Dialog getbianjiAlertDialog(final USBService context) {

			Dialog ad;
			Builder builder = new Builder(context);
			ScrollView sScrollView = new ScrollView(context); // view 太多无法全部显示用此
			
			 bianjilin = new LinearLayout(context);
			 bianjilin.setOrientation(1);
			TextView textView =  new TextView(context);
			textView.setText(MResource.getIdByName(context, "string","command_prefix_input_ascii"));
			final EditText editText =  new EditText(context);
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
					if (c > 127) {
						editText.setText(String.valueOf(127));
					}
				}
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});
			bianjilin.addView(textView);
			bianjilin.addView(editText);
			sScrollView.addView(bianjilin);

			builder.setView(sScrollView);
//			builder.setTitle("输出方式");
		

			builder.setMessage(MResource.getIdByName(context, "string","command_suffix_name"))
					.setPositiveButton(MResource.getIdByName(context, "string","command_button_submit"), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EditText editText=(EditText)bianjilin.getChildAt(1);
						String aString =	editText.getText().toString();
						int  myc =0;
						try {
							  myc = Integer.valueOf(aString);
						} catch (Exception e) {
							// TODO: handle exception
						}
						if (myc>127) {
							myc =127;
						}
						if (myc>-1) {
							 mylength =linal1.getChildCount();
							 for (int i = 0; i < mylength; i++) {
								Button button =(Button) linal1.getChildAt(i);
								
								if (selectIndex==(Integer)button.getTag() ) {
									button.setText(	asciistr[myc]);	
									 break;
								}
							}
//							Button button =(Button)	 linal1.getChildAt(selectIndex);
//							button.setText(	asciistr[myc]);	
						}
				
							dialog.dismiss();
						}
					})
					.setNegativeButton(MResource.getIdByName(context, "string","command_button_cancel"), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			ad = builder.create();
//			ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			ad.setCanceledOnTouchOutside(false); // 点击外面区域不会让dialog消失
			return ad;

		}
		
		//  
		
		private int Check(String abc) {
			for (int i = 0; i < 128; i++) {
				
				if (abc.equals(asciistr[i])) {
					return i;
				}
			}
			return -1;
		}
	}
