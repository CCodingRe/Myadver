package com.google.android.ore.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.ore.OreApp;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.TANetWorkUtil;
import com.google.android.ore.util.F;

public class HttpDownload {

	public static final int HTTP_OK = 0;

	public static final int HTTP_NET_ERROR = -100;
	public static final int HTTP_FILE_EXISTS = -101;
	public static final int HTTP_CHECK_FILE_FAIL = -102;
	public static final int HTTP_FILE_OPEN_FAIL = -103;
	public static final int HTTP_CANCEL = -104;
	public static final int HTTP_LEFTTOREADSIZE_LESS_ZERO = -105;
	public static final int HTTP_EXCEPTION = -106;

	public static final int CONNECT_TIME_OUT = 60 * 1000;
	public static final int READ_TIME_OUT = 60 * 1000;

	public interface HttpDownloadFileListener {

		void onDLComplete(Integer result);

		void onProgressUpdate(Long progress);
	}

	private TaskProcess mTask;

	private String TAG = HttpDownload.class.getSimpleName();

	public void cancel() {
		if (null != mTask) {
			mTask.cancel();
			mTask = null;
		}
	}

	public void stop() {
		if (null != mTask) {
			mTask.stop();
			mTask = null;
		}
	}

	public void start(HttpDownloadFileListener listener, String url,
			String loacalFileName) {
		if (TextUtils.isEmpty(url)) {
			L.d(TAG, "TextUtils.isEmpty(url)");
			return;
		}
		if (DownloadingMgr.isDownloading(url)) {
			L.d(TAG, "正在下载:" + url);
			return;
		}
		cancel();
		mTask = new TaskProcess();
		mTask.listener = listener;
		mTask.execute(url, loacalFileName);
	}

	public class TaskProcess extends AsyncTask<String, Long, Integer> {
		HttpDownloadFileListener listener;
		boolean stop = false;
		private String mDownloadUrl;

		public void cancel() {
			listener = null;
		}

		public void stop() {
			stop = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(0);
			if (listener != null) {
				listener.onDLComplete(result);
			}
			mTask = null;
			DownloadingMgr.removeDownloading(mDownloadUrl);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			super.onProgressUpdate(values);
			Long progress = values[0];
			if (listener != null) {
				listener.onProgressUpdate(progress);
			}
		}

		private boolean isStop() {

			if (stop || null == listener) {
				return true;
			}
			return false;
		}

		@Override
		protected Integer doInBackground(String... params) {
			mDownloadUrl = params[0];
			String loacalFileName = params[1];
			int result = doBreakpointContinuinglyDownloadFile(mDownloadUrl,
					loacalFileName);
			return result;
		}

		private int doBreakpointContinuinglyDownloadFile(String url,
				String targetDir) {
			long nTotalSize = 0; // 需要下载的文件的总的大小
			long nLeftToReadSize = 0; // 本次需要从网络中读取的报文数目
			long nBeginRange = 0; // 已经读取的字节数
			InputStream in = null;
			OutputStream out = null;
			GZIPInputStream gzipIs = null;
			try {
				File file = F.makeFile(targetDir);
				if (file == null || file.exists() == false) {
					// 文件创建不成功
					return HTTP_FILE_OPEN_FAIL;
				}
				if (file.length() > 0) {
					nBeginRange = (int) file.length();
				}
				out = new FileOutputStream(file, true);
				HttpURLConnection httpConnection = createHttpConnection(url);
				String strByteRange = "bytes=" + nBeginRange + "-";
				httpConnection.setRequestProperty("Range", strByteRange);
				nLeftToReadSize = httpConnection.getContentLength();
				int nCode = httpConnection.getResponseCode();
				// 无效的状态码
				if (nCode == -1) {
					return HTTP_NET_ERROR;
				} else if (nCode == 416) {
					// 请求的范围越界，意味着本地的文件已经全部下载完毕
					return HTTP_OK;
				} else if (nCode >= 400 && nCode <= 500) {
					return HTTP_NET_ERROR;
				} else if (nCode >= 500 && nCode <= 600) {
					return HTTP_NET_ERROR;
				} else if (nCode == 206) {
					// 断点续传
					nTotalSize = nLeftToReadSize + nBeginRange;
				} else if (nCode == 200) {
					nTotalSize = nLeftToReadSize;
				} else {
					return HTTP_NET_ERROR;
				}
				if (nLeftToReadSize < 0) {
					return HTTP_LEFTTOREADSIZE_LESS_ZERO;
				}
				in = httpConnection.getInputStream();
				String strEncodeing = httpConnection
						.getHeaderField("Content-Encoding");
				if ((strEncodeing != null)
						&& strEncodeing.equalsIgnoreCase("gzip")) {
					gzipIs = new GZIPInputStream(new BufferedInputStream(in));
					byte[] buffer = new byte[10240];
					long size = 0;
					while (isStop() == false && true) {
						int nRead = gzipIs.read(buffer, 0, buffer.length);
						if (nRead == -1) {
							break;
						}
						out.write(buffer, 0, nRead);
						size += nRead;
						long progress = 100 * (size + nBeginRange) / nTotalSize;
						publishProgress(progress);
					}
				} else {
					byte[] buffer = new byte[10240];
					long size = 0;
					while (isStop() == false && true) {
						int nRead = in.read(buffer, 0, buffer.length);
						if (nRead == -1) {
							break;
						}
						out.write(buffer, 0, nRead);
						size += nRead;
						long progress = 100 * (size + nBeginRange) / nTotalSize;
						publishProgress(progress);
					}
				}
				if (isStop()) {
					return HTTP_CANCEL;
				}
				return HTTP_OK;
			} catch (IOException e) {
				L.d(TAG, "HttpDownLoadFile HTTP_EXCEPTION : " + e.getMessage());
				return HTTP_EXCEPTION;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
					if (out != null) {
						out.close();
					}
					if (gzipIs != null) {
						gzipIs.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
				out = null;
				gzipIs = null;
			}
		}

		private HttpURLConnection createHttpConnection(String strResultUrl) {

			HttpURLConnection httpConnection = null;
			try {
				URL requestUrl = new URL(strResultUrl);

				if (TANetWorkUtil.isWifiConnected(OreApp.get())) {
					httpConnection = (HttpURLConnection) requestUrl
							.openConnection();
				} else {
					String strProxyHost = android.net.Proxy.getDefaultHost();
					if (strProxyHost != null) {
						Proxy proxy = new Proxy(Proxy.Type.HTTP,
								new InetSocketAddress(
										android.net.Proxy.getDefaultHost(),
										android.net.Proxy.getDefaultPort()));
						httpConnection = (HttpURLConnection) requestUrl
								.openConnection(proxy);
					} else {
						httpConnection = (HttpURLConnection) requestUrl
								.openConnection();
					}
				}

				if (httpConnection != null) {
					httpConnection.setConnectTimeout(CONNECT_TIME_OUT);
					httpConnection.setReadTimeout(READ_TIME_OUT);
					httpConnection.setRequestProperty("User-Agent", OreApp.get()
							.getPackageName() + "_android");
					httpConnection.setRequestProperty("Connection",
							"Keep-Alive");
					httpConnection.setRequestProperty("Cache-control",
							"no-cache");
					// httpConnection.setRequestProperty("Accept-Encoding",
					// "gzip");
					httpConnection.setRequestMethod("GET");
					httpConnection.setInstanceFollowRedirects(true);
					// httpConnection.setDoOutput(true);
					// httpConnection.setDoInput(true);
				}

			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}

			return httpConnection;
		}
	}

}
