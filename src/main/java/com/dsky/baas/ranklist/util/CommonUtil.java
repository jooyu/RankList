package com.dsky.baas.ranklist.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;



public class CommonUtil {

	private final static Logger log = Logger.getLogger(CommonUtil.class);
	/**
	 * 解析http header 中的cookie
	 * @param req
	 * @return
	 */
	public static Map<String, String> parseHeaderCookie(HttpServletRequest req) {
		String cookieString = req.getHeader("cookie");
		if (cookieString == null || cookieString.isEmpty()) {
			cookieString = req.getHeader("cookied");
		}
		log.debug("cookie names string:"
				+ JSON.toJSONString(req.getHeaderNames()));

		if (cookieString != null && !cookieString.isEmpty()) {
			String[] cookieArray = cookieString.split(";");
			Map<String, String> cookieRes = new HashMap<String, String>();
			for (int i = 0; i < cookieArray.length; i++) {
				String cookieItem = cookieArray[i].trim();
				String[] cookieItemKV = cookieItem.split("=");
				if (cookieItemKV.length == 2) {
					cookieRes.put(cookieItemKV[0].trim(),
							cookieItemKV[1].trim());
				}
			}
			return cookieRes;
		} else {
			return null;
		}

	}

	public static int getNowTimeStamp() {
		return (int) (System.currentTimeMillis() / 1000);
	}
	
	public static Object pickValue(Object... args){
		Object now = null;
		for(int i=0;i<args.length;i++){
			if(args[i]!=null){
				if(args[i] instanceof String){
					now = args[i];
					String nowString = (String)now;
					if(!nowString.isEmpty()){
						return nowString;
					}
				}else if(args[i] instanceof Integer){
					now = args[i];
					int nowInt = parseInt(now);
					if(nowInt!=0){
						return nowInt;
					}
				}
			}
		}
		return now;
	}
	
	public static int parseInt(Object param){
		return parseInt(param,0);
	}

	public static int parseInt(Object param,int defaultNumber){
		if(param==null){
			return defaultNumber;
		}
		try {
			return Integer.parseInt(param.toString());	
		} catch (Exception e) {
			return defaultNumber;
		}
	}	
	
	/**
	 * 返回今天是星期几
	 * @return 0 周日，1周一，....，6周六
	 */
	public static int getTodayOfWeek(){
		Date today = new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(today);
        return c.get(Calendar.DAY_OF_WEEK)-1;
	}
	
	public static String getConfigItem(String itemName,String filename){
		InputStream inputStream = CommonUtil.class.getClassLoader().getResourceAsStream(filename);
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e) {
			log.debug("getConfigItem error: "+e.getMessage());			
			return null;
		}
		
		String result = p.getProperty(itemName);
		log.debug("getConfigItem name: "+itemName);
		log.debug("getConfigItem filename: "+filename);
		log.debug("getConfigItem result: "+result);
		try {
			inputStream.close();
		} catch (IOException e) {
			log.debug("getConfigItem input stream close error: "+e.getMessage());
		}
		return result;
	}
	public static String getConfigItem(String itemName){
		return getConfigItem(itemName, "config.properties");
	}
	
	//判断是否是controller
	public static boolean isAControllerClassName(String className){
//		com.dsky.baas.gameinvite.controller.InvitedCodeController
		return Pattern.compile("^com\\.dsky\\.baas\\.(.*)Controller$").matcher(className).find();

	}
	
	
	//获取的md5值，自己判断生成的MD5值
	public static boolean isMd5Key(String getMd5Key, String generateMd5Key)
	{
		if(!(getMd5Key.equalsIgnoreCase(generateMd5Key)))
		{
			System.out.println("getMd5Key");
			System.out.println(getMd5Key);
			System.out.println("stringMD5(str)");
			System.out.println(generateMd5Key);
			return false;
		}
		return true;
		
	}
	
	
	
	//md5判断   2016.12.23
//	public static boolean isMd5Key(String uid,String gid,String actid,String score,String memo,String key)
//	{
//		
////		if(uid.isEmpty()||gid.isEmpty()||actid.isEmpty()||score.isEmpty()||memo.isEmpty()||key.isEmpty())
////		{
////			return false;
////		}
//		String screctKey="tuiguangyuan";
//		String str="uid="+uid+"&gid="+gid+"&actid="+actid+"&score="+score+"&memo="+memo+"&screctKey="+screctKey;
////		System.out.println("str");
////		System.out.println(str);
//		if(!(key.equalsIgnoreCase(stringMD5(str))))
//		{
////			System.out.println(key.equals(stringMD5(str)));
////			System.out.println("key");
////			System.out.println(key);
//			System.out.println("stringMD5(str)");
//			System.out.println(stringMD5(str));
//			return false;
//		}
//		return true;
//		
//	}
	
	/**
	  * 获取加密后的字符串
	  * @param input
	  * @return
	  */
	 public static String stringMD5(String pw) {
	  try {  
	     
	        // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）  
	        MessageDigest messageDigest =MessageDigest.getInstance("MD5");  
	        // 输入的字符串转换成字节数组  
	        byte[] inputByteArray = pw.getBytes();  
	        // inputByteArray是输入字符串转换得到的字节数组  
	        messageDigest.update(inputByteArray);  
	        // 转换并返回结果，也是字节数组，包含16个元素  
	        byte[] resultByteArray = messageDigest.digest();  
	        // 字符数组转换成字符串返回  
	        return byteArrayToHex(resultByteArray);  
	     } catch (NoSuchAlgorithmException e) {  
	        return null;  
	     }  
	 }
	 
	    public static String byteArrayToHex(byte[] byteArray) {  
	        // 首先初始化一个字符数组，用来存放每个16进制字符  
	        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };  
	        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））  
	        char[] resultCharArray =new char[byteArray.length * 2];  
	        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去  
	        int index = 0; 
	        for (byte b : byteArray) {  
	           resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];  
	           resultCharArray[index++] = hexDigits[b& 0xf];  
	        }
	        // 字符数组组合成字符串返回  
	        return new String(resultCharArray);  
	    }
	    
	    
	    //计算两个时间点的秒数
	    @SuppressWarnings("unused")
		public static int expireTimeByExpireType(int createTime,int expireType)
	    {
	    	//换算当前时间为日期
	    	int expireTime = 0;
	    	Date time ;
	    	Date timeNow ;
	    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//HH:mm:ss
	    	 
	    	 Calendar c = Calendar.getInstance();  
	    	  time =c.getTime();
	    	   c.set(Calendar.HOUR_OF_DAY,0);
	    	     c.set(Calendar.MINUTE,0);
	    	     c.set(Calendar.SECOND, 0);
	    	 if(expireType==0)
	    	 {
	    		 expireTime = -1;
	    		 log.debug("永不过期");
	    	 }   
	    	 else if(expireType==1)
	    	{
	    		//日    当日
	    		 log.debug("日过期");
	    		 c.set(Calendar.DATE,c.get(Calendar.DATE)+1);
	    	    timeNow=c.getTime();
	    	     expireTime=CommonUtil.parseInt(timeNow.getTime()/1000)-createTime;
		         log.debug(time);
		         log.debug(c.getTime());
		         log.debug(CommonUtil.parseInt(timeNow.getTime()/1000));
	    		 log.debug(expireTime);
	    		
	    	}
	    	else if(expireType==2)
	    	{
	    		//周,返回当前周几  边界值
	    		 log.debug("周过期");
	          int weekKey= c.get(Calendar.DAY_OF_WEEK)-1;	
	          	System.out.println(c.get(Calendar.DATE));
	          	System.out.println(weekKey);
	          	int dateNow=c.get(Calendar.DATE);
		        switch (weekKey) {
				case 0:
				c.set(Calendar.DATE, dateNow+1);
					break;
	             case 1:
	            	 c.set(Calendar.DATE, dateNow+7);
					break;
	             case 2:
	            	 c.set(Calendar.DATE, dateNow+6);
					break;
	             case 3:
	            	 c.set(Calendar.DATE, dateNow+5);
					break;
	             case 4:
	            	 c.set(Calendar.DATE, dateNow+4);
					break;
	             case 5:
	            	 c.set(Calendar.DATE, dateNow+3);
					break;
	             case 6:
	            	 c.set(Calendar.DATE, dateNow+2);
					break;
			   }
			    timeNow=c.getTime();
	    	     expireTime=CommonUtil.parseInt(timeNow.getTime()/1000)-createTime;
		         log.debug(time);
		         log.debug(c.getTime());
		         log.debug(CommonUtil.parseInt(timeNow.getTime()/1000));
	    		 log.debug(expireTime);
	    	}
	    	else if(expireType==3)
	    	{
	    		//月
	    		 log.debug("月过期");
	    		if(Calendar.MONTH==11)
	    		{
	    			c.set(Calendar.MONTH,c.get(Calendar.MONTH));
	    			c.set(Calendar.DATE,+1);
	    			c.set(Calendar.YEAR,c.get(Calendar.YEAR)+1);
	    		}
	    		    c.set(Calendar.MONTH,c.get(Calendar.MONTH)+1);
	    		    c.set(Calendar.DATE,+1);
	    		    timeNow=c.getTime();
		    	     expireTime=CommonUtil.parseInt(timeNow.getTime()/1000)-createTime;
			         log.debug(time);
			         log.debug(c.getTime());
			         log.debug(CommonUtil.parseInt(timeNow.getTime()/1000));
		    		 log.debug(expireTime);
	    	}
			return expireTime;

	   }
}

