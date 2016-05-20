package com.sdc.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2016-05-10.
 */
public class FileUtil {

    private static String separatorChar = StringUtil.separatorChar;
    /**
     * 查看路径中的文件夹是否存在，如果不存在则创建文件夹
     * @param file 文件或文件夹路径
     */
    public static void checkDir(String file){
        String dir = getFilePath(file);
        File f = new File(dir);
        if (!f.exists() || !f.isDirectory()){
            f.mkdirs();
        }
    }

    /**
     * 从完整路径中获取所在文件夹，如果路径中不包括文件，认为该路径为一个文件夹
     * @param file String类型，完整路径
     * @return String类型，所在文件夹
     */
    public static String getFilePath(String file){
        String fPath = file.trim();
        fPath = fPath.substring(0, fPath.lastIndexOf(separatorChar));
        if (fPath.substring(fPath.length() - 1) != separatorChar){
            fPath += separatorChar;
        }
        return fPath;
    }

    public static String getPackagePath(String packageName){
        String outPath = getCurrentPath();
        outPath += separatorChar+ "src" + separatorChar + packageName.replace(".", separatorChar) + separatorChar;
        return outPath;
    }

    public static boolean saveToFile(String file, String content){
        checkDir(file);
        deleteFile(file);
        try{
            FileWriter fw = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            pw.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteFile(String file){
        File f = new File(file);
        if (f.exists() && f.isFile()){
            f.delete();
        }
    }

    public static String getCurrentPath(){
        File f = new File("");
        return f.getAbsolutePath();
    }
}
