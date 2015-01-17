package erandoo.app.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import erandoo.app.config.Config;

public class DownloadHelper extends AsyncTask<Void, Void,Boolean> {
	private Context context;
	private String fileUrl;
	private String fileName;
	private ProgressDialog progressDialog;
	
	
	public static void downloadFile(Context context,String fileUrl, String fileName){
		DownloadHelper dHelper = new DownloadHelper();
		dHelper.context = context;
		dHelper.fileUrl = Util.urlValidator(fileUrl);
		dHelper.fileName = fileName;
		if(isDownloadManagerAvailable(context)){ 
			fileDownloadByDManager(context,dHelper.fileUrl,dHelper.fileName);
		}else{
			dHelper.execute();
		}
	}
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Downloading..."); 
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.show();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		Boolean isSucess = false;
		File dFile = null;
		File dir = null;
		FileOutputStream fileStream = null;
		InputStream inputStream = null;
		try{
			dir = new File(Config.PATH_DOWNLOADED_FILES);
			if(!dir.exists()){
				dir.mkdir();
			}
			URL url = new URL(fileUrl);
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
            int fileSize = connection.getContentLength();
            Util.progressDialog.setMax(fileSize/1024);
          //  GlobalData.isDownloading = true;
            dFile = new File(dir,fileName);
           // GlobalData.downloadPath = dFile.getAbsolutePath();
            fileStream = new FileOutputStream(dFile);
            byte[] data = new byte[1024];
            int bytesRead = 0, totalRead = 0;
            inputStream = connection.getInputStream();
            while((bytesRead = inputStream.read(data, 0, data.length)) >= 0)
            {
            	fileStream.write(data, 0, bytesRead);
                totalRead += bytesRead;
                int totalReadInKB = totalRead /1024;
                progressDialog.setProgress((int)totalReadInKB);
                if(fileSize == totalRead){
                	//GlobalData.isDownloading = false;
                }
             }
            fileStream.close();
            inputStream.close();
            isSucess = true;
        }catch(Exception e){
        	dFile.delete(); 
        	isSucess = false;
        }finally {
            try {
                if (fileStream != null)
                	fileStream.close();
                if (inputStream != null)
                	inputStream.close();
            } catch (IOException ignored) { }
        }
		return isSucess;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if(result){
			Util.showCenteredToast(context, "Downloading completed"); 
		}else{
			Util.showCenteredToast(context, "Downloading interrupted"); 
		}
	}

	public static boolean isDownloadManagerAvailable(Context context) {
	    try {
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
	            return false;
	        }
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
	        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
	        return list.size() > 0;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static void fileDownloadByDManager(Context context,String fileUrl,String fileName){
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
		request.setDescription("Attachment Downaloding");
		request.setTitle("erandoo");
		// in order for this if to run, you must use the android 3.2 to compile your app
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    request.allowScanningByMediaScanner();
		    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

		// get download service and enqueue file
		DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}
}
