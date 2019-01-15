package com.huaneng.zhdj.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/11/18.
 */

public class FileUtils {

    // 读取assets中的文件
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            Log.e("getFromAssets", e.getMessage() + "");
        }
        return "";
    }

    public static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 读取文本文件
     *
     * @param fileName
     * @return
     */
    public static String read(String fileName) {
        return read(new File(fileName));
    }

    /**
     * 读取文本文件
     * @return
     */
    public static String read(File file) {
        return read(file, null);
    }

    /**
     * 读取文本文件
     * @return
     */
    public static String read(File file, String charsetName) {
        try {
            FileInputStream in = new FileInputStream(file);
            return readInStream(in, charsetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String readInStream(FileInputStream inStream, String charsetName) {
        try {
            if (TextUtils.isEmpty(charsetName)) {
                charsetName = "UTF-8";
            }
            StringBuffer sb=new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream, charsetName));
            char[] chs=new char[1024];
            int len=0;
            while((len=br.read(chs))!=-1){
                sb.append(new String(chs, 0, len));
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            Log.i("readInStream", e.getMessage());
        }
        return null;
    }
}
