package com.kalzn.judger.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileIO {
    public static String readWholeStringFile(File file) throws IOException {
        FileReader reader = new FileReader(file);
        StringBuffer stringBuffer = new StringBuffer();
        int ch;
        while((ch = reader.read()) != -1) {
            stringBuffer.append((char)ch);
        }
        return stringBuffer.toString();

    }
    public static String readWholeStringFile(String path) throws IOException {
        return readWholeStringFile(new File(path));
    }

    public static void copy(String file1, String file2) throws IOException {
        FileChannel input = null;
        FileChannel output = null;

        StringBuffer stringBuffer = new StringBuffer(file2);
        if (file2.lastIndexOf('/') != -1) {
            stringBuffer.delete(file2.lastIndexOf('/'), file2.length());
        }
        File dir = new File(stringBuffer.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            input = new FileInputStream(new File(file1)).getChannel();
            output = new FileOutputStream(new File(file2)).getChannel();
            output.transferFrom(input, 0, input.size());
        }  finally {
            if (input != null)
                input.close();
            if (output != null)
                output.close();
        }
    }

    public static Long checkSize(File dir) {
        Long result = (long)0;
        if(dir.isDirectory()){
            File[] childrenFiles = dir.listFiles();
            for (File childFile:childrenFiles){
                result += checkSize(childFile);
            }
        }
        else {
            result = dir.length();
        }
        return result;
    }

    public static Long delete(File file) {
        Long result = (long)0;
        //目录
        if(file.isDirectory()){
            File[] childrenFiles = file.listFiles();
            for (File childFile:childrenFiles){
                result += delete(childFile);
            }
        } else {
            result = file.length();
        }
        file.delete();
        return result;
    }
    public static Long delete(String file) {
        return delete(new File(file));
    }

    public static void unzip(String zipfile, String dir, int bufferSize) throws IOException{
        String inputFile = zipfile;
        String destDirPath = dir;
        File srcFile = new File(inputFile);//获取当前压缩文件
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new IOException("Src file is not found");
        }
        ZipFile zipFile = new ZipFile(srcFile);//创建压缩文件对象
        //开始解压
        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 如果是文件夹，就创建个文件夹
            if (entry.isDirectory()) {
                String dirPath = destDirPath + "/" + entry.getName();
                srcFile.mkdirs();
            } else {
                // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                File targetFile = new File(destDirPath + "/" + entry.getName());
                // 保证这个文件的父文件夹必须要存在
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                targetFile.createNewFile();
                // 将压缩文件内容写入到这个文件中
                InputStream is = zipFile.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(targetFile);
                int len;
                byte[] buf = new byte[bufferSize];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                // 关流顺序，先打开的后关闭
                fos.close();
                is.close();
            }
        }
    }

}
