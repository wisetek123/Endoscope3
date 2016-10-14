package com.example.endoscope;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

//文件路径处理
public class Utility {

	//判断有没有sd卡
	public static boolean isSDcardOK() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	//获取SD卡根目录
	public static String getSDcardRoot() {
		if (isSDcardOK()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}

		return null;
	}

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int msgId) {
		Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
	}

	
	public static int countMatches(String res, String findString) {
		if (res == null) {
			return 0;
		}

		if (findString == null || findString.length() == 0) {
			throw new IllegalArgumentException(
					"The param findString cannot be null or 0 length.");
		}

		return (res.length() - res.replace(findString, "").length())
				/ findString.length();
	}

	//查找jpeg文件
	public static boolean isImage(String fileName) {
		return fileName.endsWith(".JPEG");
	}

	//查找mp4文件
	public static boolean isVideo(String fileName) {
		return fileName.endsWith(".mp4");
	}
}
