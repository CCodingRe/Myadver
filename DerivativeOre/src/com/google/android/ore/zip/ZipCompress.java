package com.google.android.ore.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.android.ore.thinkandroid.L;

public class ZipCompress {
	private static final String TAG = ZipCompress.class.getSimpleName();
    private static final int BUFFER = 2048;

    private File[] mFiles;
    private File mZipFile;

    public ZipCompress(File[] files, File zipFile) {
        mFiles = files;
        mZipFile = zipFile;
    }

    public void zip() {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dist = new FileOutputStream(mZipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dist));
            byte data[] = new byte[BUFFER];

            for (File mFile : mFiles) {
    			L.d(TAG, "Adding: " + mFile);
                if (mFile == null || !mFile.exists()) {
                    continue;
                }
                FileInputStream fi = new FileInputStream(mFile);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(mFile.getAbsolutePath());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
