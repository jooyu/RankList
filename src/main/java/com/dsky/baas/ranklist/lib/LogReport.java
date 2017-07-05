package com.dsky.baas.ranklist.lib;

import java.io.Serializable;


public class LogReport implements Serializable {
	/**
	 * 实现序列化
	 */
	private static final long serialVersionUID = 863813635402906837L;
	// log上报日志结构
	public String logType;
	public String hostIp;
	public String playerId;
	public String module;
	public String cmd;
	public int errorCode;
	public int retCode;
	public int projectType;
	public String srcFile;
	public int srcLine;
	public String func;
	public int pid;
	public String level;
	public String msg;
	public int exectime;
	public String useAgent;
	public int requestTime;
	public String appid;
	
	

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public int getProjectType() {
		return projectType;
	}

	public void setProjectType(int projectType) {
		this.projectType = projectType;
	}

	public String getSrcFile() {
		return srcFile;
	}

	public void setSrcFile(String srcFile) {
		this.srcFile = srcFile;
	}

	public int getSrcLine() {
		return srcLine;
	}

	public void setSrcLine(int srcLine) {
		this.srcLine = srcLine;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getExectime() {
		return exectime;
	}

	public void setExectime(int exectime) {
		this.exectime = exectime;
	}

	public String getUseAgent() {
		return useAgent;
	}

	public void setUseAgent(String useAgent) {
		this.useAgent = useAgent;
	}

	public int getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(int requestTime) {
		this.requestTime = requestTime;
	}

	public LogReport(){}
	public LogReport(String logType, String hostIp, String playerId,
			String module, String cmd, int errorCode, int retCode,
			int projectType, String srcFile, int srcLine, String func, int pid,
			String level, String msg, int exectime, String useAgent,
			int requestTime,String appid) {
		super();
		this.logType = logType;
		this.hostIp = hostIp;
		this.playerId = playerId;
		this.module = module;
		this.cmd = cmd;
		this.errorCode = errorCode;
		this.retCode = retCode;
		this.projectType = projectType;
		this.srcFile = srcFile;
		this.srcLine = srcLine;
		this.func = func;
		this.pid = pid;
		this.level = level;
		this.msg = msg;
		this.exectime = exectime;
		this.useAgent = useAgent;
		this.requestTime = requestTime;
		this.setAppid(appid);
	}

	public String logReportToString() {
		return logType + "," + hostIp + "," + playerId + "," + module + ","
				+ cmd + "," + errorCode + "," + retCode + "," + projectType
				+ "," + srcFile + "," + srcLine + "," + func + "," + pid + ","
				+ level + "," + msg + "," + exectime + "," + useAgent + ","
				+ requestTime;//+","+getAppid();

	}

}
