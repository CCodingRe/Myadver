package com.google.android.ore.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.android.ore.thinkandroid.L;

public class ZipDecompress {
	private File mZipFile;
	private File mLocation;
	private static final String TAG = ZipCompress.class.getSimpleName();

	public ZipDecompress(File zipFile, File location) {
		L.d(TAG, "zipFile " + zipFile.getAbsolutePath());
		L.d(TAG, "location " + location.getAbsolutePath());
		mZipFile = zipFile;
		mLocation = location;
		checkDir("");
	}

	public void unzip() {
		try {
			FileInputStream fin = new FileInputStream(mZipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				L.d(TAG, "Unzipping " + ze.getName());
				if (ze.isDirectory()) {
					checkDir(ze.getName());
				} else {
					File file = new File(mLocation, ze.getName());
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					FileOutputStream fout = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					for (int c = 0; c != -1; c = zin.read(buffer)) {
						fout.write(buffer, 0, c);
					}
					zin.closeEntry();
					fout.close();
				}
			}
			zin.close();
		} catch (Exception e) {
			L.d(TAG, "unzip" + e.getMessage());
		}
	}

	private void checkDir(String dir) {
		File f = new File(mLocation, dir);
		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}
}