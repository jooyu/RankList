package com.dsky.baas.ranklist.lib;

import java.util.HashMap;

public class ApiResultObject {
	private Integer code = 0;
	private String msg = "";
	private Object data;
	private Object ext;
	public ApiResultObject(){
		data = new HashMap<String,Object>();
		ext = new HashMap<String,Object>();
	}
	public ApiResultObject(Integer code,String msg,Object data,Object ext){
		this();
		if(code==null){
			code = 0;
		}
		this.setCode(code);
		
		if(msg==null){
			msg = "";
		}
		this.setMsg(msg);
		if(data!=null){
			this.setData(data);
		}
		
		if(ext!=null){
			this.setExt(ext);
		}
	}
	public ApiResultObject(Integer code,String msg,Object data){
		this(code,msg,data,null);
	}
	public ApiResultObject(Integer code,String msg){
		this(code,msg,null,null);
	}	
	public ApiResultObject(Object data){
		this(null,null,data,null);
	}
	public ApiResultObject(Object data,Object ext){
		this(null,null,data,ext);
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Object getExt() {
		return ext;
	}
	public void setExt(Object ext) {
		this.ext = ext;
	}

}
