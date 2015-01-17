package erandoo.app.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;

import android.text.TextUtils;

import erandoo.app.utilities.Constants;

public class NetworkMgrOlddevices {

	public static NetworkResponse httpGet(String url) {

		NetworkResponse netResp = null;
		if (TextUtils.isEmpty(url)) {
			return netResp;
		}

		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		netResp = new NetworkResponse();
		HttpGet httpGet = null;
		try {

			httpGet = new HttpGet(url);
			BasicHttpParams basicHttpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(basicHttpParams,
					HttpConstants.HTTP_REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(basicHttpParams,
					HttpConstants.SOCKET_REQUEST_TIMEOUT);
			HttpConnectionParams.setSocketBufferSize(basicHttpParams, 8192);

			DefaultHttpClient httpClient = new DefaultHttpClient(
					basicHttpParams);
			HttpResponse response = null;

			response = httpClient.execute(httpGet);

			StringBuffer resposebuf = new StringBuffer();
			StatusLine status = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			inputStreamReader = new InputStreamReader(entity.getContent(),
					HttpConstants.UTF8);
			bufferedReader = new BufferedReader(inputStreamReader,
					HttpConstants.INPUT_STREAM_BUFFSIZE);
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				resposebuf.append(str);
			}

			if (status.getStatusCode() != 200) {
				netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
				return netResp;
			} else {
				netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
				netResp.respStr = resposebuf.toString();

			}
		} catch (OutOfMemoryError e) {
			netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
			return netResp;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
			return netResp;
		} catch (IOException e) {
			e.printStackTrace();
			netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
			return netResp;
		} catch (Exception e) {
			httpGet = null;
			netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
			return netResp;
		} finally {
			closeHttpGet(httpGet);
			closeBufferedReader(bufferedReader);
			closeInputStream(inputStreamReader);
		}
		return netResp;
	}

	/**
	 * This method is used to close all the http get request.
	 * 
	 * @param httpPost
	 * @return
	 */
	private static boolean closeHttpGet(HttpGet httpGet) {
		boolean rslt = false;
		if (httpGet != null) {
			httpGet.abort();
			httpGet = null;
			rslt = true;
		} else {
			rslt = false;
		}
		return rslt;
	}

	// Note: UNUSED Method - refer httpPost()
	public static NetworkResponse httpPost(String httpUrl, String postData) {

		// System.out.println("httpPostOld()");

		NetworkResponse netResp = null;
		netResp = new NetworkResponse();
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(httpUrl);
			httpPost.setHeader("Accept-Charset", HttpConstants.UTF8);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			netResp.netRespCode = NetworkResponseCode.INVALID_URL;
			httpPost = null;
		}
		if (httpPost != null) {
			BasicHttpParams basicHttpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(basicHttpParams,
					HttpConstants.HTTP_REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(basicHttpParams,
					HttpConstants.SOCKET_REQUEST_TIMEOUT);
			HttpConnectionParams.setSocketBufferSize(basicHttpParams, 8192);
			// basicHttpParams.setParameter(
			// CoreProtocolPNames.PROTOCOL_VERSION,
			// HttpVersion.HTTP_1_1);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

			nameValuePairs.add(new BasicNameValuePair(Constants.FLD_DATA,
					postData));

			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						HttpConstants.UTF8));
				// CompressionUtil
				// .setCompressedEntity(appContext,
				// new UrlEncodedFormEntity(nameValuePairs).toString(),
				// httpPost);
			} catch (Exception e) {
				e.printStackTrace();
				httpPost = null;
				netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
				return netResp;
			}

			DefaultHttpClient httpClient = new DefaultHttpClient(
					basicHttpParams);
			HttpResponse response = null;
			try {
				// Add Request Interceptor
				httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
					public void process(HttpRequest request, HttpContext context) {

						request.addHeader("Accept-Charset", HttpConstants.UTF8);
						// Add header to accept gzip content
						if (!request
								.containsHeader(HttpConstants.HEADER_ACCEPT_ENCODING)) {
							request.addHeader(
									HttpConstants.HEADER_ACCEPT_ENCODING,
									HttpConstants.ENCODING_GZIP);
						}
					}
				});

				// Add Response Interceptor
				httpClient
						.addResponseInterceptor(new HttpResponseInterceptor() {
							public void process(HttpResponse response,
									HttpContext context) {
								// Inflate any responses compressed with
								// gzip
								final HttpEntity entity = response.getEntity();
								final Header encoding = entity
										.getContentEncoding();
								if (encoding != null) {
									for (HeaderElement element : encoding
											.getElements()) {
										if (element.getName().equalsIgnoreCase(
												HttpConstants.ENCODING_GZIP)) {
											// System.out
											// .println("httpPostOld() unZip");
											response.setEntity(new InflatingEntity(
													response.getEntity()));
											break;
										}
									}
								}
							}
						});
				response = httpClient.execute(httpPost);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				closeHttpPost(httpPost);
				netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
				return netResp;
			} catch (ConnectTimeoutException e) {
				netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
				closeHttpPost(httpPost);
				return netResp;
			} catch (SocketTimeoutException e) {
				netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
				closeHttpPost(httpPost);
				return netResp;
			} catch (Exception e) {
				netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
				closeHttpPost(httpPost);
				return netResp;
			}

			StringBuffer resposebuf = new StringBuffer(
					HttpConstants.INPUT_STREAM_BUFFSIZE);
			StatusLine status = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStreamReader inputStreamReader = null;
				BufferedReader bufferedReader = null;
				try {
					inputStreamReader = new InputStreamReader(
							entity.getContent(), HttpConstants.UTF8);
					bufferedReader = new BufferedReader(inputStreamReader,
							HttpConstants.INPUT_STREAM_BUFFSIZE);
					String str;
					while ((str = bufferedReader.readLine()) != null) {
						resposebuf.append(str);
					}
				} catch (OutOfMemoryError e) {
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				} catch (IllegalStateException e) {
					e.printStackTrace();
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				} catch (IOException e) {
					e.printStackTrace();
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				} finally {
					closeHttpPost(httpPost);
					closeBufferedReader(bufferedReader);
					closeInputStream(inputStreamReader);
				}
			}
			if (status.getStatusCode() != 200) {
				netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
				return netResp;
			} else {
				netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
				netResp.respStr = resposebuf.toString();

			}
		} else {

		}
		// Common.checkObject("httpPost: EXIT : ");
		return netResp;
	}

	private static boolean closeHttpPost(HttpPost httpPost) {
		boolean rslt = false;
		if (httpPost != null) {
			httpPost.abort();
			httpPost = null;
			rslt = true;
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
