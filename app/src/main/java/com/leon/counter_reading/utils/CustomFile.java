package com.leon.counter_reading.utils;

import static com.leon.counter_reading.helpers.Constants.CURRENT_IMAGE_SIZE;
import static com.leon.counter_reading.helpers.Constants.MAX_IMAGE_SIZE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.Voice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class CustomFile {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static Bitmap loadImage(Context context, String address) {
        try {
            File f = new File(context.getExternalFilesDir(null), context.getString(R.string.camera_folder));
            f = new File(f, address);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static MultipartBody.Part bitmapToFile(Bitmap bitmap, Context context) {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String fileNameToSave = "JPEG_" + new Random().nextInt() + "_" + timeStamp + ".jpg";
        File f = new File(context.getCacheDir(), fileNameToSave);
        try {
            if (!f.createNewFile()) return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            final byte[] bitmapData = compressBitmapToByte(bitmap);
            final FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(f, MediaType.parse("image/jpeg"));
        return MultipartBody.Part.createFormData("File", f.getName(), requestBody);
    }

    public static byte[] compressBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap compressBitmap(Bitmap original) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            if (stream.toByteArray().length > MAX_IMAGE_SIZE) {
                final int width, height;
                if (original.getHeight() > original.getWidth()) {
                    height = 1000;
                    width = original.getWidth() / (original.getHeight() / height);
                } else {
                    width = 1000;
                    height = original.getHeight() / (original.getWidth() / width);
                }
                original = Bitmap.createScaledBitmap(original, width, height, false);
                stream = new ByteArrayOutputStream();
                original.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            }
            CURRENT_IMAGE_SIZE = stream.toByteArray().length;
            return original;
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        return null;
    }

    public static ByteArrayInputStream compressBitmapIS(Bitmap original) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        if (stream.toByteArray().length > MAX_IMAGE_SIZE) {
            int qualityPercent = Math.max((int) ((double)
                    stream.toByteArray().length / MAX_IMAGE_SIZE), 20);
            original = Bitmap.createScaledBitmap(original
                    , (int) ((double) original.getWidth() * qualityPercent / 100)
                    , (int) ((double) original.getHeight() * qualityPercent / 100), false);
            stream = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        }
        return new ByteArrayInputStream(stream.toByteArray());
    }

    public static String bitmapToBinary(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Arrays.toString(byteArray);
    }

    public static Bitmap binaryToBitmap(String s) {
        byte[] bytes = s.getBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String saveTempBitmap(Bitmap bitmap, Context context) {
        if (isExternalStorageWritable()) {
            return saveImage(bitmap, context);
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
            return context.getString(R.string.error_external_storage_is_not_writable);
        }
    }

    @SuppressLint("SimpleDateFormat")
    static String saveImage(Bitmap bitmapImage, Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(null) + context.getString(R.string.camera_folder));
        if (!mediaStorageDir.exists()) if (!mediaStorageDir.mkdirs()) return null;
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name_melli))).format(new Date());
        String fileNameToSave = "JPEG_" + timeStamp + ".jpg";
        File file = new File(mediaStorageDir, fileNameToSave);
        if (file.exists()) if (!file.delete()) return null;
        try {
            final FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CURRENT_IMAGE_SIZE = file.length();
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
        return fileNameToSave;
    }

    public static boolean copyImages(final ArrayList<Image> images, final int trackNumber, final Context context) {
        if (isExternalStorageWritable()) {
            final File storageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + trackNumber + "/images");
            if (!storageDir.exists()) if (!storageDir.mkdirs()) return false;
            boolean saved = false;
            for (Image image : images) {
                File from = new File(context.getExternalFilesDir(null).getAbsolutePath() +
                        context.getString(R.string.camera_folder) + image.address);
                File to = new File(storageDir + "/" + image.address);
                saved = from.renameTo(to);
            }
            return saved;
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
            return false;
        }
    }

    public static void copyImages(final Image image, final int trackNumber, final Context context) {
        if (isExternalStorageWritable()) {
            final File storageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + trackNumber + "/images");
            if (!storageDir.exists()) if (!storageDir.mkdirs()) return;
            File from = new File(context.getExternalFilesDir(null).getAbsolutePath() +
                    context.getString(R.string.camera_folder) + image.address);
            File to = new File(storageDir + "/" + image.address);
            if (!from.renameTo(to))
                new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
        }
    }

    @SuppressLint({"SimpleDateFormat"})
    public static boolean copyAudios(final ArrayList<Voice> voices, final int trackNumber, final Context context) {
        if (isExternalStorageWritable()) {
            File storageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + trackNumber + "/voices/");
            if (!storageDir.exists()) if (!storageDir.mkdirs()) return false;
            boolean saved = false;
            for (Voice voice : voices) {
                File from = new File(context.getExternalFilesDir(null).getAbsolutePath() +
                        context.getString(R.string.audio_folder) + voice.address);
                File to = new File(storageDir + "/" + voice.address);
                saved = from.renameTo(to);
            }
            return saved;
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
            return false;
        }
    }

    public static boolean copyFile(String inputPath, String outputPath) {
        File from = new File(inputPath);
        File to = new File(outputPath);
        return from.renameTo(to);
    }

    public static void copyFile(File src, Uri destUri, Context context) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(src));
            bos = new BufferedOutputStream(context.getContentResolver().openOutputStream(destUri));
            final byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String createAudioFile(Context context) {
        File storageDir = new File(context.getExternalFilesDir(null).getAbsolutePath() + context.getString(R.string.audio_folder));
        if (!storageDir.exists())
            if (!storageDir.mkdirs()) return null;
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        return "audio_" + timeStamp + ".ogg";
    }

    public static MultipartBody.Part prepareVoiceToSend(String fileName) {
        File file = new File(fileName);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(("multipart/form-data")));
        return MultipartBody.Part.createFormData("FILE", file.getName(), requestFile);
    }


    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    public static boolean writeResponseApkToDisk(ResponseBody body, Activity activity) {
        if (isExternalStorageWritable()) {
            try {
                File storageDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                if (!storageDir.exists()) {
                    if (!storageDir.mkdirs()) {
                        return false;
                    }
                }
                String timeStamp = (new SimpleDateFormat(
                        activity.getString(R.string.save_format_name))).format(new Date());
                String fileName = activity.getPackageName().substring(
                        activity.getPackageName().lastIndexOf(".") + 1) + "_" +
                        DifferentCompanyManager.getActiveCompanyName().toString() +
                        "_" + timeStamp + ".apk";
                File futureStudioIconFile = new File(storageDir, fileName);
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    byte[] fileReader = new byte[7168];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.e(".apk file", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    new CustomToast().success(
                            activity.getString(R.string.file_downloaded), Toast.LENGTH_LONG);
                    runFile(activity, fileName);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new CustomToast().warning(
                    activity.getString(R.string.error_external_storage_is_not_writable));
        }
        return false;
    }

    public static void runFile(Activity activity, String fileName) {
        StrictMode.VmPolicy.Builder newBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newBuilder.build());//TODO Create directory
        File storageDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        File toInstall = new File(storageDir, fileName);
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID +
                    ".provider", toInstall);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Uri apkUri = Uri.fromFile(toInstall);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);
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

    public static ReadingData readData() {
        File root = Environment.getExternalStorageDirectory();
        File file = findFile(root, "json.txt");

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
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, ReadingData.class);
    }

    public static String readData(File file) {
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
        return text.toString();
    }
}
