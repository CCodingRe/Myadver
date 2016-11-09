package com.google.android.ore.util;

import java.io.File;

import com.google.android.ore.Constant;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.zip.Zip;

public class MaterialUtil {
	private String TAG = MaterialUtil.class.getSimpleName();

	public boolean checkLocalResZipComplete(String res_md5) {
		File resDir = Constant.getDir(Constant.RES_ZIP_DIR);
		if (null == resDir) {
			// 不能出现
			return false;
		}
		final String resFileName = resDir.getAbsolutePath() + "/" + res_md5;
		if (checkResExistAndComplete(resFileName, res_md5)) {
			L.d(TAG,
					"checkLocalResZipComplete checkResExistAndComplete true resFileName:"
							+ resFileName);
			// 解压文件
			Zip.unzip(new File(resFileName), Constant.getDir(Constant.RES_DIR));
			L.d(TAG, "checkLocalResZipComplete Zip.unzip");
			return true;
		} else {
			// 本地存在被改动过的res zip
			// FileUtils.deleteFile(resFileName);
			L.d(TAG,
					"checkLocalResZipComplete FileUtils.deleteFile resFileName:"
							+ resFileName);
		}
		return false;
	}

	public static boolean checkResExistAndComplete(String resFileName,
			String fileMd5) {
		File resFile = new File(resFileName);
		if (null != resFile && resFile.exists()
				&& fileMd5.equalsIgnoreCase(Encrypt.getFileMd5(resFileName))) {
			return true;
		}
		return false;
	}
}
