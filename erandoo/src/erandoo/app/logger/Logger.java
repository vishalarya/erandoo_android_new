package erandoo.app.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;
import erandoo.app.config.Config;
import erandoo.app.network.HttpConstants;

@SuppressLint("SimpleDateFormat")
public class Logger {

	public static boolean IS_INTERNAL_RELEASE = true;

	private String tag;
	private static boolean isInit = false;
	private static String fileName = null;
	private static File logFile = null;
	private static final long FILE_SIZE = 100 * 1024 * 1024; // 1 MB = 1024*1024
																// byte

	public static final int VERBOSE = 0;
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int WARNING = 3;
	public static final int ERROR = 4;

	private static int label = DEBUG;

	static {
		init(Config.LOG_FILE_NAME);
	}

	public Logger(String tag) {
		this.tag = tag;
	}

	/**
	 * This is static method, for initializing for variable of ELogger. This
	 * method takes file name as argument. If file name is passed then this
	 * class saves the log message in given file name on device SDCARD. If file
	 * name not provided then this class print the log only in logcat.
	 * 
	 * @param logFileName
	 * @return return's true if ELogger successfully initialized otherwise false
	 */
	synchronized public static boolean init(String logFileName) {

		if (isInit) {
			if (logFile != null) {
				fileName = null;
				logFile = null;
			}
			isInit = false;
		}

		if (logFileName != null) {
			fileName = logFileName;
			String status = Environment.getExternalStorageState();
			if (!status.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
				// System.out.println("init():SD Card is not mounted. Log cannot save");
				return false;
			}
			logFile = new File(Environment.getExternalStorageDirectory()
					+ File.separator + Config.APP_FOLDER_NAME, fileName);
//			String s = logFile.getAbsolutePath();			
//			System.out.println("file "+s);
			if (logFile != null) {
				if (!logFile.exists()) {
					try {
						logFile.createNewFile();
					} catch (IOException e) {
						logFile = null;
						// System.out.println("saveLogToFile(): Problem in creating File on SD Card");
						e.printStackTrace();
						return false;
					}
					isInit = true;
					return true;

				}
				if (logFile.length() >= FILE_SIZE) {
					if (backUpLog()) {

					}

				}

			} else {
				return false;
			}
		} else {
			return false;
		}

		isInit = true;
		return true;
	}

	static public void close() {
		fileName = null;
		logFile = null;
		isInit = false;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void info(String msg) {
		Log.i(tag, msg);
		msg = "[" + "INFO" + "] " + msg;
		if (logFile != null && label <= INFO) {
			saveLogToFile(msg);
		}
	}

	public void debug(String msg) {
		Log.d(tag, msg);
		msg = "[" + "DEBUG" + "] " + msg;
		if (logFile != null && label <= DEBUG) {
			saveLogToFile(msg);
		}
	}

	public void fatal(String msg) {
		Log.e(tag, msg);
		if (logFile != null)
			saveLogToFile(msg);
	}

	public void error(String msg) {
		Log.e(tag, msg);
		msg = "[" + "ERROR" + "] " + msg;
		if (logFile != null && label <= ERROR) {
			saveLogToFile(msg);
		}
	}

	public void warn(String msg) {
		Log.w(tag, msg);
		msg = "[" + "WARNING" + "] " + msg;
		if (logFile != null && label <= WARNING) {
			saveLogToFile(msg);
		}
	}

	public void verbose(String msg) {
		Log.v(tag, msg);
		msg = "[" + "VERBOSE" + "] " + msg;
		if (logFile != null && label <= VERBOSE) {
			saveLogToFile(msg);
		}
	}

	/**
	 * This method takes the backup of log file if the log file size is greater
	 * than 100MB
	 * 
	 * @return true if backup taken otherwise false
	 */
	@SuppressWarnings("unused")
	synchronized private static boolean backUpLog() {
		String backupFileName = fileName + "_backup";
		File backUpFile = new File(Environment.getExternalStorageDirectory()
				+ File.separator + Config.APP_FOLDER_NAME, backupFileName);
		if (backUpFile == null) {
			return false;
		}

		if (backUpFile.exists()) {

			backUpFile.delete();

		}

		logFile.renameTo(backUpFile);
		logFile = new File(Environment.getExternalStorageDirectory(), fileName);
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * This method saves the log message into specified file name. It takes
	 * message as parameter
	 * 
	 * @param message
	 *            to be saved
	 */
	synchronized private void saveLogToFile(String msg) {
		if (!Config.DEBUG_BUILD) {
			return;
		}
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestr = fmt.format(System.currentTimeMillis());
		long threadId = Thread.currentThread().getId();
		String logStr = "[" + timestr + "]" + " " + "[" + threadId + "]" + "  "
				+ tag + "  " + msg;

		if (logFile.length() >= FILE_SIZE) {

			if (backUpLog()) {

			} else {
				// TODO
			}

		}

		BufferedWriter buf = null;
		try {
			buf = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(logFile, true)),
					HttpConstants.INPUT_STREAM_BUFFSIZE);// (new
			// FileWriter(logFile,
			// true));
			if (buf != null) {
				buf.append(logStr);
				buf.newLine();
				buf.flush();
				buf.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to set the log levels.
	 * 
	 * @param logLevel
	 */
	public static void setLogLevel(int logLevel) {
		label = logLevel;
	}
}
