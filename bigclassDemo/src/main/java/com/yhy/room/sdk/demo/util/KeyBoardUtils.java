package com.yhy.room.sdk.demo.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 键盘操作
 * 
 *
 */
public class KeyBoardUtils {

	/**
	 * 键盘隐藏或显示，如果隐藏则显示，如果显示则隐藏
	 * 
	 * @param context
	 */
	public static void keyBoardSHowOrHide(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 键盘显示
	 * 
	 * @param view
	 *            获得焦点的输入框控件
	 * @param context
	 */
	public static void keyBoardShow(View view, Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 键盘隐藏
	 * 
	 * @param view
	 *            获得焦点的输入框控件
	 * @param context
	 */
	public static void keyBoardHide(View view, Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
