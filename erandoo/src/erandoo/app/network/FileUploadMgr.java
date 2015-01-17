package erandoo.app.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import android.text.TextUtils;
import android.util.Log;

public class FileUploadMgr {
	public static  int MAX_BUFF_SIZE = 32 * 1024;
	private static final String lineEnd = "\r\n";
	private static final String twoHyphens = "--";
	private static final String boundary = "*****";
	private static final String T_B_L = twoHyphens + boundary + lineEnd;
	private static final String T_B_T_L = twoHyphens + boundary + twoHyphens + lineEnd;
	private static final String MP_FD = "multipart/form-data;boundary=" + boundary;
	private static final String FLD_DATA = "data";
	
	public static NetworkResponse httpMultiPartPostImage(String httpUrl,ArrayList<UploadItem> uploadItems,String reqJson) {
		NetworkResponse netResp = new NetworkResponse();
		if (uploadItems.size() == 0 || TextUtils.isEmpty(reqJson)) {
			Log.i("httpMultiPartPostImage():", "incorrect paramater");
			return netResp;
		}

		URL url = null;
		DataOutputStream dos = null;
	//	FileInputStream fileInputStream = null;
		HttpURLConnection httpConn = null;
		// open a URL connection to the Servlet
		try {
			url = new URL(httpUrl);// set url
		} catch (MalformedURLException e) {
			Log.i("httpMultiPartPostImage():", "MalformedURLException");
			netResp.netRespCode = NetworkResponseCode.INVALID_URL;
			return netResp;
		}
		try {
			httpConn = (HttpURLConnection) url.openConnection();// Open a HTTP connection to the URL
			httpConn.setRequestProperty("Accept-Charset", HttpConstants.UTF8);
			httpConn.setRequestProperty("Content-Type", HttpConstants.urlencoded);
		} catch (IOException e) {
			Log.i("httpMultiPartPostImage():", "MalformedURLException");
			netResp.netRespCode = NetworkResponseCode.INVALID_URL;
			return netResp;
		}
		httpConn.setRequestProperty("Connection", "Keep-Alive");
		httpConn.setConnectTimeout(HttpConstants.HTTP_REQUEST_TIMEOUT);
		httpConn.setReadTimeout(HttpConstants.SOCKET_REQUEST_TIMEOUT);
		StringBuffer sb = null;
		int respCode = -1;
		InputStreamReader inputStream = null;
		BufferedReader in = null;
		try {
			httpConn.setDoInput(true);// Allow Inputs
			httpConn.setDoOutput(true);// Allow Outputs
			httpConn.setUseCaches(false);
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", MP_FD);
			
			httpConn.setRequestProperty(URLEncoder.encode(FLD_DATA, HttpConstants.UTF8), URLEncoder.encode(reqJson, HttpConstants.UTF8));

			dos = new DataOutputStream(httpConn.getOutputStream());
			
			for(UploadItem item : uploadItems)	{
				netResp = uploadFile(dos, item, netResp);
			}

			dos.writeBytes(T_B_T_L);

			dos.flush();
			dos.close();
			// ------------------ read the SERVER RESPONSE
			respCode = httpConn.getResponseCode();
			inputStream = new InputStreamReader(httpConn.getInputStream(),HttpConstants.UTF8);
			in = new BufferedReader(inputStream,HttpConstants.INPUT_STREAM_BUFFSIZE);
			sb = new StringBuffer(HttpConstants.INPUT_STREAM_BUFFSIZE);
			String str;
			try{
				while ((str = in.readLine()) != null) {
					sb.append(str);
				}
			}catch(OutOfMemoryError oom){
				Log.i("httpMultiPartPostImage():", "out of memory error");
			}
		} catch (IOException e) {
			Log.i("httpMultiPartPostImage():", "IOException " + e);
			netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
			if (e instanceof FileNotFoundException) {
				netResp.netRespCode = NetworkResponseCode.FILE_NOT_FOUND;
			}
			return netResp;
		}
		catch (OutOfMemoryError oom){
			Log.i("httpMultiPartPostImage():", "OOM Exception " + oom.toString());
			netResp.netRespCode = NetworkResponseCode.OUT_OF_MEMORY_ERROR;
			return netResp;
		}catch (Exception e) {
			netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
			Log.i("httpMultiPartPostImage():", "Network Exception " + e);
			return netResp;
		} finally {
			closeBufferedReader(in);
			closeInputStream(inputStream);
			closeHttpConnection(httpConn);
		}
		
		if (respCode != 200) {
			Log.i("httpMultiPartPostImage():", "network response code is "
						+ respCode);
			netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
			closeHttpConnection(httpConn);
			return netResp;
		} else {
			netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
			netResp.respStr = sb.toString();
			return netResp;
		}
	}
	
	private static NetworkResponse uploadFile(DataOutputStream dos, UploadItem item, NetworkResponse netResp) {
		FileInputStream fileInputStream =null;
		int bytesRead = 0, bytesAvailable = 0, bufferSize = 0;
		byte[] buffer = null;
		try {
			dos.writeBytes(T_B_L);
			// image file name
			dos.writeBytes("Content-Disposition: form-data; name=\"content\";filename=\""
					+ item.fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			fileInputStream = new FileInputStream(new File(item.filePath));

			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, MAX_BUFF_SIZE);
			buffer = new byte[bufferSize];
			while ((bytesRead = fileInputStream.read(buffer, 0, bufferSize)) > 0) {
				dos.write(buffer, 0, bytesRead);
			}
			// send multipart form data necessary after file data...
			dos.writeBytes(lineEnd);
		} catch (IOException ioe) {
			Log.i("httpMultiPartPostImage():", "IOException " + ioe);
			netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
			if (ioe instanceof FileNotFoundException) {
				netResp.netRespCode = NetworkResponseCode.FILE_NOT_FOUND;
			}
			return netResp;
		} catch (OutOfMemoryError oom){
			Log.i("httpMultiPartPostImage():", "OOM Exception " + oom.toString());
			netResp.netRespCode = NetworkResponseCode.OUT_OF_MEMORY_ERROR;
			return netResp;
		}finally{
			if(fileInputStream != null){
				closeFileInputStream(fileInputStream);
			}
		}
		return netResp;
	}

	/**
	 * This method is used to close all the http connections.
	 * 
	 * @param httpConn
	 * @return
	 */
	private static boolean closeHttpConnection(HttpURLConnection httpConn) {
		boolean rslt = false;
		if (httpConn != null) {
			httpConn.disconnect();
			httpConn = null;
			rslt = true;
		} else {
			rslt = false;
		}
		return rslt;
	}

	/**
	 * This method is used to close all the file Input streams.
	 * 
	 * @param fileInputStream
	 * @return
	 */
	private static boolean closeFileInputStream(FileInputStream fileInputStream) {
		boolean rslt = false;
		if (fileInputStream != null) {
			try {
				fileInputStream.close();
				rslt = true;
			} catch (IOException e) {
				rslt = false;
				e.printStackTrace();
			}
			fileInputStream = null;
		} else {
			rslt = false;
		}
		return rslt;

	}
	
	/**
	 * This method is used to close input streams.
	 * 
	 * @param inputStream
	 * @return
	 */
	private static boolean closeInputStream(InputStreamReader inputStream) {
		boolean rslt = false;
		if (inputStream != null) {
			try {
				inputStream.close();
				rslt = true;
			} catch (IOException e) {
				Log.i("closeInputStream() :", "exception while closing input stream : "
							+ e);
				e.printStackTrace();
				rslt = false;
			}
			inputStream = null;
		} else {
			rslt = false;
		}
		return rslt;
	}

	/**
	 * This method is used to close all the buffered readers.
	 * 
	 * @param bufReader
	 * @return
	 */
	private static boolean closeBufferedReader(BufferedReader bufReader) {
		boolean rslt = false;
		if (bufReader != null) {
			try {
				bufReader.close();
				rslt = true;
			} catch (IOException e) {
				e.printStackTrace();
				rslt = false;
			}
		} else {
			rslt = false;
		}
		return rslt;
	}

}
