package erandoo.app.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.MessageDetail;
import erandoo.app.database.Project;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class NetworkMgr {
	private static final String SPACE = " ";

	private static boolean isFROYODevice = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1);

	public NetworkResponse httpGet(String url) {
		return NetworkMgrOlddevices.httpGet(url);
	}

	public static NetworkResponse httpPost(String httpUrl, String postData) {
		if (isFROYODevice) {
			return NetworkMgrOlddevices.httpPost(httpUrl, postData);
		}
		//Log.v("postData->", postData);
		NetworkResponse netResp = new NetworkResponse();

		if (TextUtils.isEmpty(postData)) {
		} else {
			netResp = new NetworkResponse();
			InputStream is = null;
			HttpURLConnection conn = null;
			try {
				String data = URLEncoder.encode(Constants.FLD_DATA,
						HttpConstants.UTF8)
						+ "="
						+ URLEncoder.encode(postData, HttpConstants.UTF8);
				// constants
				URL url = new URL(httpUrl); 

				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty(HttpConstants.ACCEPT_CHARSET,
						HttpConstants.UTF8);
				conn.setRequestProperty(HttpConstants.CONNECTION,
						HttpConstants.KEEP_ALIVE);
				conn.setConnectTimeout(HttpConstants.HTTP_REQUEST_TIMEOUT);
				conn.setReadTimeout(HttpConstants.HTTP_READ_TIMEOUT);
				conn.setRequestProperty(HttpConstants.HEADER_ACCEPT_ENCODING,
						HttpConstants.ENCODING_GZIP);
				conn.setFixedLengthStreamingMode(data.getBytes().length);
				// make some HTTP header nicety
				// POST the data
				OutputStreamWriter osw = new OutputStreamWriter(
						conn.getOutputStream(), HttpConstants.UTF8);
				osw.write(data);
				osw.close();
				osw = null;
				// do somehting with response
				is = conn.getInputStream();
				// Log.v("HTTP_STATUS_CODE->","="+conn.getResponseCode());
				String response = readIt(is, conn.getContentEncoding());
				// System.out.println("HttpPost Response code "
				// + conn.getResponseCode());
				// System.out.println("HttpPost Response " + response);
				if (conn.getResponseCode() != 200) {
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				} else {
					netResp.respStr = response;
					netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				// / eLogger.fatal("httpPostNew : ClientProtocolException" + e);
				netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
				return netResp;
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
				netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
				// eLogger.fatal("httpPostNew : ConnectTimeoutException" + e);
				return netResp;
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
				// eLogger.fatal("httpPostNew : SocketTimeoutException : " + e);
				return netResp;
			} catch (Exception e) {
				e.printStackTrace();
				netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
				// eLogger.fatal("httpPostNew : Network Exception" + e);
				return netResp;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
						// eLogger.fatal("httpPost : SocketTimeoutException : "
						// + e);
					}
				}
				if (conn != null) {
					conn.disconnect();
				}
			}
		}
		return netResp;
	}

	private static String readIt(InputStream is, String encoding)
			throws IOException, UnsupportedEncodingException {

		StringBuilder sb = new StringBuilder(
				HttpConstants.INPUT_STREAM_BUFFSIZE);
		BufferedReader r = null;
		if (HttpConstants.ENCODING_GZIP.equals(encoding)) {
			// System.out.println("httpPostNew() unZip");
			r = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(is), HttpConstants.UTF8),
					HttpConstants.INPUT_STREAM_BUFFSIZE);
		} else {
			r = new BufferedReader(
					new InputStreamReader(is, HttpConstants.UTF8),
					HttpConstants.INPUT_STREAM_BUFFSIZE);
		}
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		r.close();
		return sb.toString();
	}

	public static NetworkResponse httpGet(String sourcefileURI, File outputFile) {

		NetworkResponse netResp = new NetworkResponse();
		// System.out.println("downloadFile: sourcefileURI: " + sourcefileURI
		// + "  outputFile : " + outputFile);

		// file should not be download if exists.
		if (outputFile.exists()) {
			netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
			return netResp;
		}
		// System.out.println("downloadFile: sourcefileURI: " + sourcefileURI
		// + "  outputFilePath : " + outputFilePath);

		if (TextUtils.isEmpty(sourcefileURI)) {
			netResp.netRespCode = NetworkResponseCode.NET_RESP_FAILURE;
			return netResp;
		}

		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			// constants
			sourcefileURI = URLDecoder
					.decode(sourcefileURI, HttpConstants.UTF8);
			// System.out.println("sourcefileURI received " + sourcefileURI);
			if (sourcefileURI.contains(SPACE)) {
				sourcefileURI = sourcefileURI.replace(SPACE, "%20");
			}
			// System.out.println("sourcefileURI " + sourcefileURI);
			URL url = new URL(sourcefileURI);

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(HttpConstants.ACCEPT_CHARSET, "UTF-8");
			conn.setRequestProperty(HttpConstants.CONNECTION,HttpConstants.KEEP_ALIVE);
			conn.setConnectTimeout(HttpConstants.HTTP_REQUEST_TIMEOUT);
			conn.setReadTimeout(HttpConstants.HTTP_READ_TIMEOUT);

			// make some HTTP header nicety
			// conn.setRequestProperty("Content-Type",
			// "text/plain; charset=utf-8");
			conn.setRequestProperty(HttpConstants.HEADER_ACCEPT_ENCODING,
					HttpConstants.ENCODING_GZIP);
			is = conn.getInputStream();
			int statusCode = conn.getResponseCode();
			if (statusCode == HttpStatus.SC_OK) {
				InputStream inputStream = null;
				try {
					inputStream = conn.getInputStream();
				} catch (IllegalStateException e) {
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				} catch (FileNotFoundException e) {
					// No need to treat missing file on server side as an error
					netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
					return netResp;
				} catch (Exception e) {
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(outputFile);
				} catch (FileNotFoundException e) {
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				}
				int bytesRead = 0;
				try {
					String encoding = conn.getContentEncoding();
					// NOTE: Need to check how to get encoding info from
					// Response.
					BufferedInputStream bis;

					if (HttpConstants.ENCODING_GZIP.equals(encoding)) {
						// System.out.println("unzip file");
						bis = new BufferedInputStream(new GZIPInputStream(
								inputStream),
								HttpConstants.INPUT_STREAM_BUFFSIZE);
					} else {
						// System.out.println("no unzip file");
						bis = new BufferedInputStream(inputStream,
								HttpConstants.INPUT_STREAM_BUFFSIZE);
					}
					byte[] buffer = new byte[HttpConstants.INPUT_STREAM_BUFFSIZE];
					while ((bytesRead = bis.read(buffer)) > 0) {
						fos.write(buffer, 0, bytesRead);
					}
					fos.close();
					// Fix for zero byte file
					if (outputFile.length() == 0) {
						outputFile.delete();
						// downloading 0 byte file is not an error. System
						// should not try it again.
						return netResp;
					}
				} catch (IOException e) {
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					// eLogger.fatal("downloadFile : IOException" + e);
					return netResp;
				}
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				netResp.netRespCode = NetworkResponseCode.FILE_NOT_FOUND;
				return netResp;
			}
		} catch (ClientProtocolException e) {
			// eLogger.fatal("httpPost : ClientProtocolException" + e);
			netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
			return netResp;
		} catch (ConnectTimeoutException e) {
			netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
			// eLogger.fatal("httpPost : ConnectTimeoutException" + e);
			return netResp;
		} catch (SocketTimeoutException e) {
			netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
			// eLogger.fatal("httpPost : SocketTimeoutException : " + e);
			return netResp;
		} catch (FileNotFoundException e) {
			// No need to treat missing file on server side as an error
			netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
			// eLogger.fatal("downloadFile :IOException " + e);
			return netResp;
		} catch (Exception e) {
			netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
			// eLogger.fatal("httpPost : Network Exception" + e);
			return netResp;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					// eLogger.fatal("httpPost : SocketTimeoutException : " +
					// e);
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
		return netResp;
	}

	// ---------------------------Specific Functions-------------------------

	public static NetworkResponse postPendingTasks(ArrayList<Project> projects) {
		NetworkResponse response = null;
		try {
			if (Util.isDeviceOnline(AppGlobals.appContext)) {
				DatabaseMgr dbMgr = DatabaseMgr.getInstance(AppGlobals.appContext);
				Long trno = null;
				Cmd cmd = null;
				for (Project project : projects) {
					String recId = String.valueOf(project._Id);
					if (trno != null){
						Log.v("postPendingTasks-> trno=", String.valueOf(trno));
						project.trno = trno;
					}
					if (project.task_kind.equals(Constants.INSTANT)) {
						project.workLocation = dbMgr.getTaskWLocation(recId);
						cmd = CmdFactory.createPostInstantCmd(project);
						cmd.addData(Project.FLD_INVITED_DOERS, dbMgr.getTaskDoers(recId));
						
					} else {
						if (project.task_kind.equals(Constants.VIRTUAL)) {
							cmd = CmdFactory.createPostVirtualProjectCmd(project);
							cmd.addData(Project.FLD_MULTI_LOCATIONS,dbMgr.getTaskLocationJSONArray(recId));
						} else {
							project.workLocation = dbMgr.getTaskWLocation(recId);
							cmd = CmdFactory.createPostInPersonProjectCmd(project);
						}
						cmd.addData(Project.FLD_MULTI_SKILLS, dbMgr.getTaskSkillJSONArray(recId));
						cmd.addData(Project.FLD_MULTI_CAT_QUESTIONS,dbMgr.getTaskQuestion(recId));
						cmd.addData(Project.FLD_INVITED_DOERS, dbMgr.getTaskDoers(recId));
					}

					response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
					if (response != null) {
						trno = dbMgr.updateTaskData(response, project,null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Util.writeDBfileOnSdcard(AppGlobals.appContext);
		return response;
	}

	public static NetworkResponse getCategorySkillAndQuestion(Long skillTrno,
			Long quesTrno, String categoryId) {
		NetworkResponse response = null;
		try {
			Cmd cmd = CmdFactory.createGetCategorySkillAndQuestionCmd(
					skillTrno, quesTrno, categoryId);
			response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static NetworkResponse postPendingMessages(ArrayList<MessageDetail> pendingMsg) {
		NetworkResponse response = null;
		try {
			if (Util.isDeviceOnline(AppGlobals.appContext)) {
				DatabaseMgr dbMgr = DatabaseMgr.getInstance(AppGlobals.appContext);
				Long trno = null;
				Cmd cmd = null;
				for (MessageDetail messages : pendingMsg) {
					if (trno != null)
						messages.trno = trno;
					
					cmd = CmdFactory.createNewMessageCmd();
					cmd.addData(MessageDetail.FLD_TO_USER_ID, messages.to_user_ids);
					cmd.addData(MessageDetail.FLD_TASK_ID, messages.task_id);
					cmd.addData(MessageDetail.FLD_BODY, messages.body);
					cmd.addData(MessageDetail.FLD_MESSAGE_TYPE, messages.msg_type);
					cmd.addData(MessageDetail.FLD_IS_PUBLIC, messages.is_public);
					cmd.addData(MessageDetail.FLD_MOBILE_REC_ID,Util.getMobileRecToken(messages.recId));
					cmd.addData(MessageDetail.FLD_TRNO, messages.trno); 
					response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
					if (response != null) {
						if (response.isSuccess()) {
							trno = dbMgr.updateMessageData(response, messages,null);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}


}
