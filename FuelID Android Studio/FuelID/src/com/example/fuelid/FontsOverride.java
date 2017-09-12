package com.example.fuelid;

import java.lang.reflect.AccessibleObject;
import java.text.DateFormat.Field;

import android.content.Context;
import android.graphics.Typeface;
import android.content.Context;
import android.graphics.Typeface;


	public final class FontsOverride {

	    public static void setDefaultFont(Context context,
	            String staticTypefaceFieldName, String fontAssetName) {
	        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
	                fontAssetName);
	        replaceFont(staticTypefaceFieldName, regular);
	    }

	    protected static void replaceFont(String staticTypefaceFieldName,
	            final Typeface newTypeface) {
	        try {
	            final java.lang.reflect.Field staticField = Typeface.class
	                    .getDeclaredField(staticTypefaceFieldName);
	            ((AccessibleObject) staticField).setAccessible(true);
	            staticField.set(null, newTypeface);
	        } catch (NoSuchFieldException e) {
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	    }}
	

