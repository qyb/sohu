package com.scss.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Copyright  2003 Beijing Northen FiberHome Co. Ltd. All rights reserved.
 * Description:    日志输出类
 *  1.日志信息分等级,从低到高依次为【DEBUG,INFO,WARN,ERROR】，
 *    可在构造对象时或运行时改变允许输出的日志等级，如只输出等级在WARN以上的日志信息
 *  2.可以将日志输出到文件，同时可输出到屏幕
 *  3.日志文件输出的目录默认为系统TEMP目录
 *  4.日志文件每天产生一个，但如果文件大小达到某一门限值(20M)，将新生成一个文件
 * 该类的使用方法：
 *  第一步：获取日志对象: getLogger;
 *  第二步：输出各类日志信息： debug,info,warn,error,exception;
 *                        debugT,infoT,warnT,errorT
 * 示例：见 main()
 * Author: Hu Zhi Yong
 * Version:V1.0
 * 2008-12-13 输出类名,方法名与行号
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class Logger
{
	//日志级别
	public static final int ALL = 0;

	public static final int DEBUG = ALL + 1;

	public static final int INFO = DEBUG + 1;

	public static final int WARN = INFO + 1;

	public static final int ERROR = WARN + 1;

	public static final int NONE = 0x7fffffff;

	private static final String[] LOG_LEVEL = { "ALL", "DEBUG", "INFO", "WARN","ERROR" };

	//the maximum size of each log file 20M
	private static final long MAX_FILE_SIZE = 20480000;

	//private SimpleDateFormat formatter = new SimpleDateFormat(
	//  "yyyy-MM-dd HH:mm:ss" );

	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

	private static final SimpleDateFormat fileformatter = new SimpleDateFormat("yyyyMMdd-HHmmss");

	private static final HashMap loggers = new HashMap(64);

	private File file = null;

	private PrintWriter fileWriter;

	private String logDir =  null;

	private int CUR_DATE;

	private String logName = "syslog";

	private int logLevel = ALL; // default loglevel

	private boolean toScreen = true; //日志信息是否输出到屏幕

	public static synchronized Logger[] getAllLogger()
    {
    	Logger[] ret = new Logger[loggers.values().size()];

    	ret =  (Logger[])loggers.values().toArray(ret);

    	return ret;
    }
    /**
     * 获得日志对象
     * @param logName 日志对象名称，如：MyLOg,ConfigLOg,...
     * @param logLevel允许输出的最低等级，有效取值为：ALL,DEBUG,INFO,WARN,ERROR
     * @param toScreen 是否输出到屏幕
     * @return 日志对象
     */
    public static synchronized Logger getLogger( String logName)
    {
    	return getLogger(logName,Logger.ALL,true);
    }
    
	/**
	 * 获得日志对象
	 * @param logName 日志对象名称，如：MyLOg,ConfigLOg,...
	 * @param logLevel允许输出的最低等级，有效取值为：ALL,DEBUG,INFO,WARN,ERROR
	 * @param toScreen 是否输出到屏幕
	 * @return 日志对象
	 */
	public static synchronized Logger getLogger(String logName, int logLevel,
			boolean toScreen) {
		Logger logger = (Logger) loggers.get(logName);
		if (logger == null) {
			logger = new Logger(logName, logLevel, toScreen);
			loggers.put(logName, logger);
		} else {
			logger.setLogLevel(logLevel);
		}
		return logger;
	}

	/**
	 * 获得日志对象
	 * @param logDir 日志文件存放的目录
	 * @param logName 日志对象名称，如：MyLOg,ConfigLOg,...
	 * @param logLevel允许输出的最低等级，有效取值为：ALL,DEBUG,INFO,WARN,ERROR
	 * @param toScreen 是否输出到屏幕
	 * @return 日志对象
	 */
	public static synchronized Logger getLogger(String logDir, String logName,
			int logLevel, boolean toScreen) {
		Logger logger = (Logger) loggers.get(logName);
		if (logger == null) {
			logger = new Logger(logName, logLevel, toScreen);
			logger.logDir = logDir;
			loggers.put(logName, logger);
		}
		return logger;

	}
    /**
     * add by 敖战 2009-02-02
     * 自动根据类名获得日志对象
     * @param classN 日志对象所在类名,
     * @param classPathLevel 日志对象所在类路径的层位置，如com.wri.hy.onecm,onecm就是第3层
     * @param logLevel允许输出的最低等级，有效取值为：ALL,DEBUG,INFO,WARN,ERROR
     * @param toScreen 是否输出到屏幕
     * @return 日志对象
     */
    public static synchronized Logger getLogger(Class classN, int classPathLevel, 
    		int logLevel,  boolean toScreen )
    {
    	String cName=classN.getName();
    	String[] pNames = cName.split("\\.");
    	StringBuffer logName=new StringBuffer();
    	for(int i=classPathLevel; i<pNames.length; i++){
    		if(i>classPathLevel){
    			logName.append('/');
    		}
    		logName.append(pNames[i]);
    	}
    	//System.out.println(cName+"*********"+pNames.length+"************"+classPathLevel);
    	return getLogger(logName.toString().toUpperCase(), logLevel, toScreen);
    }

	/**
	 * 获取日志文件输出目录
	 * @return
	 */
	private String getLogDir() 
	{
		if ( this.logDir == null )
		{
			logDir =  System.getProperty("user.home") + File.separator + "log"
				+ File.separator + this.logName;
		}
		return this.logDir;
	}
/*
	private void close() {
		if (this.fileWriter != null) {
			fileWriter.close();
		}
	}
*/
	/**
	 * 构造函数，其它类不能构造该日志对象，只能通过
	 * @param logFileName  日志对象名称，如：MyLOg,ConfigLOg,...
	 * @param logLevel 允许输出的最低等级，有效取值为：ALL,DEBUG,INFO,WARN,ERROR
	 * @param toScreen 是否输出到屏幕
	 */
	private Logger(String logFileName, int logLevel, boolean toScreen) {
		logName = logFileName;

		logDir = getLogDir();

		if (logLevel > ERROR || logLevel < ALL)
			this.logLevel = ALL;
		else
			this.logLevel = logLevel;

		this.toScreen = toScreen;

		setNewWriter();
	}

	/**
	 * 获得一形如 YYYYMMDD|20031105的八位整数
	 * @return
	 */
	private int getCurrDateDigital() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(Calendar.getInstance().getTime());
		int curDate = 0;
		try {
			curDate = Integer.parseInt(date);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return curDate;
	}

	/**
	 * 是否需要新生成一个日志文件
	 * 如果进入了新的一天，或者日志文件大小达到了门限值，则需要新生成一个日志文件
	 * @return
	 */
	private boolean isNewLogFileRequired() {
		if (file == null)
			return false;
		else
			return (getCurrDateDigital() > CUR_DATE)
					|| (file.length() > MAX_FILE_SIZE);
	}

	/**
	 * 生成一个新的PrintWriter对象，供写日志用
	 */
	private synchronized void setNewWriter() {
		if (fileWriter != null)
			fileWriter.close();

		if (getCurrDateDigital() > CUR_DATE) {
			CUR_DATE = getCurrDateDigital();

			//logDir = logDir + File.separator + logName;

			new File(logDir).mkdirs();
		} else {
			String nfile = logDir + File.separator
					+ fileformatter.format(Calendar.getInstance().getTime())
					+ ".log";
			file.renameTo(new File(nfile));
		}

		String logFile = logDir + File.separator + CUR_DATE + ".log";
		if (this.toScreen == true) {
			System.out.println("LOG FILE:" + logFile);
		}

		try {
			file = new File(logFile);
			fileWriter = new PrintWriter(new FileWriter(file, true));
		} catch (IOException ex) {
			System.err.println("Fatal Error [" + ex.toString() + "]!"
					+ " cannot write to the log file [" + logFile + "].");
			System.err.println("System.out is used.");
			fileWriter = new PrintWriter(System.out);
		}
	}

	/**
	 * 输出日志到日志文件(和屏幕)
	 * @param message  要输出的日志信息
	 * @param logLevel 日志信息等级
	 * @param timeMark 是否打上时间标记
	 */
	private synchronized void log(Object message, int logLevel, boolean timeMark) {
		if (this.logLevel > logLevel || fileWriter == null) {
			return;
		}
		if (isNewLogFileRequired()) {
			setNewWriter();
		}
		//构造时间
		String buff;
		if (timeMark == true) {
			buff = formatter.format(Calendar.getInstance().getTime()) + "["
					+ LOG_LEVEL[logLevel] + "]" + getCallerInfo();
		} else {
			buff = "";
		}

		buff += message;

		if (this.toScreen) {
			if (logLevel >= WARN)
				System.err.println(buff);
			else
				System.out.println(buff);
		}

		//写入文件
		fileWriter.println(buff);
		fileWriter.flush();
	}

	/**
	 * 输出带时间标记的DEUBG级别的日志信息
	 * @param message 要输出的日志信息
	 */
	public void debugT(Object message) {
		log(message, DEBUG, true);
	}

	/**
	 * 输出不带时间标记的DEUBG级别的日志信息
	 * @param message 要输出的日志信息
	 */
	public void debug(Object message) {
		log(message, DEBUG, false);
	}

	/**
	 * 输出带时间标记的INFO级别的日志信息
	 * @param message 要输出的日志信息
	 */
	public void infoT(Object message) {
		log(message, INFO, true);
	}

	/**
	 * 输出不带时间标记的INFO级别的日志信息
	 * @param message 要输出的日志信息
	 */
	public void info(Object message) {
		log(message, INFO, false);
	}

	/**
	 * 输出带时间标记的WARN级别的日志信息
	 * @param message 要输出的日志信息
	 */
	public void warnT(Object message) {
		log(message, WARN, true);
	}

	/**
	 * 输出不带时间标记的WARN级别的日志信息
	 * @param message 要输出的日志信息
	 */
	public void warn(Object message) {
		log(message, WARN, false);
	}

	/**
	 * 输出带时间标记的ERROR级别的日志信息
	 * @param message 要输出的日志信息
	 */
	public void errorT(Object message) {
		log(message, ERROR, true);
	}

	/**
	 * 输出不带时间标记的ERROR级别的日志信息
	 * @param message 要输出的日志信息
	 */
	public void error(Object message) {
		log(message, ERROR, false);
	}

	/**
	 * 输出带时间标记的Exception详细信息
	 * @param throwable 异常对象
	 */
	public void exception(Throwable throwable) {
		if (fileWriter == null || logLevel == NONE)
			return;

		if (isNewLogFileRequired()) {
			setNewWriter();
		}

		String buff = formatter.format(Calendar.getInstance().getTime());

		buff += "[EXCEPTION] Thr:" + Thread.currentThread().getName();

		if (this.toScreen) {
			System.err.println(buff);
			throwable.printStackTrace();
		}

		fileWriter.println(buff);
		throwable.printStackTrace(fileWriter);
		fileWriter.flush();

	}

	/**
	 * 获得调用者信息
	 * @return － 调用者信息
	 */
	private String getCallerInfo() {
		// 通过这个classname的获取我们是可以获取调用类的名称
		Exception e = new Exception();
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		String callerInfo = null;

		if (stackTraceElements.length > 3) {
			
			//int callerDept = stackTraceElements.length - 1;
			int callerDept = 3;
			
			String className = stackTraceElements[callerDept].getClassName();
			className = className.substring(className.lastIndexOf(".") + 1);

			callerInfo = "[" + className + "."
					+ stackTraceElements[callerDept].getMethodName() + "->"
					+ stackTraceElements[callerDept].getLineNumber() + "] ";
		}

		return callerInfo;
	}

	public void setToScreen(boolean toScreen) {
		this.toScreen = toScreen;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public int getLogLevel() {
		return logLevel;
	}

    public String getLogLevelLabel()
    {
    	if ( this.logLevel >= 0 && this.logLevel <= 4 )
    		return Logger.LOG_LEVEL[logLevel];
    	else
    		return "UNKNOWN";
    }
    
	@SuppressWarnings("null")
	public static void main(String[] args) {
		//Step 1: 获取日志对象
		Logger logger = Logger.getLogger("mylog", ALL, true);

		logger.debug(logger.getCallerInfo());

		//Step 2: 输出各种级别的日志信息
		logger.debugT("here is debug log with timemark");
		logger.debug("Here is debug log without timemark");

		logger.infoT("here is info log with timemark");
		logger.info("Here is info log without timemark");

		logger.warnT("here is warn log with timemark");
		logger.warn("Here is warn log without timemark");

		logger.errorT("Here is error log with timemark");
		logger.error("Here is error log without timemark");

		try {
			int[] a = null;
			a[0] = 0;
		} catch (Exception e) {
			//输出异常信息
			logger.exception(e);
		}
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public boolean isToScreen() {
		return toScreen;
	}

	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}
}
