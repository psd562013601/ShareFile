package com.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import android.os.Environment;

public class FileOperation {

	// 手机存储文件夹目录：/sdcard/FileShare
	private static String localDir = Environment.getExternalStorageDirectory()
			.getPath() + "/FileShare";
	private static int BUFFER_SIZE = 1024;

	public static boolean fileDownload(String tarDir) {
		SmbFileInputStream in = null;
		FileOutputStream out = null;

		File localFileFolder = new File(localDir);
		if (!localFileFolder.exists()) {
			localFileFolder.mkdirs();
		}

		// 下载文件至本地
		SmbFile smbFile = null;
		try {
			smbFile = new SmbFile(tarDir);
			String fileName = smbFile.getName();
			File localFile = new File(localDir + File.separator + fileName);
			// 创建文件
			if (!localFile.exists()) {
				localFile.createNewFile();
			} else {
				localFile = getRenamedFile(fileName);
				localFile.createNewFile();
			}
			
			in = new SmbFileInputStream(smbFile);
			out = new FileOutputStream(localFile);
			
            byte[] buffer = new byte[BUFFER_SIZE];  
            int len = -1;  
            while((len=in.read(buffer)) != -1){  
                out.write(buffer, 0, len);  
            }  
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}



	public static boolean fileUpload(String dirFrom,String dirTo){
		//定义缓冲流
		FileInputStream in = null;  
        SmbFileOutputStream out = null;  
		try {
			//建立本地和远程文件
			File local = new File(dirFrom);
			SmbFile remote = new SmbFile(dirTo + local.getName());	
			//将文件读入缓冲流
			in = new FileInputStream(local);  
	        out = new SmbFileOutputStream(remote);	
	        //上传
	        byte[] buffer = new byte[1024];  
            int len = -1;  
            while((len=in.read(buffer)) != -1){  
                out.write(buffer, 0, len);  
            }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}


	public static File getRenamedFile(String fileName) {
		int i=2;
		File tmpFile = null;
		String prefix = "";
		String suffix = "";
		int index = fileName.lastIndexOf('.');
		if(index<0) {// 没有点
			prefix = fileName;
			suffix = "";
		} else {
			prefix = fileName.substring(0, index);
			suffix = fileName.substring(index);
		}
		do {
			String tmpFileName = prefix + " (" + (i++) + ")" + suffix;
			tmpFile = new File(localDir + File.separator + tmpFileName);
		} while(tmpFile.exists());
		
		return tmpFile;
	}
}
