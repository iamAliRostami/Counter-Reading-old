package com.leon.counter_reading.utils;

import static com.leon.counter_reading.utils.CustomFile.compressBitmapToByte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leon.counter_reading.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Converters {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        final Gson gson = new Gson();
        return gson.toJson(list);
    }

    public static String replaceNonstandardDigits(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            final char ch = input.charAt(i);
            if (isNonstandardDigit(ch)) {
                final int numericValue = Character.getNumericValue(ch);
                if (numericValue >= 0) builder.append(numericValue);
            } else builder.append(ch);
        }
        return builder.toString();
    }

    private static boolean isNonstandardDigit(char ch) {
        return Character.isDigit(ch) && !(ch >= '0' && ch <= '9');
    }

    @SuppressLint("SimpleDateFormat")
    public static MultipartBody.Part bitmapToFile(Bitmap bitmap, Context context) {
        final String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        final String fileNameToSave = "JPEG_" + new Random().nextInt() + "_" + timeStamp + ".jpg";
        final File f = new File(context.getCacheDir(), fileNameToSave);
        try {
            if (!f.createNewFile()) return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }

    public static String convertToAscii(String s) {
        String sTemp = Normalizer.normalize(s, Normalizer.Form.NFKD);
        String regex = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
        String finalString = new String(sTemp.replaceAll(regex, "").getBytes(StandardCharsets.US_ASCII), StandardCharsets.US_ASCII);
        return new String(finalString.getBytes(), StandardCharsets.UTF_8);
    }
}
