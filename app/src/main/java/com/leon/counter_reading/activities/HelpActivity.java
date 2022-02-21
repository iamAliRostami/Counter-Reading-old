package com.leon.counter_reading.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Debug;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityHelpBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelpActivity extends BaseActivity {
    private final int maxNumber = 37;
    private ActivityHelpBinding binding;
    private Activity activity;
    private int pageNumber = 0;

    @Override
    protected void initialize() {
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        try {
            openPdfFromRaw();
            binding.imageButtonPrevious.setVisibility(View.GONE);
            binding.imageButtonNext.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setOnArrowButtonClickListener();
    }

    private void setOnArrowButtonClickListener() {
        binding.imageButtonNext.setOnClickListener(v -> {
            binding.imageButtonPrevious.setVisibility(View.VISIBLE);
            pageNumber++;
            if (pageNumber + 1 == maxNumber)
                binding.imageButtonNext.setVisibility(View.GONE);
            try {
                openPdfFromRaw();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        binding.imageButtonPrevious.setOnClickListener(v -> {
            pageNumber--;
            if (pageNumber == 0)
                binding.imageButtonPrevious.setVisibility(View.GONE);
            try {
                openPdfFromRaw();
                binding.imageButtonNext.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void openPdfFromRaw() throws IOException {
        // Copy sample.pdf from 'res/raw' folder into cache so PdfRenderer can handle it
        File fileCopy = new File(getCacheDir(), "HELP_" + BuildConfig.VERSION_CODE + "_PAGE_".concat(String.valueOf(pageNumber)));
//        copyToCache(fileCopy, R.raw.sample);
        copyToCache(fileCopy, R.raw.counter_reading);
        // We get a page from the PDF doc by calling 'open'
        ParcelFileDescriptor fileDescriptor =
                ParcelFileDescriptor.open(fileCopy,
                        ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer mPdfRenderer = new PdfRenderer(fileDescriptor);
        PdfRenderer.Page mPdfPage = mPdfRenderer.openPage(pageNumber);
        // Create a new bitmap and render the page contents into it
        Bitmap bitmap = Bitmap.createBitmap(mPdfPage.getWidth(),
                mPdfPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        mPdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // Set the bitmap in the ImageView
        binding.imageView.setImageBitmap(bitmap);
    }

    void copyToCache(File file, @RawRes int pdfResource) throws IOException {
        if (!file.exists()) {
            //Get input stream object to read the pdf
            InputStream input = getResources().openRawResource(pdfResource);
            FileOutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int size;
            // Copy the entire contents of the file
            while ((size = input.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            //Close the buffer
            input.close();
            output.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_contact_us) {
            Intent intent = new Intent(activity, ContactUsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}