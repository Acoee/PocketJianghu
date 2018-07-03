package com.app.jianghu.common.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.app.jianghu.common.ImageUtils;

/**
 * 图片缓存-文件缓存
 * @author songqy
 * @version 1.0.0
 * @time 2014-10-08
 *
 */
public class ImageFileCache {
	private static final String CACHDIR = "maisulang/ImgCach";
	private static final String WHOLESALE_CONV = ".cach";

	private static final int MB = 1000 * 1000;
	private static final int CACHE_SIZE = 10;
	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
	
	private static ImageFileCache instance;
	
	public synchronized static ImageFileCache getInstance(){
		if(instance == null){
			instance = new ImageFileCache();
		}
		return instance;
	}

	private ImageFileCache() {
		// 清理文件缓存
		removeCache(getDirectory());
	}
	
	/** 从缓存中获取图片 **/
	public Bitmap getImage(final String url) {
		return getImage(url, 0, 0);
	}

	/** 从缓存中获取图片 **/
	public Bitmap getImage(final String url, final int width, final int height) {
		final String path = getDirectory() + "/" + convertUrlToFileDir(url) + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();  
			    options.inJustDecodeBounds = true;  
				Bitmap bmp = BitmapFactory.decodeFile(path, options);
				options.inSampleSize = ImageUtils.calculateInSampleSize(options, width, height);
				options.inJustDecodeBounds = false;  
				bmp = BitmapFactory.decodeFile(path, options);
				if (bmp == null) {
					file.delete();
				} else {
					updateFileTime(path);
					return bmp;
				}
			} catch(Exception e){
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/** 将图片存入文件缓存 **/
	public void saveBitmap(Bitmap bm, String url) {
		if (bm == null || url == null) {
			return;
		}
		// 判断sdcard上的空间
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			// SD空间不足
			return;
		}
		String filename = convertUrlToFileName(url);
		String dir = getDirectory() + "/" + convertUrlToFileDir(url);
		File dirFile = new File(dir);
		if (!dirFile.exists())
			dirFile.mkdirs();
		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			if(url.endsWith(".png") || url.endsWith(".PNG")){
				bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			}else{
				bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			}
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			Log.w("ImageFileCache", "FileNotFoundException");
		} catch (IOException e) {
			Log.w("ImageFileCache", "IOException: " + e.getMessage());
		}
	}
	
	/**
	 * 计算filecache的大小
	 * @return
	 */
	public static long getFileCacheSize(){
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return 0L;
		}
		String dirPath = new ImageFileCache().getDirectory();
		File dir = new File(dirPath);
		if (!dir.exists()) {
			return 0L;
		}
		return getFileSize(dir);
	}
	
	/**
	 * 获取文件大小
	 * @param file
	 * @return
	 */
	public static long getFileSize(File file){
		if(file == null || !file.exists()) return 0;
		if(file.isFile()){
			return file.length();
		}else{
			int size = 0;
			File[] files = file.listFiles();
			if(files != null){
				for(int i=0; i< files.length; i++){
					if(files[i].isFile()){
						size += files[i].length();
					}else{
						size += getFileSize(files[i]);
					}
				}
			}
			return size;
		}
	}
	
	/**
	 * 清除缓存
	 * @return
	 */
	public static boolean clearFileCache(){
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		String dirPath = new ImageFileCache().getDirectory();
		File dir = new File(dirPath);
		if (dir.exists()) {
			deleteFile(dir);
		}
		return true;
	}
	
	/**
	 * 删除文件（f.delete只能删除空的目录，如果目录不为空，则要递归删除文件）
	 * @param file
	 */
	public static void deleteFile(File file){
		if(file == null) return;
		if(file.isFile()){
			file.delete();
			return;
		}
		if(file.isDirectory()){
			File[] files = file.listFiles();
			if(files==null || files.length==0){
				file.delete(); //删除空文件夹(否则删除不掉)
				return;
			}
			for(int i=0; i<files.length; i++){
				deleteFile(files[i]);
			}
			file.delete(); //删除空文件夹(否则删除不掉)
		}
	}

	/**
	 * 计算存储目录下的文件大小，
	 * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
	 * 那么删除40%最近没有被使用的文件
	 */
	private boolean removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return true;
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}

		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {
			dirSize += files[i].length();
		}

		if (dirSize > CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			for (int i = 0; (i<files.length&&i<removeFactor); i++) {
				files[i].delete();
			}
		}

		if (freeSpaceOnSd() <= CACHE_SIZE) {
			return false;
		}

		return true;
	}

	/** 修改文件的最后修改时间 **/
	public void updateFileTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/** 计算sdcard上的剩余空间 **/
	@SuppressWarnings("deprecation")
	private int freeSpaceOnSd() {
		try {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			double sdFreeMB = ((double) stat.getAvailableBlocks() * 
					(double) stat.getBlockSize()) / MB;
			return (int) sdFreeMB;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/** 将url转成文件名 **/
	private String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1] + WHOLESALE_CONV;
	}
	
	/** 将url转成文件目录 **/
	private String convertUrlToFileDir(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 3] + strs[strs.length - 2];
	}

	/** 获得缓存目录 **/
	private String getDirectory() {
		String dir = getSDPath() + "/" + CACHDIR;
		return dir;
	}

	/** 取SD卡路径 **/
	private String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}

	/**
	 * 根据文件的最后修改时间进行排序
	 */
	private class FileLastModifSort implements Comparator<File> {
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

}