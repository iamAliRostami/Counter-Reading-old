package com.leon.counter_reading.utils;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.net.Uri;
import android.view.KeyEvent;

import com.github.mjdev.libaums.fs.UsbFile;
import com.leon.counter_reading.R;
import com.leon.counter_reading.fragments.ExplorerFragment;
import com.leon.counter_reading.helpers.Constants;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;

public class USBUtils {

    private static final HashMap<String, Integer> sMimeIcons = new HashMap<>();

    static {
        int icon;

        // Package
        icon = R.drawable.ic_doc_apk_alpha;
        add("application/vnd.android.package-archive", icon);

        // Audio
        icon = R.drawable.ic_doc_audio_alpha;
        add("application/ogg", icon);
        add("application/x-flac", icon);

        // Certificate
        icon = R.drawable.ic_doc_certificate_alpha;
        add("application/pgp-keys", icon);
        add("application/pgp-signature", icon);
        add("application/x-pkcs12", icon);
        add("application/x-pkcs7-certreqresp", icon);
        add("application/x-pkcs7-crl", icon);
        add("application/x-x509-ca-cert", icon);
        add("application/x-x509-user-cert", icon);
        add("application/x-pkcs7-certificates", icon);
        add("application/x-pkcs7-mime", icon);
        add("application/x-pkcs7-signature", icon);

        // Source code
        icon = R.drawable.ic_doc_codes_alpha;
        add("application/rdf+xml", icon);
        add("application/rss+xml", icon);
        add("application/x-object", icon);
        add("application/xhtml+xml", icon);
        add("text/css", icon);
        add("text/html", icon);
        add("text/xml", icon);
        add("text/x-c++hdr", icon);
        add("text/x-c++src", icon);
        add("text/x-chdr", icon);
        add("text/x-csrc", icon);
        add("text/x-dsrc", icon);
        add("text/x-csh", icon);
        add("text/x-haskell", icon);
        add("text/x-java", icon);
        add("text/x-literate-haskell", icon);
        add("text/x-pascal", icon);
        add("text/x-tcl", icon);
        add("text/x-tex", icon);
        add("application/x-latex", icon);
        add("application/x-texinfo", icon);
        add("application/atom+xml", icon);
        add("application/ecmascript", icon);
        add("application/json", icon);
        add("application/javascript", icon);
        add("application/xml", icon);
        add("text/javascript", icon);
        add("application/x-javascript", icon);

        // Compressed
        icon = R.drawable.ic_doc_compressed_alpha;
        add("application/mac-binhex40", icon);
        add("application/rar", icon);
        add("application/zip", icon);
        add("application/x-apple-diskimage", icon);
        add("application/x-debian-package", icon);
        add("application/x-gtar", icon);
        add("application/x-iso9660-image", icon);
        add("application/x-lha", icon);
        add("application/x-lzh", icon);
        add("application/x-lzx", icon);
        add("application/x-stuffit", icon);
        add("application/x-tar", icon);
        add("application/x-webarchive", icon);
        add("application/x-webarchive-xml", icon);
        add("application/gzip", icon);
        add("application/x-7z-compressed", icon);
        add("application/x-deb", icon);
        add("application/x-rar-compressed", icon);

        // Contact
        icon = R.drawable.ic_doc_contact_am_alpha;
        add("text/x-vcard", icon);
        add("text/vcard", icon);

        // Event
        icon = R.drawable.ic_doc_event_am_alpha;
        add("text/calendar", icon);
        add("text/x-vcalendar", icon);

        // Font
        icon = R.drawable.ic_doc_font_alpha;
        add("application/x-font", icon);
        add("application/font-woff", icon);
        add("application/x-font-woff", icon);
        add("application/x-font-ttf", icon);

        // Image
        icon = R.drawable.ic_doc_image_alpha;
        add("application/vnd.oasis.opendocument.graphics", icon);
        add("application/vnd.oasis.opendocument.graphics-template", icon);
        add("application/vnd.oasis.opendocument.image", icon);
        add("application/vnd.stardivision.draw", icon);
        add("application/vnd.sun.xml.draw", icon);
        add("application/vnd.sun.xml.draw.template", icon);

        // PDF
        icon = R.drawable.ic_doc_pdf_alpha;
        add("application/pdf", icon);

        // Presentation
        icon = R.drawable.ic_doc_presentation_alpha;
        add("application/vnd.stardivision.impress", icon);
        add("application/vnd.sun.xml.impress", icon);
        add("application/vnd.sun.xml.impress.template", icon);
        add("application/x-kpresenter", icon);
        add("application/vnd.oasis.opendocument.presentation", icon);

        // Spreadsheet
        icon = R.drawable.ic_doc_spreadsheet_am_alpha;
        add("application/vnd.oasis.opendocument.spreadsheet", icon);
        add("application/vnd.oasis.opendocument.spreadsheet-template", icon);
        add("application/vnd.stardivision.calc", icon);
        add("application/vnd.sun.xml.calc", icon);
        add("application/vnd.sun.xml.calc.template", icon);
        add("application/x-kspread", icon);

        // Text
        icon = R.drawable.ic_doc_text_am_alpha;
        add("application/vnd.oasis.opendocument.text", icon);
        add("application/vnd.oasis.opendocument.text-master", icon);
        add("application/vnd.oasis.opendocument.text-template", icon);
        add("application/vnd.oasis.opendocument.text-web", icon);
        add("application/vnd.stardivision.writer", icon);
        add("application/vnd.stardivision.writer-global", icon);
        add("application/vnd.sun.xml.writer", icon);
        add("application/vnd.sun.xml.writer.global", icon);
        add("application/vnd.sun.xml.writer.template", icon);
        add("application/x-abiword", icon);
        add("application/x-kword", icon);

        // Video
        icon = R.drawable.ic_doc_video_am_alpha;
        add("application/x-quicktimeplayer", icon);
        add("application/x-shockwave-flash", icon);

        // Word
        icon = R.drawable.ic_doc_word_alpha;
        add("application/msword", icon);
        add("application/vnd.openxmlformats-officedocument.wordprocessingml.document", icon);
        add("application/vnd.openxmlformats-officedocument.wordprocessingml.template", icon);

        // Excel
        icon = R.drawable.ic_doc_excel_alpha;
        add("application/vnd.ms-excel", icon);
        add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", icon);
        add("application/vnd.openxmlformats-officedocument.spreadsheetml.template", icon);

        // Powerpoint
        icon = R.drawable.ic_doc_powerpoint_alpha;
        add("application/vnd.ms-powerpoint", icon);
        add("application/vnd.openxmlformats-officedocument.presentationml.presentation", icon);
        add("application/vnd.openxmlformats-officedocument.presentationml.template", icon);
        add("application/vnd.openxmlformats-officedocument.presentationml.slideshow", icon);
    }

    public static boolean isMassStorageDevice(UsbDevice device) {
        boolean result = false;

        int interfaceCount = device.getInterfaceCount();
        for (int i = 0; i < interfaceCount; i++) {
            UsbInterface usbInterface = device.getInterface(i);
            // we currently only support SCSI transparent command set with
            // bulk transfers only!
            if (usbInterface.getInterfaceClass() != UsbConstants.USB_CLASS_MASS_STORAGE
                    || usbInterface.getInterfaceSubclass() != Constants.INTERFACE_SUBCLASS
                    || usbInterface.getInterfaceProtocol() != Constants.INTERFACE_PROTOCOL) {
                continue;
            }

            // Every mass storage device has exactly two endpoints
            // One IN and one OUT endpoint
            int endpointCount = usbInterface.getEndpointCount();
            UsbEndpoint outEndpoint = null;
            UsbEndpoint inEndpoint = null;
            for (int j = 0; j < endpointCount; j++) {
                UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                        outEndpoint = endpoint;
                    } else {
                        inEndpoint = endpoint;
                    }
                }
            }

            if (outEndpoint == null || inEndpoint == null)
                continue;
            result = true;
        }

        return result;
    }

    public static boolean isConfirmButton(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                return true;
            default:
                return false;
        }
    }

    public static Comparator<UsbFile> comparator = new Comparator<UsbFile>() {
        @Override
        public int compare(UsbFile lhs, UsbFile rhs) {
            switch (ExplorerFragment.sortByCurrent) {
                case Constants.SORT_BY_NAME:
                    return sortByName(lhs, rhs);
                case Constants.SORT_BY_DATE:
                    return sortByDate(lhs, rhs);
                case Constants.SORT_BY_SIZE:
                    return sortBySize(lhs, rhs);
                default:
                    break;
            }
            return 0;
        }

        int extractInt(String s) {
            int result = 0;
            // return 0 if no digits found
            try {
                String num = s.replaceAll("\\D", "");
                result = num.isEmpty() ? 0 : Integer.parseInt(num);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        int checkIfDirectory(UsbFile lhs, UsbFile rhs) {
            if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            }

            if (rhs.isDirectory() && !lhs.isDirectory()) {
                return 1;
            }

            return 0;
        }

        int sortByName(UsbFile lhs, UsbFile rhs) {
            int result;
            int dir = checkIfDirectory(lhs, rhs);
            if (dir != 0)
                return dir;
            // Check if there is any number
            String lhsNum = lhs.getName().replaceAll("\\D", "");
            String rhsNum = rhs.getName().replaceAll("\\D", "");
            int lhsRes;
            int rhsRes;
            if (!lhsNum.isEmpty() && !rhsNum.isEmpty()) {
                lhsRes = extractInt(lhs.getName());
                rhsRes = extractInt(rhs.getName());
                return lhsRes - rhsRes;
            }
            result = lhs.getName().compareToIgnoreCase(rhs.getName());
            return result;
        }

        int sortByDate(UsbFile lhs, UsbFile rhs) {
            long result;
            int dir = checkIfDirectory(lhs, rhs);
            if (dir != 0)
                return dir;

            result = lhs.lastModified() - rhs.lastModified();

            return (int) result;
        }

        int sortBySize(UsbFile lhs, UsbFile rhs) {
            long result = 0;
            int dir = checkIfDirectory(lhs, rhs);
            if (dir != 0)
                return dir;

            try {
                result = lhs.getLength() - rhs.getLength();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return (int) result;
        }
    };

    private static void add(String mimeType, int resId) {
        if (sMimeIcons.put(mimeType, resId) != null) {
            throw new RuntimeException(mimeType + " already registered!");
        }
    }


    public static int loadMimeIcon(String mimeType) {

        // Look for exact match first
        Integer resId = sMimeIcons.get(mimeType);
        if (resId != null) {
            return resId;
        }

        if (mimeType == null) {
            // TODO: generic icon?
            return R.drawable.ic_doc_generic_am_alpha;
        }

        // Otherwise look for partial match
        final String typeOnly = mimeType.split("/")[0];
        if ("audio".equals(typeOnly)) {
            return R.drawable.ic_doc_audio_alpha;
        } else if ("image".equals(typeOnly)) {
            return R.drawable.ic_doc_image_alpha;
        } else if ("text".equals(typeOnly)) {
            return R.drawable.ic_doc_text_am_alpha;
        } else if ("video".equals(typeOnly)) {
            return R.drawable.ic_doc_video_am_alpha;
        } else {
            return R.drawable.ic_doc_generic_am_alpha;
        }
    }

    public static String getMimetype(File f) {
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri
                .fromFile(f).toString().toLowerCase());

        return getMimetype(extension);
    }

    public static String getMimetype(String extension) {
        return android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                extension);
    }

    public static String getHumanSortBy(Context context) {
        switch (ExplorerFragment.sortByCurrent) {
            case Constants.SORT_BY_DATE:
                return context.getString(R.string.date);
            case Constants.SORT_BY_SIZE:
                return context.getString(R.string.size);
            case Constants.SORT_BY_NAME:
            default:
                return context.getString(R.string.name);
        }
    }
}
