/**
 * 
 */
package com.google.android.ore.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class F {
	private static final String TAG = F.class.getSimpleName();

	/**
	 * @param file
	 *            path
	 * @return File or null
	 */
	public static File makeFile(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		File file = new File(filePath);
		if (file.exists()) {
			return file;
		}
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			Log.w(TAG, filePath + " " + e.toString());
		}
		if (file.exists()) {
			return file;
		}
		return null;
	}

	/**
	 * 
	 * @param fromPath
	 * @param toPath
	 * @return boolean
	 */
	public static void copy(String fromPath, String toPath) {
		InputStream is = null;
		FileOutputStream fs = null;
		try {
			int len = 0;
			File fromFilefile = new File(fromPath);
			if (fromFilefile.exists()) {
				is = new FileInputStream(fromPath);
				fs = new FileOutputStream(toPath);
				byte[] buffer = new byte[1444];
				while ((len = is.read(buffer)) != -1) {
					fs.write(buffer, 0, len);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(is);
			closeStream(fs);
		}
	}

	public static void copy(File fromFile, File toFile) {
		InputStream is = null;
		FileOutputStream fs = null;
		try {
			int len = 0;
			if (fromFile.exists()) {
				is = new FileInputStream(fromFile);
				fs = new FileOutputStream(toFile);
				byte[] buffer = new byte[1444];
				while ((len = is.read(buffer)) != -1) {
					fs.write(buffer, 0, len);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(is);
			closeStream(fs);
		}
	}

	public static boolean copyFile(InputStream fileInputStream, File toFile) {
		boolean result = true;
		if (fileInputStream != null) {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(toFile, false);
				byte[] buffer = new byte[8 * 1024];
				int byteSize = 0;

				while ((byteSize = fileInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, byteSize);
				}
			} catch (FileNotFoundException e) {
				result = false;
				e.printStackTrace();
			} catch (IOException e) {
				result = false;
				e.printStackTrace();
			} finally {
				try {
					if (fileOutputStream != null) {
						fileOutputStream.flush();
						fileOutputStream.close();
					}
					fileInputStream.close();
				} catch (IOException e) {
					result = false;
					e.printStackTrace();
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	public static void closeStream(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeStream(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void append(String filePath, String content) {
		if (TextUtils.isEmpty(content)) {
			return;
		}
		append(makeFile(filePath), content);
	}

	public static void append(File file, String content) {
		if (!isSdCardAvailable()) {
			return;
		}
		if (file == null) {
			return;
		}
		BufferedWriter bW = null;
		try {
			bW = new BufferedWriter(new FileWriter(file, true), 8 * 1024);
			bW.write(content);
			bW.flush();
			bW.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean rename(File from, File to) {
		if (from == null || to == null) {
			Log.w("F.rename", "File from or to is null !");
			return false;
		}
		return from.renameTo(to);
	}

	public static boolean rename(String fromPath, String toPath) {
		File from = makeFile(fromPath);
		File to = makeFile(toPath);
		return rename(from, to);
	}

	public static boolean isSdCardAvailable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (!mExternalStorageAvailable) {
			return false;
		}
		if (!mExternalStorageWriteable) {
			return false;
		}
		return true;
	}
	
	public enum FILE_FORMAT{
		EXE,MIDI,RAR,ZIP,JPG,GIF,BMP,PNG,UNKNOWN
	}

	public static FILE_FORMAT getFileFormat(String filePath) {
		FILE_FORMAT fileFormat = FILE_FORMAT.UNKNOWN;
		try {
			// 从SDCARD下读取一个文件
			FileInputStream inputStream = new FileInputStream(filePath);
			byte[] buffer = new byte[2];
			// 文件类型代码
			String filecode = "";
			// 文件类型
			// 通过读取出来的前两个字节来判断文件类型
			if (inputStream.read(buffer) != -1) {
				for (int i = 0; i < buffer.length; i++) {
					// 获取每个字节与0xFF进行与运算来获取高位，这个读取出来的数据不是出现负数
					// 并转换成字符串
					filecode += Integer.toString((buffer[i] & 0xFF));
				}
				// 把字符串再转换成Integer进行类型判断
				switch (Integer.parseInt(filecode)) {
				case 7790:
					fileFormat = FILE_FORMAT.EXE;
					break;
				case 7784:
					fileFormat = FILE_FORMAT.MIDI;
					break;
				case 8297:
					fileFormat = FILE_FORMAT.RAR;
					break;
				case 8075:
					fileFormat = FILE_FORMAT.ZIP;
					break;
				case 255216:
					fileFormat = FILE_FORMAT.JPG;
					break;
				case 7173:
					fileFormat = FILE_FORMAT.GIF;
					break;
				case 6677:
					fileFormat = FILE_FORMAT.BMP;
					break;
				case 13780:
					fileFormat = FILE_FORMAT.PNG;
					break;
				default:
					fileFormat = FILE_FORMAT.UNKNOWN;
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileFormat;
	}
}
