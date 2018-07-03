package com.app.jianghu.common.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

/**
 * 图片缓存-从网络获取图片
 * @author songqy
 * @version 1.0.0
 * @time 2014-10-08
 *
 */
public class ImageGetFromHttp {
	
	/**
	 * 获取网络图片
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(Context context ,String url) {
		Bitmap bitmap = null;
		BitmapDisplayConfig defaultDisplayConfig = new BitmapDisplayConfig();
		defaultDisplayConfig.setAnimation(null);
		defaultDisplayConfig
				.setAnimationType(BitmapDisplayConfig.AnimationType.fadeIn);
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int defaultWidth = (int) Math.floor(displayMetrics.widthPixels);
		int defaultHeight = (int) Math.floor(displayMetrics.heightPixels);
		defaultDisplayConfig.setBitmapHeight(defaultHeight);
		defaultDisplayConfig.setBitmapWidth(defaultWidth);

		if(bitmap == null){
			byte[] data = getFromHttp(url);
			if(data != null && data.length > 0){
				try {
					bitmap = decodeSampledBitmapFromByteArray(data,0,data.length,defaultDisplayConfig.getBitmapWidth(),defaultDisplayConfig.getBitmapHeight());
					//bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				} catch (Exception e) {
					e.printStackTrace();
				} catch(OutOfMemoryError e){
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}
	
	public static Bitmap decodeSampledBitmapFromByteArray(byte[] data,  int offset, int length, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }
	
	private static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
            final float totalPixels = width * height;

            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
	
	private static final int IO_BUFFER_SIZE = 8 * 1024; // 8k

	private static byte[] getFromHttp(String urlString) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		FlushedInputStream in = null;

		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new FlushedInputStream(new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			while ((b = in.read()) != -1) {
				baos.write(b);
			}
			return baos.toByteArray();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}
		return null;
	}
	
	public static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int by_te = read();
					if (by_te < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
	
	
	/*private static final String LOG_TAG = "ImageGetFromHttp";

	public static Bitmap downloadBitmap(String url) {
		final HttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(LOG_TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					FilterInputStream fit = new FlushedInputStream(inputStream);
					return BitmapFactory.decodeStream(fit);
				} finally {
					if (inputStream != null) {
						inputStream.close();
						inputStream = null;
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
		} catch (IllegalStateException e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Incorrect URL: " + url);
		} catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
		} finally {
			client.getConnectionManager().shutdown();
		}
		return null;
	}

	
	 * Android对于InputStream流有个小bug在慢速网络的情况下可能产生中断，
	 * 可以考虑重写FilterInputStream处理skip方法来解决这个bug。
	 * BitmapFactory类的decodeStream方法在网络超时或较慢的时候无法获取完整的数据，这里我
	 * 们通过继承FilterInputStream类的skip方法来强制实现flush流中的数据
	 * ，主要原理就是检查是否到文件末端，告诉http类是否继续。
	 
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}*/
}