package com.hackerrank.files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHelper {
    private static final int BUFFER_SIZE = 4096;

    public static Map<String, byte[]> unzip(byte[] zip) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(zip));
        ZipEntry entry = zipIn.getNextEntry();

        Map<String, byte[]> map = new HashMap<>();
        while (entry != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
            bos.close();
            map.put(entry.getName(), bos.toByteArray());
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

        return map;
    }
}
