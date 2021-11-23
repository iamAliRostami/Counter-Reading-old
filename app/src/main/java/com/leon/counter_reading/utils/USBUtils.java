package com.leon.counter_reading.utils;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.leon.counter_reading.helpers.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.regex.Pattern;

public class USBUtils {

    public static boolean isMassStorageDevice(UsbDevice device) {
        boolean result = false;

        int interfaceCount = device.getInterfaceCount();
        for (int i = 0; i < interfaceCount; i++) {
            UsbInterface usbInterface = device.getInterface(i);
            Log.i("TAG", "found usb interface: " + usbInterface);

            // we currently only support SCSI transparent command set with
            // bulk transfers only!
            if (usbInterface.getInterfaceClass() != UsbConstants.USB_CLASS_MASS_STORAGE
                    || usbInterface.getInterfaceSubclass() != Constants.INTERFACE_SUBCLASS
                    || usbInterface.getInterfaceProtocol() != Constants.INTERFACE_PROTOCOL) {
                Log.i("TAG", "device interface not suitable!");
                continue;
            }

            // Every mass storage device has exactly two endpoints
            // One IN and one OUT endpoint
            int endpointCount = usbInterface.getEndpointCount();
            if (endpointCount != 2) {
                Log.w("TAG", "inteface endpoint count != 2");
            }

            UsbEndpoint outEndpoint = null;
            UsbEndpoint inEndpoint = null;
            for (int j = 0; j < endpointCount; j++) {
                UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                Log.i("TAG", "found usb endpoint: " + endpoint);
                if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                        outEndpoint = endpoint;
                    } else {
                        inEndpoint = endpoint;
                    }
                }
            }

            if (outEndpoint == null || inEndpoint == null) {
                Log.e("TAG", "Not all needed endpoints found!");
                continue;
            }

            result = true;
        }

        return result;
    }
}
