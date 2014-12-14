package com.sds.ivor.fileexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class Util {

    private static final String TAG = Util.class.getName();
    private static File COPIED_FILE = null;
    private static int pasteMode = 1;


    public static final int PASTE_MODE_COPY = 0;
    public static final int PASTE_MODE_MOVE = 1;


    private Util() {
    }


    static boolean isMusic(File file) {

        Uri uri = Uri.fromFile(file);
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

        if (type == null)
            return false;
        else
            return (type.toLowerCase().startsWith("audio/"));

    }

    static boolean isVideo(File file) {

        Uri uri = Uri.fromFile(file);
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

        if (type == null)
            return false;
        else
            return (type.toLowerCase().startsWith("video/"));
    }

    public static boolean isPicture(File file) {

        Uri uri = Uri.fromFile(file);
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

        if (type == null)
            return false;
        else
            return (type.toLowerCase().startsWith("image/"));
    }

    public static boolean isProtected(File path) {
        return (!path.canRead() && !path.canWrite());
    }

    public static boolean isSdCard(File file) {

        try {
            return (file.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getCanonicalPath()));
        } catch (IOException e) {
            return false;
        }

    }

    public static void openFile(final Context context, final File target) {
        final String mime = MimeTypes.getMimeType(target);
        final Intent i = new Intent(Intent.ACTION_VIEW);
       /* Toast.makeText(context,mime,Toast.LENGTH_SHORT).show();*/
        if (mime != null) {
            i.setDataAndType(Uri.fromFile(target), mime);
        } else {
            i.setDataAndType(Uri.fromFile(target), "*/*");
        }

        if (context.getPackageManager().queryIntentActivities(i, 0).isEmpty()) {
            Toast.makeText(context, "Could not open this file sorry about it! =(", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        try {
            context.startActivity(i);
        } catch (Exception e) {
            Toast.makeText(context,
                    "OMG! -> " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static Drawable getIcon(Context mContext, File file) {

        if (!file.isFile()) //dir
        {
            if (Util.isProtected(file)) {
                return mContext.getResources().getDrawable(R.drawable.filetype_sys_dir);

            } else if (Util.isSdCard(file)) {
                return mContext.getResources().getDrawable(R.drawable.filetype_sdcard);
            } else {
                return mContext.getResources().getDrawable(R.drawable.filetype_dir);
            }
        } else //file
        {
            String fileName = file.getName();
            if (Util.isProtected(file)) {
                return mContext.getResources().getDrawable(R.drawable.filetype_sys_file);

            }
            if (fileName.endsWith(".apk")) {
                return mContext.getResources().getDrawable(R.drawable.filetype_apk);
            }
            if (fileName.endsWith(".zip")) {
                return mContext.getResources().getDrawable(R.drawable.filetype_zip);
            } else if (Util.isMusic(file)) {
                return mContext.getResources().getDrawable(R.drawable.filetype_music);
            } else if (Util.isVideo(file)) {
                return mContext.getResources().getDrawable(R.drawable.filetype_video);
            } else if (Util.isPicture(file)) {
                return mContext.getResources().getDrawable(R.drawable.filetype_image);
            } else {
                return mContext.getResources().getDrawable(R.drawable.filetype_generic);
            }
        }

    }

    public static String prepareMeta(FileListEntry file, Context context) {

        File f = file.getPath();
        try {
            if (isProtected(f)) {
                return context.getString(R.string.system_path);
            }
            if (file.getPath().isFile()) {
                return context.getString(R.string.size_is, FileUtils.byteCountToDisplaySize(file.getSize()));
            }

        } catch (Exception e) {
            Log.e(Util.class.getName(), e.getMessage());
        }

        return "";
    }

    public static CharSequence[] getFileProperties(FileListEntry file, Activity context) {

        if (Util.isSdCard(file.getPath())) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long sdAvailSize = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            long totalSize = (long) stat.getBlockCount() * (long) stat.getBlockSize();

            return new CharSequence[]{context.getString(R.string.total_capacity, Util.getSizeStr(totalSize)),
                    context.getString(R.string.free_space, Util.getSizeStr(sdAvailSize))};
        } else if (file.getPath().isFile())
            return new CharSequence[]{context.getString(R.string.filepath_is, file.getPath().getAbsolutePath()),
                    context.getString(R.string.mtime_is, DateFormat.getDateFormat(context).format(file.getLastModified())),
                    context.getString(R.string.size_is, FileUtils.byteCountToDisplaySize(file.getSize()))};

        else {
            return new CharSequence[]{context.getString(R.string.filepath_is, file.getPath().getAbsolutePath()),
                    context.getString(R.string.mtime_is, DateFormat.getDateFormat(context).format(file.getLastModified())),
                    context.getString(R.string.size_is, FileUtils.byteCountToDisplaySize(file.getSize()))};
        }
    }

    private static String getSizeStr(long bytes) {

        if (bytes >= FileUtils.ONE_GB) {
            return (double) Math.round((((double) bytes / FileUtils.ONE_GB) * 100)) / 100 + " GB";
        } else if (bytes >= FileUtils.ONE_MB) {
            return (double) Math.round((((double) bytes / FileUtils.ONE_MB) * 100)) / 100 + " MB";
        } else if (bytes >= FileUtils.ONE_KB) {
            return (double) Math.round((((double) bytes / FileUtils.ONE_KB) * 100)) / 100 + " KB";
        }

        return bytes + " bytes";
    }
}
