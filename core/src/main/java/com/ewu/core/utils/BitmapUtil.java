package com.ewu.core.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class BitmapUtil {

    /**
     * 获取图片类型的后缀名
     *
     * @param url
     * @return
     */
    public static String getSimpleMimeType(String url) {
        return getMimeType(url).replace("image/", "");
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * 获取图片的类型信息
     *
     * @param in
     * @return
     * @see #getImageType(byte[])
     */
    public static String getImageType(InputStream in) {
        if (in == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[8];
            in.read(bytes);
            return getImageType(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取图片的类型信息
     *
     * @param filePath
     * @return
     * @see #getImageType(byte[])
     */
    public static String getImageType(String filePath) {
        InputStream in = null;
        File file = new File(filePath);
        if (file.exists()) { //文件存在时
            try {
                in = new FileInputStream(filePath);//读入原文件
                System.out.println("in-->" + in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (in == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[8];
            in.read(bytes);
            return getImageType(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取图片的类型信息
     *
     * @param bytes 2~8 byte at beginning of the image file
     * @return image mimetype or null if the file is not image
     */
    public static String getImageType(byte[] bytes) {
        if (isJPEG(bytes)) {
            return "image/jpeg";
        }
        if (isGIF(bytes)) {
            return "image/gif";
        }
        if (isPNG(bytes)) {
            return "image/png";
        }
        if (isBMP(bytes)) {
            return "image/bmp";
        }
        return null;
    }

    private static boolean isJPEG(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(byte[] b) {
        if (b.length < 6) {
            return false;
        }
        return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(byte[] b) {
        if (b.length < 8) {
            return false;
        }
        return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78
                && b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
                && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == 0x42) && (b[1] == 0x4d);
    }

    /**
     * 获取图片类型的后缀
     *
     * @param filePath
     * @return
     */
    public static String getPostfix(String filePath) {
        String postfix = "jpg";
        String type = BitmapUtil.getSimpleMimeType(filePath);
        if (!TextUtils.isEmpty(type) && type.indexOf("png") != -1) {
            postfix = "png";
        }
        return postfix;
    }

    /**
     * 在尽量不失真的情况下，压缩图片(仅区别png，其他都压缩后都转为jpg)
     *
     * @param filePath
     * @return
     */
    private static Bitmap getSmallBitmap2(String filePath) {
        if (FileUtil.getFileSize(filePath) < 200 * 1000) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath); //未达到200KB 以上不压缩
            return bitmap;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if (bm == null) {
            return null;
        }
        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm, degree);
        FileOutputStream baos = null;
        try {
            baos = new FileOutputStream(new File(filePath));
            bm.compress(getCompressFormat(filePath), 30, baos);
            baos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bm;
    }

    private static CompressFormat getCompressFormat(String filePath) {
        CompressFormat compressFormat = CompressFormat.JPEG;
        String type = BitmapUtil.getSimpleMimeType(filePath);
        if (!TextUtils.isEmpty(type) && type.indexOf("png") != -1) {
            compressFormat = CompressFormat.PNG;
        }
        return compressFormat;
    }

    /**
     * 计算缩放大小
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * 旋转
     *
     * @param bitmap
     * @param rotate
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static long bitmap2Byte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        return baos.size();
    }

    public static void saveFile(Bitmap bm, String fileName) throws IOException {
        String subForder = "/storage/emulated/0/Download/";
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

    public static void saveFile(File imageFile) throws IOException {
        byte[] bytes = FileUtil.getBytes(imageFile);
        int lenth = (int) imageFile.length();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, lenth);
        saveFile(bitmap, imageFile.getName());
    }

    public static Bitmap getSmallBitmap(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        long fileSize = bitmap2Byte(bitmap);
        if (fileSize < 5 * 100 * 1024) {//未达到500kb 以上不压缩
            return bitmap;
        }
        Bitmap bitmap2 =Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
        return  bitmap2;
    }

    /**
     * @param path    图片路径
     * @param targetW 期待的缩放后宽度
     * @param targetH 期待的缩放后高度
     * @return
     */
    public static Bitmap equalRatioScale(String path, int targetW, int targetH) {
        // 获取option
        BitmapFactory.Options options = new BitmapFactory.Options();
        // inJustDecodeBounds设置为true,这样使用该option decode出来的Bitmap是null，
        // 只是把长宽存放到option中
        options.inJustDecodeBounds = true;
        // 此时bitmap为null
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int inSampleSize = 1; // 1是不缩放
        // 计算宽高缩放比例
        int inSampleSizeW = options.outWidth / targetW;
        int inSampleSizeH = options.outHeight / targetH;
        // 最终取大的那个为缩放比例，这样才能适配，例如宽缩放3倍才能适配屏幕，而
        // 高不缩放就可以，那样的话如果按高缩放，宽在屏幕内就显示不下了
        if (inSampleSizeW > inSampleSizeH) {
            inSampleSize = inSampleSizeW;
        } else {
            inSampleSize = inSampleSizeH;
        }
        // 设置缩放比例
        options.inSampleSize = inSampleSize;
        // 一定要记得将inJustDecodeBounds设为false，否则Bitmap为null
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * @param path    图片路径
     * @param quality 质量 0-100,100表示原图
     * @return
     */
    public static Bitmap losslessScale(String path, int quality) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        Log.e("哈哈", "原始大小：" + baos.toByteArray().length);
        // 因为质量压缩不是可以无限缩小的，所以一张高质量的图片，再怎么压缩，
        // 最终size可能还是大于你指定的size，造成异常
        // 所以不建议循环压缩，而是指定quality，进行一次压缩就可以了
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        Log.e("哈哈", "最终大小" + baos.toByteArray().length);
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                baos.toByteArray(), 0, baos.toByteArray().length);
        return compressedBitmap;
    }

    public static Bitmap losslessScale(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        Log.e("哈哈", "原始大小：" + baos.toByteArray().length);
        // 因为质量压缩不是可以无限缩小的，所以一张高质量的图片，再怎么压缩，
        // 最终size可能还是大于你指定的size，造成异常
        // 所以不建议循环压缩，而是指定quality，进行一次压缩就可以了
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        Log.e("哈哈", "最终大小" + baos.toByteArray().length);
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                baos.toByteArray(), 0, baos.toByteArray().length);
        return compressedBitmap;
    }

    /**
     * 将bitmap转为base64
     * @param imgPath
     * @param bitmap
     * @return
     */
    public static String imgToBase64(String imgPath, Bitmap bitmap) {
        if (imgPath !=null && imgPath.length() > 0) {
            bitmap = readBitmap(imgPath);
        }
        if(bitmap == null){
            //bitmap not found!!
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();

            byte[] imgBytes = out.toByteArray();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static Bitmap readBitmap(String imgPath) {
        try {
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }

    }
}
