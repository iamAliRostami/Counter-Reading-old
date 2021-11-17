package com.leon.counter_reading.utils;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.ReadingData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class OfflineUtils {

    public static String[] getStorageDirectories() {
        String[] storageDirectories;
        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        List<String> results = new ArrayList<>();
        File[] externalDirs = MyApplication.getContext().getExternalFilesDirs(null);
        for (File file : externalDirs) {
            String path = null;
            try {
                path = file.getPath().split("/Android")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (path != null) {
                if (Environment.isExternalStorageRemovable(file) || rawSecondaryStoragesStr != null && rawSecondaryStoragesStr.contains(path)) {
                    results.add(path);
                }
            }
        }
        storageDirectories = results.toArray(new String[0]);
        return storageDirectories;
    }

    public static void writeOnSdCard(String path) {
        try {
            File file = new File(path.concat("/Download/mySdFile10.txt"));
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("{\n" +
                    "    \"trackingDtos\": [\n" +
                    "        {\n" +
                    "            \"id\": \"d93f0702-09a1-4693-9a39-f79b4c00a712\",\n" +
                    "            \"trackNumber\": 125776,\n" +
                    "            \"listNumber\": null,\n" +
                    "            \"insertDateJalali\": \"1400/08/26\",\n" +
                    "            \"insertTime\": \"09:39\",\n" +
                    "            \"zoneId\": 131301,\n" +
                    "            \"zoneTitle\": \"1  - منطقه يک\",\n" +
                    "            \"isBazdid\": false,\n" +
                    "            \"year\": 1400,\n" +
                    "            \"isRoosta\": false,\n" +
                    "            \"fromEshterak\": \"105010500 \",\n" +
                    "            \"toEshterak\": \"105468800 \",\n" +
                    "            \"fromDate\": \"1400/08/01\",\n" +
                    "            \"toDate\": \"1400/08/01\",\n" +
                    "            \"itemQuantity\": 2110,\n" +
                    "            \"alalHesabPercent\": 50,\n" +
                    "            \"imagePercent\": 50,\n" +
                    "            \"hasPreNumber\": true,\n" +
                    "            \"displayBillId\": true,\n" +
                    "            \"displayRadif\": false,\n" +
                    "            \"counterReaderId\": \"9dcbdd6a-c68c-4fa5-99f8-654bf62b6775\",\n" +
                    "            \"counterReaderName\": \"مامور قرائت\",\n" +
                    "            \"stateTitle\": null,\n" +
                    "            \"hasMap\": false,\n" +
                    "            \"description\": null,\n" +
                    "            \"x\": \"51.6678892\",\n" +
                    "            \"y\": \"32.6581116\"\n" +
                    "        }\n" +
                    "    ]" +
                    "}");
            myOutWriter.close();
            fOut.close();
            new CustomToast().success("Writing SD 'mySdFile.txt' Done");
        } catch (Exception e) {
            new CustomToast().error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static ReadingData readFromSdCard(String path) {
        File root = new File(path.concat("/Download"));
        File file = findFile(root, "125776.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        String json = text.toString();
        Log.e("json", json);

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, ReadingData.class);
    }

    public static File findFile(File dir, String name) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    File found = findFile(child, name);
                    if (found != null) return found;
                } else {
                    if (name.equals(child.getName())) return child;
                }
            }
        }
        return null;
    }
    /*
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myFolder", "downloads/myFolder.zip");
     */

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            /*if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {*/
            byte[] data = new byte[BUFFER];
            FileInputStream fi = new FileInputStream(sourcePath);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
            entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
//            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     * Zips a subfolder
     */
    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {
        final int BUFFER = 2048;
        File[] fileList = folder.listFiles();
        BufferedInputStream origin;
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    zipSubFolder(out, file, basePathLength);
                } else {
                    byte[] data = new byte[BUFFER];
                    String unmodifiedFilePath = file.getPath();
                    String relativePath = unmodifiedFilePath
                            .substring(basePathLength);
                    FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(relativePath);
                    entry.setTime(file.lastModified()); // to keep modification time after unzipping
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }
        }
    }

    /*
     * gets the last path component
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        return segments[segments.length - 1];
    }

}
