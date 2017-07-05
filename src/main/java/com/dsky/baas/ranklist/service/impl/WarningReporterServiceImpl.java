package com.dsky.baas.ranklist.service.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.dsky.baas.ranklist.lib.ApiResultObject;
import com.dsky.baas.ranklist.lib.LogReport;
import com.dsky.baas.ranklist.service.IWarningReporterService;
import com.dsky.baas.ranklist.util.CommonUtil;



@Service("warningReporterService")
public class WarningReporterServiceImpl implements IWarningReporterService {
	private Logger log = Logger.getLogger(WarningReporterServiceImpl.class);
	private ExecutorService exec = Executors.newCachedThreadPool();

	private String localAddress = "";
	
	private String getEth0IP() throws SocketException{
		
		
		
			NetworkInterface iface = null;
			String ethr;
			String myip = "";
			String regex = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +	"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

			for(Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();ifaces.hasMoreElements();)
			{
				iface = (NetworkInterface)ifaces.nextElement();
				ethr = iface.getDisplayName();
				log.debug("=NetworkInterface");
				log.debug(ethr);
				if (Pattern.matches("eth[0-9]", ethr))
				{
					System.out.println("Interface:" + ethr);
					InetAddress ia = null;
					for(Enumeration<InetAddress> ips = iface.getInetAddresses();ips.hasMoreElements();)
					{
						ia = (InetAddress)ips.nextElement();
						log.debug("====InetAddress");
						log.debug(ia.getCanonicalHostName());
						log.debug(ia.getAddress().toString());
						log.debug(ia.getHostAddress());
						if (Pattern.matches(regex, ia.getHostAddress()))
						{
							myip = ia.getHostAddress();
							log.debug("======ip");
							log.debug(myip);
							return myip;
						}
					}
				}
			}
			
			return myip;

	}
	
	private LogReport stdLogReportPacker(StackTraceElement[] stackElements,Integer code, String msg) {
		
		String classname = "";
		String filename = "";
		int fileline = 0;
		String cmd = "[boot test or no controller]";
		String funcName = "";
		for (int i = 0; i < stackElements.length; i++) {
			log.debug("###############");
			String nowClassName = stackElements[i].getClassName();
			log.debug(nowClassName);
			log.debug(stackElements[i].getFileName());
			log.debug(stackElements[i].getMethodName());
			if(funcName.equals("") && !nowClassName.equals(this.getClass().getName())){//获得非本类的调用
				funcName = stackElements[i].getMethodName();
			}
			if (CommonUtil.isAControllerClassName(nowClassName)) {
				classname = nowClassName;
				filename = stackElements[i].getFileName();
				fileline = stackElements[i].getLineNumber();
				cmd = stackElements[i].getMethodName();				

				break;
			}
		}
		int pid = CommonUtil.parseInt(Thread.currentThread().getId());
		int logLevel = 0;
		int exectime = 0;
		String useragent = "";
		int requestTime = CommonUtil.getNowTimeStamp();

		int projectType = CommonUtil.parseInt(CommonUtil.getConfigItem("remoteLogServer.project_type"));
		String module = CommonUtil.getConfigItem("remoteLogServer.sub-module");
		if(module==null||module.isEmpty()||module.equals("")){
			module = "unknow-module";
		}
		String msgType = "Access";
		if(code>0){
			msgType="Error";
		}
		
		LogReport logReportData = new LogReport();
		logReportData.setLogType(msgType);
		logReportData.setHostIp(this.localAddress);
		logReportData.setPlayerId("0");
		logReportData.setModule("推广员");
		logReportData.setCmd(cmd);
		logReportData.setErrorCode(code);
		logReportData.setRetCode(code);
		logReportData.setProjectType(projectType);
		logReportData.setSrcFile(filename + ":" + classname);
		logReportData.setSrcLine(fileline);
		logReportData.setFunc(funcName);
		logReportData.setPid(pid);
		logReportData.setLevel(logLevel + "");
		logReportData.setMsg(module+"::"+msg.replace(",", "，"));
		logReportData.setExectime(exectime);
		logReportData.setUseAgent(useragent);
		logReportData.setRequestTime(requestTime);
		logReportData.setAppid("0");

		return logReportData;
	}

	public WarningReporterServiceImpl() {
		
		super();
		log.debug("booting....!!");

			try {
				localAddress = getEth0IP();
			} catch (SocketException e) {
				log.error("Can't get the IP address of this local server|"+e.getMessage());
				reportWarnString("Can't get the IP address of this local server|"+e.getMessage());
			}
		reportWarnString("This is a test message that when the spring booting");
	}
	@Override
	public void captureApiResultPacker(Object retVal) {
		log.debug("captureApiResultPacker begin");
		Throwable ex = new Throwable();
		final StackTraceElement[] stackElements = ex.getStackTrace();
		final ApiResultObject retApiResultObj = (ApiResultObject) retVal;
		if (retApiResultObj.getCode() > 0) {
			Future<String> resultFuture = exec.submit(new Callable<String>() {
				@Override
				public String call() {

					reportWarn(stdLogReportPacker(stackElements,retApiResultObj.getCode(),
							retApiResultObj.getMsg()));
					return "";
				}
			});
		}

	}

	@Override
	public boolean reportWarn(LogReport logReportDataParam) {
		final LogReport logReportData = logReportDataParam;

		String logServerAddress = CommonUtil
				.getConfigItem("remoteLogServer.address");

		if (logServerAddress == null || logServerAddress.isEmpty()) {
			log.error(Thread.currentThread().getId()
					+ ":remoteLogServer.address value is NULL or not exists");
			return false;
		}
		String logServerPort = CommonUtil.getConfigItem("remoteLogServer.port");
		if (logServerPort == null || logServerPort.isEmpty()) {
			log.error(Thread.currentThread().getId()
					+ ":remoteLogServer.port value is NULL or not exists");
			return false;
		}
		int logServerPortInt = CommonUtil.parseInt(logServerPort);
		if (logServerPortInt == 0) {
			log.error(Thread.currentThread().getId()
					+ ": remoteLogServer.port value is NaN");
			return false;
		}

		String writeMsgContent = logReportData.logReportToString();
		
		log.debug("writeMsgContent:" + writeMsgContent);

		Socket socket = null;
		boolean returnVal = true;
		try {
			socket = new Socket(logServerAddress, logServerPortInt);
			// 向服务器端发送数据
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			// out.writeUTF(writeMsgContent);
			// out.writeChars(writeMsgContent);
			out.write(writeMsgContent.getBytes("UTF-8"));
			out.close();

		} catch (IOException e) {

			log.error("captureApiResultPacker socket error: " + e.getMessage());
			returnVal = false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				log.error("captureApiResultPacker socket close error: "
						+ e.getMessage());
			}
		}
		return returnVal;

	}

	@Override
	public void reportWarnString(String msgParam,int code) {
		log.debug("reportWarnString begin");
		final String msg = msgParam;
		Throwable ex = new Throwable();
		final StackTraceElement[] stackElements = ex.getStackTrace();
		final int finalCode = code;
		Future<String> resultFuture = exec.submit(new Callable<String>() {
			@Override
			public String call() {
				
				reportWarn(stdLogReportPacker(stackElements,finalCode, msg));
				return "";

			}
		});
		
	}
	
	@Override
	public void reportWarnString(String msgParam){
		reportWarnString(msgParam,1);
	}
	
}
