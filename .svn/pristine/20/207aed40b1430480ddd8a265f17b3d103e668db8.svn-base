package com.ewu.core.utils;

import android.content.Context;
import android.os.Environment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	/**
	 * Helper Method to Test if external Storage is Available
	 */
	public static boolean isExternalStorageAvailable() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			state = true;
		}
		return state;
	}

	/**
	 * Helper Method to Test if external Storage is read only
	 */
	public static boolean isExternalStorageReadOnly() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
			state = true;
		}
		return state;
	}

	/**
	 * Write to external public directory
	 * 
	 * @param filename
	 *            - the filename to write to
	 * @param content
	 *            - the content to write
	 */
	public static void writeToExternalStoragePublic(Context context, String filename, String content) {
		if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
			try {
				File fileDir = context.getExternalFilesDir(null);
				File file = new File(fileDir, filename);
				if (!file.getParentFile().exists())
				{
					file.getParentFile().mkdirs();
				}
				
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(file);

				byte[] buffer = content.getBytes();
				fos.write(buffer);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteExternalStoragePrivateFile(Context context, String fileName) {
		File file = new File(context.getExternalFilesDir(null), fileName);
		if (null != file) {
			file.delete();
		}
	}

	/*
	 * Write to the external storage using the latest Level 8 APIs
	 */
	public static void writeExternallStoragePrivate(Context context, String fileName, String fileContent) {
		try {
			File file = new File(context.getExternalFilesDir(null), fileName);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = fileContent.getBytes();
			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/*
	 * Writes content to internal storage making the content private to the
	 * application.
	 */
	public static void writeInternalStoragePrivate(Context context, String fileName, String fileContent) {
		try {
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte[] buffer = fileContent.getBytes();
			fos.write(buffer);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void deleteInternalStoragePrivate(Context context, String filename) {
		File file = context.getFileStreamPath(filename);
		if (null != file) {
			file.delete();
		}
	}

	/**
	 * 获取文件大小
	 *
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	public static byte[] getBytes(File file){
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

}
