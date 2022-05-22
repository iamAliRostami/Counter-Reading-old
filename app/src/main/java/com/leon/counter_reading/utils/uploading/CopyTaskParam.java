package com.leon.counter_reading.utils.uploading;


import java.io.File;

import me.jahnen.libaums.fs.UsbFile;

public class CopyTaskParam {
    public final UsbFile from;
    public final File to;
    public int position;

    public CopyTaskParam(UsbFile from, File to) {
        this.from = from;
        this.to = to;
    }
}
