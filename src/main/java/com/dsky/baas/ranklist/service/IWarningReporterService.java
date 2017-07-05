package com.dsky.baas.ranklist.service;

import com.dsky.baas.ranklist.lib.LogReport;



public interface IWarningReporterService {
	public void captureApiResultPacker(Object retVal);
	public boolean reportWarn(LogReport logReportData);
	public void reportWarnString(String msg,int code);
	public void reportWarnString(String msg);

}
