package com.google.android.ore.zip;

import java.io.File;

public class Zip {

    public static void unzip(File zipFile, File location) {
        new ZipDecompress(zipFile, location).unzip();
    }

    public static void zip(File[] files, File zipFile) {
        new ZipCompress(files, zipFile).zip();
    }

}
