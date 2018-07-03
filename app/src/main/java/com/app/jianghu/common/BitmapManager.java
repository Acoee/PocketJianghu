package com.app.jianghu.common;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.app.jianghu.app.AppConfig;
import com.app.jianghu.common.cache.ImageFileCache;
import com.app.jianghu.common.cache.ImageGetFromHttp;
import com.app.jianghu.common.cache.ImageMemoryCache;
import com.app.jianghu.common.utils.SystemUtils;
import com.app.jianghu.widget.ZoomImageView;

import cn.com.acoe.jianghu.app.R;

/**
 * 异步线程加载图片工具类
 * 使用说明：
 * BitmapManager bmpManager;
 * bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.loading));
 * bmpManager.loadBitmap(imageURL, imageView);
 * @author songqy
 * @version 1.0.0
 * @created 2015-04-28
 */
@SuppressWarnings("deprecation")
public class BitmapManager {  
    private static ExecutorService pool;  //执行向服务器端获取图片的线程池
    private static Map<View, String> imageViews;  //保存之前显示过的ImageView和对应的url（弱引用集合）
    private Bitmap defaultBmp;  //加载失败后默认显示的图片
    
    private ImageMemoryCache memoryCache; //内存缓存
	private ImageFileCache fileCache; //文件缓存
    
    static {  
        pool = Executors.newFixedThreadPool(5);  //固定线程池大小为5
        imageViews = Collections.synchronizedMap(new WeakHashMap<View, String>());
    }  
    
    public BitmapManager(){}
    Context context ;
    
    /**
     * 构造函数
     * @param context 当前上下文对象
     * @param def 默认显示的图片
     */
    public BitmapManager(Context context , Bitmap def) {
    	this.defaultBmp = def;
    	this.context = context;
    	this.memoryCache = ImageMemoryCache.getInstance(context);
    	this.fileCache = ImageFileCache.getInstance();
    }
    
    /**
     * 构造函数
     * @param context 当前上下文对象
     */
    public BitmapManager(Context context){
    	//this.defaultBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.load_default_icon);
    	this.defaultBmp = ImageUtils.readBitmapByResId(context, R.mipmap.load_default_icon);
    	this.context = context;
    	this.memoryCache = ImageMemoryCache.getInstance(context);
    	this.fileCache = ImageFileCache.getInstance();
    }
    
    /**
     * 保存图片到缓存
     * @param url
     * @param bitmap
     */
    public void saveBitmapToCache(String url, Bitmap bitmap){
    	fileCache.saveBitmap(bitmap, url);
		memoryCache.addBitmapToCache(url, bitmap);
    }
    
    /**
     * 设置默认图片
     * @param bmp
     */
    public void setDefaultBmp(Bitmap bmp) {  
    	this.defaultBmp = bmp;  
    }   
    
    /**
     * 设置默认图片
     * @param drawable
     */
    public void setDefaultBmp(int drawable) {  
    	this.defaultBmp = BitmapFactory.decodeResource(context.getResources(), drawable);
    } 
    
    /**
     * 从缓存中获取图片
     * @param url
     * @return
     */
    public Bitmap getBitmapFromCache(String url) {  
    	if(SystemUtils.Companion.isEmpty(url)){
			return null;
		}
        //从内存缓存中获取
        Bitmap bitmap = this.memoryCache.getBitmapFromCache(url);  
        if (bitmap != null) {  
			return bitmap;
        } else {  
        	//加载SD卡中的图片缓存
        	bitmap = this.fileCache.getImage(url);
    		if(bitmap != null){
        		// 添加到内存缓存
				memoryCache.addBitmapToCache(url, bitmap);
				return bitmap;
        	}else{
        		return null;
        	}
        }  
    }  
  
    /**
     * 加载图片-可指定显示图片的高宽
     * @param url 图片链接
     * @param imageView 要显示的view
     */
    public void loadBitmap(String url, ImageView imageView) {  
    	if(SystemUtils.Companion.isEmpty(url)){
			imageView.setImageBitmap(defaultBmp);
			return;
		}
        imageViews.put(imageView, url);  
        //从内存缓存中获取
        Bitmap bitmap = this.memoryCache.getBitmapFromCache(url);  
        if (bitmap != null) {  
			//显示缓存图片
            imageView.setImageBitmap(bitmap);  
            imageViews.remove(imageView);
        } else {  
        	//加载SD卡中的图片缓存
        	bitmap = this.fileCache.getImage(url);
    		if(bitmap != null){
				//显示SD卡中的图片缓存
        		imageView.setImageBitmap(bitmap);
        		// 添加到内存缓存
				memoryCache.addBitmapToCache(url, bitmap);
				imageViews.remove(imageView);
        	}else{
				//线程加载网络图片
        		imageView.setImageBitmap(this.defaultBmp);
        		queueJob(url, imageView);
        	}
        }  
    }  
    
    /**
     * 加载图片-可指定显示图片的高宽
     * @param url 图片链接
     * @param zoomImage 要显示的view
     */
    public void loadBitmap(String url, ZoomImageView zoomImage, ProgressBar progress) {
    	if(progress!=null) progress.setVisibility(View.VISIBLE); //显示刷新按钮
    	if(SystemUtils.Companion.isEmpty(url)){
    		zoomImage.setImageBitmap(defaultBmp);
    		if(progress!=null) progress.setVisibility(View.GONE);
			return;
		}
        imageViews.put(zoomImage, url);  
        //从内存缓存中获取
        Bitmap bitmap = this.memoryCache.getBitmapFromCache(url);  
        if (bitmap != null) {  
			//显示缓存图片
        	zoomImage.setImageBitmap(bitmap);
        	if(progress!=null) progress.setVisibility(View.GONE);
        	if(progress!=null) progress.setVisibility(View.GONE);
        	imageViews.remove(zoomImage);
        } else {  
        	//加载SD卡中的图片缓存
        	bitmap = this.fileCache.getImage(url);
    		if(bitmap != null){
				//显示SD卡中的图片缓存
    			zoomImage.setImageBitmap(bitmap);
    			if(progress!=null) progress.setVisibility(View.GONE);
        		// 添加到内存缓存
				memoryCache.addBitmapToCache(url, bitmap);
				imageViews.remove(zoomImage);
        	}else{
				//线程加载网络图片
        		if(progress == null)
        			zoomImage.setImageBitmap(defaultBmp);
        		queueJob(url, zoomImage, progress);
        	}
        }  
    } 
    
    /**
     * 加载背景图片-可指定显示图片的高宽
     * @param url 图片链接
     * @param imageView 要显示的view
     */
    public void loadBackgroundBitmap(String url, ImageView imageView, int width, int height) {  
    	if(SystemUtils.Companion.isEmpty(url)){
			imageView.setBackgroundDrawable(new BitmapDrawable(defaultBmp));
			return;
		}
        imageViews.put(imageView, url);  
        //从内存缓存中获取
        Bitmap bitmap = this.memoryCache.getBitmapFromCache(url);  
        if (bitmap != null) {  
			//显示缓存图片
            imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));  
            imageViews.remove(imageView);
        } else {  
        	//加载SD卡中的图片缓存
        	bitmap = this.fileCache.getImage(url, width, height);
    		if(bitmap != null){
				//显示SD卡中的图片缓存
        		imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        		// 添加到内存缓存
				//memoryCache.addBitmapToCache(url, bitmap);
				imageViews.remove(imageView);
        	}else{
				//线程加载网络图片
        		imageView.setBackgroundDrawable(new BitmapDrawable(this.defaultBmp));
        		queueJobBackground(url, imageView);
        	}
        }  
    }
    
    /**
     * 加载背景图片-可指定显示图片的高宽
     * @param url 图片链接
     * @param imageView 要显示的view
     */
    public void loadBackgroundBitmap(String url, ImageView imageView) {  
    	if(SystemUtils.Companion.isEmpty(url)){
			imageView.setBackgroundDrawable(new BitmapDrawable(defaultBmp));
			return;
		}
        imageViews.put(imageView, url);  
        //从内存缓存中获取
        Bitmap bitmap = this.memoryCache.getBitmapFromCache(url);  
        if (bitmap != null) {  
			//显示缓存图片
            imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));  
            imageViews.remove(imageView);
        } else {  
        	//加载SD卡中的图片缓存
        	bitmap = this.fileCache.getImage(url);
    		if(bitmap != null){
				//显示SD卡中的图片缓存
        		imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        		// 添加到内存缓存
				memoryCache.addBitmapToCache(url, bitmap);
				imageViews.remove(imageView);
        	}else{
				//线程加载网络图片
        		imageView.setBackgroundDrawable(new BitmapDrawable(this.defaultBmp));
        		queueJobBackground(url, imageView);
        	}
        }  
    }
    
    /**
     * 加载背景图片-可指定显示图片的高宽
     * @param url 图片链接
     * @param imageView 要显示的view
     */
    public void loadBackgroundBitmap(String url, View imageView, boolean isScrolling) {  
    	if(SystemUtils.Companion.isEmpty(url)){
			imageView.setBackgroundDrawable(new BitmapDrawable(defaultBmp));
			return;
		}
        imageViews.put(imageView, url);  
        //从内存缓存中获取
        Bitmap bitmap = this.memoryCache.getBitmapFromCache(url);  
        if (bitmap != null) {  
			//显示缓存图片
            imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));  
            imageViews.remove(imageView);
        } else {  
        	//加载SD卡中的图片缓存
        	bitmap = this.fileCache.getImage(url);
    		if(bitmap != null){
				//显示SD卡中的图片缓存
        		imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        		// 添加到内存缓存
				memoryCache.addBitmapToCache(url, bitmap);
				imageViews.remove(imageView);
        	}else{
        		if(isScrolling){
        			imageView.setBackgroundDrawable(new BitmapDrawable(defaultBmp));
        			return;
        		}
				//线程加载网络图片
        		imageView.setBackgroundDrawable(new BitmapDrawable(this.defaultBmp));
        		queueJobBackground(url, imageView);
        	}
        }  
    }
    
    /**
     * 加载背景图片（无缓存）
     * @param url 图片链接
     * @param imageView 要显示的view
     */
	public void loadBackgroundBitmapNoCache(String url, ImageView imageView) {  
		if(SystemUtils.Companion.isEmpty(url)){
			imageView.setBackgroundDrawable(new BitmapDrawable(defaultBmp));
			return;
		}
    	imageViews.put(imageView, url); 
		//线程加载网络图片
		imageView.setBackgroundDrawable(new BitmapDrawable(defaultBmp));
		queueJobBackground(url, imageView);
    }
  
    /**
     * 从网络中加载图片
     * @param url 图片链接
     * @param zoomImage 要显示的view
     */
    public void queueJob(final String url, final ZoomImageView zoomImage, final ProgressBar progress) {  
        /* Create handler in UI thread. */  
        final Handler handler = new Handler() {  
            public void handleMessage(Message msg) {  
            	Log.i("queueJob", "zoom start");
            	if(progress!=null) progress.setVisibility(View.GONE);
                String tag = imageViews.get(zoomImage);
                if (tag != null && tag.equals(url)) {
                	imageViews.remove(zoomImage);
                    if (msg.obj != null) {  
                    	Bitmap result = (Bitmap) msg.obj;
                    	zoomImage.setImageBitmap(result);
                    } else {
                    	zoomImage.setImageBitmap(defaultBmp);
                    }
                }  
            }  
        };  
  
        pool.execute(new Runnable() {   
            public void run() {  
            	Bitmap result= ImageGetFromHttp.getNetBitmap(context, url);
                if(result != null){
                	//将图片缓存到文件和内存
                	fileCache.saveBitmap(result, url);
					memoryCache.addBitmapToCache(url, result);
                }
                Message message = Message.obtain();  
                message.obj = result;
                handler.sendMessage(message);  
            }  
        });  
    } 
    
    /**
     * 从网络中加载图片
     * @param url 图片链接
     * @param imageView 要显示的view
     */
    public void queueJob(final String url, final ImageView imageView) {  
        /* Create handler in UI thread. */  
        final Handler handler = new Handler() {  
            public void handleMessage(Message msg) {  
                String tag = imageViews.get(imageView);  
                if (tag != null && tag.equals(url)) { 
                	imageViews.remove(imageView);
                    if (msg.obj != null) {  
                    	Bitmap result = (Bitmap) msg.obj;
                        imageView.setImageBitmap(result);  
                    } else {
                    	imageView.setImageBitmap(defaultBmp);
                    }
                }  
            }  
        };  
  
        pool.execute(new Runnable() {   
            public void run() {  
            	Bitmap result= ImageGetFromHttp.getNetBitmap(context, url);
                if(result != null){
                	//将图片缓存到文件和内存
                	fileCache.saveBitmap(result, url);
					memoryCache.addBitmapToCache(url, result);
                }
                Message message = Message.obtain();  
                message.obj = result;
                handler.sendMessage(message);  
            }  
        });  
    } 
    
    /**
     * 从网络中加载背景图片
     * @param url 图片链接
     * @param imageView 要显示的view
     */
    public void queueJobBackground(final String url, final View imageView) {  
        /* Create handler in UI thread. */  
        final Handler handler = new Handler() {  
            public void handleMessage(Message msg) {  
                String tag = imageViews.get(imageView);  
                if (tag != null && tag.equals(url)) {  
                	imageViews.remove(imageView);
                    if (msg.obj != null) {  
                    	Bitmap result = (Bitmap) msg.obj;
                        imageView.setBackgroundDrawable(new BitmapDrawable(result));  
                    } else {
                    	//imageView.setBackgroundResource(R.drawable.load_default_icon);
                    	imageView.setBackgroundDrawable(new BitmapDrawable(defaultBmp));  
                    }
                }  
            }  
        };  
  
        pool.execute(new Runnable() {   
            public void run() {  
                Bitmap result= ImageGetFromHttp.getNetBitmap(context, url);
                if(result != null){
                	//将图片缓存到文件和内存
                	fileCache.saveBitmap(result, url);
					memoryCache.addBitmapToCache(url, result);
                }
                Message message = Message.obtain();  
                message.obj = result;
                handler.sendMessage(message);  
            }  
        });  
    }
    
}