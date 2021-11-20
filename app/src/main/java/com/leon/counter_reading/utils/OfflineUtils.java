package com.leon.counter_reading.utils;

import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class OfflineUtils {

    public static String[] getStorageDirectories() {
        String[] storageDirectories;
        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
//        String rawSecondaryStoragesStr = System.getenv("EMULATED_STORAGE_TARGET");
//        String rawSecondaryStoragesStr = System.getenv("EXTERNAL_STORAGE");
//        String rawSecondaryStoragesStr = System.getenv("EXTERNAL_SDCARD_STORAGE");
        List<String> results = new ArrayList<>();
        File[] externalDirs = getContext().getExternalFilesDirs(null);
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

    @SuppressLint("SimpleDateFormat")
    public static void writeOnSdCard(String json, String name, int trackNumber) {
        String filePostName = name.concat(".txt");
        try {
            File root = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + trackNumber);
            root.mkdirs();
            File file = new File(root + "/" + filePostName);
            Log.e("address", file.getAbsolutePath());
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(json);
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            new CustomToast().error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static ReadingData readFromSdCard(String path) {
        File root = new File(path.concat("/Download"));

        File file = findFile(root, "125776_.txt");
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
    public static boolean zipFileAtPath(int trackNumber) {
        final int BUFFER = 2048;
        final File root = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        final String sourcePath = root + "/" + trackNumber, toLocation = root + "/" + trackNumber + ".zip";
        final File sourceFile = new File(sourcePath);
        try {
            final BufferedInputStream origin;
            final FileOutputStream dest = new FileOutputStream(toLocation);
            final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            if (sourceFile.isDirectory() && sourceFile.getParent() != null) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                final byte[] data = new byte[BUFFER];
                final FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                final ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified());// to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
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
    private static void zipSubFolder(ZipOutputStream out, File folder, int basePathLength) {
        final int BUFFER = 2048;
        final File[] fileList = folder.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    zipSubFolder(out, file, basePathLength);
                } else {
                    final byte[] data = new byte[BUFFER];
                    final String unmodifiedFilePath = file.getPath();
                    final String relativePath = unmodifiedFilePath
                            .substring(basePathLength);
                    try {
                        final FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                        final BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
                        final ZipEntry entry = new ZipEntry(relativePath);
                        entry.setTime(file.lastModified()); // to keep modification time after unzipping
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, BUFFER)) != -1) {
                            out.write(data, 0, count);
                        }
                        origin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*
     * gets the last path component
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public static String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        return segments[segments.length - 1];
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File child : files)
                    deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    public static String isRemovableSDCardAvailable() {
        final String FLAG = "mnt";
        final String SECONDARY_STORAGE = System.getenv("SECONDARY_STORAGE");
        final String EXTERNAL_STORAGE_DOCOMO = System.getenv("EXTERNAL_STORAGE_DOCOMO");
        final String EXTERNAL_SDCARD_STORAGE = System.getenv("EXTERNAL_SDCARD_STORAGE");
        final String EXTERNAL_SD_STORAGE = System.getenv("EXTERNAL_SD_STORAGE");
        final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");

        Map<Integer, String> listEnvironmentVariableStoreSDCardRootDirectory = new HashMap<Integer, String>();
        listEnvironmentVariableStoreSDCardRootDirectory.put(0, SECONDARY_STORAGE);
        listEnvironmentVariableStoreSDCardRootDirectory.put(1, EXTERNAL_STORAGE_DOCOMO);
        listEnvironmentVariableStoreSDCardRootDirectory.put(2, EXTERNAL_SDCARD_STORAGE);
        listEnvironmentVariableStoreSDCardRootDirectory.put(3, EXTERNAL_SD_STORAGE);
        listEnvironmentVariableStoreSDCardRootDirectory.put(4, EXTERNAL_STORAGE);

        File[] externalStorageList;
        externalStorageList = getContext().getExternalFilesDirs(null);
        String directory;
        int size = listEnvironmentVariableStoreSDCardRootDirectory.size();
        for (int i = 0; i < size; i++) {
            if (externalStorageList != null && externalStorageList.length > 1 && externalStorageList[1] != null)
                directory = externalStorageList[1].getAbsolutePath();
            else
                directory = listEnvironmentVariableStoreSDCardRootDirectory.get(i);

            directory = canCreateFile(directory);
            if (directory != null && directory.length() != 0) {
                if (i == size - 1) {
                    if (directory.contains(FLAG)) {
                        Log.e("getClass().getSimpleName()", "SD Card's directory: " + directory);
                        return directory;
                    } else {
                        return null;
                    }
                }
                Log.e("getClass().getSimpleName()", "SD Card's directory: " + directory);
                return directory;
            }
        }
        return null;
    }

    public static String canCreateFile(String directory) {
        final String FILE_DIR = directory + File.separator + "hoang.txt";
        File tempFile = null;
        try {
            tempFile = new File(FILE_DIR);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(new byte[1024]);
            fos.flush();
            fos.close();
            Log.e("getClass().getSimpleName()", "Can write file on this directory: " + FILE_DIR);
        } catch (Exception e) {
            Log.e("getClass().getSimpleName()", "Write file error: " + e.getMessage());
            return null;
        } finally {
            if (tempFile != null && tempFile.exists() && tempFile.isFile()) {
                // tempFlie.delete();
                tempFile = null;
            }
        }
        return directory;
    }
}
