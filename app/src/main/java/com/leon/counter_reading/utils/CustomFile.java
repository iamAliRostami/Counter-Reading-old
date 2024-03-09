package com.leon.counter_reading.utils;

import static com.leon.counter_reading.helpers.Constants.CURRENT_IMAGE_SIZE;
import static com.leon.counter_reading.helpers.MyApplication.getImageQuality;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class CustomFile {

    public static boolean isExternalStorageWritable() {
        String state = "";
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
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

    public static String saveTempBitmap(final String path, final Context context) {
        if (isExternalStorageWritable()) {
            return saveImage(path, context);
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
            return context.getString(R.string.error_external_storage_is_not_writable);
        }
    }

    public static String saveTempBitmap(final Uri uri, final Context context, boolean mark) {
        if (isExternalStorageWritable()) {
            return saveImage(uri, context, mark);
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
            return context.getString(R.string.error_external_storage_is_not_writable);
        }
    }

    @SuppressLint("SimpleDateFormat")
    static String saveImage(final String path, final Context context) {
        Bitmap bitmapImage = compressBitmap(BitmapFactory.decodeFile(path));
        File mediaStorageDir = new File(context.getExternalFilesDir(null) +
                context.getString(R.string.camera_folder));
        if (!mediaStorageDir.exists()) if (!mediaStorageDir.mkdirs()) return null;
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name_melli))).format(new Date());
        String fileNameToSave = "JPEG_" + timeStamp + ".jpg";
        File file = new File(mediaStorageDir, fileNameToSave);
        if (file.exists()) if (!file.delete()) return null;
        if (bitmapImage != null) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CURRENT_IMAGE_SIZE = file.length();
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()},
                new String[]{"image/jpeg"}, null);
        deleteRecursive(new File(context.getFilesDir(), context.getString(R.string.camera_folder)));
        return fileNameToSave;
    }

    private static void deleteRecursive(final File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    Log.e("delete?", String.valueOf(new File(dir, child).delete()));
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    static String saveImage(final Uri uri, final Context context, boolean mark) {
        Bitmap bitmapImage = compressBitmap(uri, context);

        final File mediaStorageDir = new File(context.getExternalFilesDir(null) + context.getString(R.string.camera_folder));
        if (!mediaStorageDir.exists()) if (!mediaStorageDir.mkdirs()) return null;
        final String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name_melli))).format(new Date());
        final String fileNameToSave = "JPEG_" + timeStamp + ".jpg";
        final File file = new File(mediaStorageDir, fileNameToSave);
        if (file.exists()) if (!file.delete()) return null;
        if (bitmapImage != null) {
            try {
                if (mark)
                    bitmapImage = mark(bitmapImage);
                FileOutputStream out = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CURRENT_IMAGE_SIZE = file.length();
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
        return fileNameToSave;
    }

    public static Bitmap mark(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);

        float yCoordinate = (float) bitmap.getHeight();
//        float xCoordinate = (float) bitmap.getWidth() / 36;
        float xCoordinate = 0;
        canvas.drawText("G", xCoordinate, yCoordinate, paint);

        return result;
    }

    public static String saveTempBitmap(final Bitmap bitmap, Context context) {
        if (isExternalStorageWritable()) {
            return saveImage(bitmap, context);
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
            return context.getString(R.string.error_external_storage_is_not_writable);
        }
    }

    @SuppressLint("SimpleDateFormat")
    static String saveImage(final Bitmap bitmapImage, Context context) {
        final File mediaStorageDir = new File(context.getExternalFilesDir(null) + context.getString(R.string.camera_folder));
        if (!mediaStorageDir.exists()) if (!mediaStorageDir.mkdirs()) return null;
        final String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name_melli))).format(new Date());
        final String fileNameToSave = "JPEG_" + timeStamp + ".jpg";
        final File file = new File(mediaStorageDir, fileNameToSave);
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


    public static Bitmap compressBitmap(final Uri uri, Context context) {

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap original = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            if (stream.toByteArray().length > getImageQuality() * 10) {
                final int width, height;
                if (original.getHeight() > original.getWidth()) {
                    height = Math.min(getImageQuality() * 10, original.getHeight());
                    width = original.getWidth() / (original.getHeight() / height);
                } else {
                    width = Math.min(getImageQuality() * 10, original.getWidth());
                    height = original.getHeight() / (original.getWidth() / width);
                }
                original = Bitmap.createScaledBitmap(original, width, height, false);
                stream.reset();
                stream = new ByteArrayOutputStream();
                original.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            }
            CURRENT_IMAGE_SIZE = stream.toByteArray().length;
            return original;
        } catch (FileNotFoundException e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        return null;
    }

    public static Bitmap compressBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;  //you can also calculate your inSampleSize
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];

        Bitmap photo = BitmapFactory.decodeFile(path, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int currSize;
        int quality = (int) ((getImageQuality() * 100) / (new File(path).length()));

        do {
//            baos.reset();
            photo.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            currSize = baos.toByteArray().length / 1000;
            if (currSize > getImageQuality()) {
                quality -= 20;
            }
        } while (currSize > 20 &&
                currSize > getImageQuality());


        Log.e("quality", String.valueOf(quality));
//        baos.reset();
        photo.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        CURRENT_IMAGE_SIZE = baos.toByteArray().length / 1000;
        Log.e("size 2", String.valueOf(CURRENT_IMAGE_SIZE));
        return photo;
    }

    public static Bitmap compressBitmap(Bitmap original) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            if (baos.toByteArray().length > getImageQuality() * 10) {
                final int width, height;
                if (original.getHeight() > original.getWidth()) {
                    height = Math.min(getImageQuality() * 10, original.getHeight());
                    width = original.getWidth() / (original.getHeight() / height);
                } else {
                    width = Math.min(getImageQuality() * 10, original.getWidth());
                    height = original.getHeight() / (original.getWidth() / width);
                }
                original = Bitmap.createScaledBitmap(original, width, height, false);
                baos = new ByteArrayOutputStream();
                original.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            }
            CURRENT_IMAGE_SIZE = baos.toByteArray().length;
            return original;
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        return null;
    }

    public static Bitmap compressBitmap(Bitmap original, String path) {
        try {
            FileOutputStream out = new FileOutputStream(path);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.WEBP, 80, out);
            CURRENT_IMAGE_SIZE = (long) original.getRowBytes() * original.getHeight();

            return original;
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
        return null;
    }

    public static byte[] compressBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createTempImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(context.getString(R.string.save_format_name)).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createImageFile(Context context) {
        String timeStamp = new SimpleDateFormat(context.getString(R.string.save_format_name)).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = new File(context.getFilesDir(), context.getString(R.string.camera_folder));
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return new File(storageDir, imageFileName);
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
                final File storageDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                if (!storageDir.exists()) {
                    if (!storageDir.mkdirs()) {
                        return false;
                    }
                }
                final String timeStamp = (new SimpleDateFormat(activity.getString(R.string.save_format_name)))
                        .format(new Date());
                final String fileName = activity.getPackageName().substring(activity
                        .getPackageName().lastIndexOf(".") + 1) + "_" + timeStamp + ".apk";
                final File futureStudioIconFile = new File(storageDir, fileName);
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    final byte[] fileReader = new byte[7168];
                    final long fileSize = body.contentLength();
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
        final StrictMode.VmPolicy.Builder newBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newBuilder.build());
        final File storageDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        final File toInstall = new File(storageDir, fileName);
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final Uri apkUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID +
                    ".FileProvider", toInstall);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            final Uri apkUri = Uri.fromFile(toInstall);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);
    }

    public static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
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

    public static String readData(InputStream inputStream) throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            total.append(line).append('\n');
        }
        return total.toString();
    }
}
