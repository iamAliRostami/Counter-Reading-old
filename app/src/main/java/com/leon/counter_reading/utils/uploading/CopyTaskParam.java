package com.leon.counter_reading.utils.uploading;

import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;

public class CopyTaskParam {
    public final UsbFile from;
    public final File to;
    public int position;

    public CopyTaskParam(UsbFile from, File to) {
        this.from = from;
        this.to = to;
    }
}
