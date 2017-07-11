package com.dsky.baas.ranklist.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;



import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dsky.baas.ranklist.config.RedisRepository;
import com.dsky.baas.ranklist.dao.UserInfoMapMapper;
import com.dsky.baas.ranklist.model.UserInfoMap;
import com.dsky.baas.ranklist.service.IUserInfoMapService;
import com.dsky.baas.ranklist.util.CommonUtil;
import com.dsky.baas.ranklist.util.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Service("userInfoMapServcie")
public class UserInfoMapServcieImp implements IUserInfoMapService{
	 private static final ObjectMapper objectMapper = ObjectMapperFactory.getDefaultObjectMapper();
	private final Logger log = Logger.getLogger(UserInfoMapServcieImp.class);
	@Autowired
	private RedisRepository redisRepository;
	
	@Value("${kv.url.get}")
	private String  kvUrlGet;


	@Autowired
	private UserInfoMapMapper userInfoMapMapper;
	@Override
	public int insertUserInfoMap(int uid, int boardid, int score,
			int create_time,int expireTime) {
		if(uid<=0 ||boardid<=0 ||score<0)
		{
			return -1;
		}
		if(selectUserInfoMapByUidAndBoardid(uid,boardid)!=null)
		{
			return -1;
		}
		UserInfoMap userInfoMap=new UserInfoMap();
		userInfoMap.setUid(uid);
		log.debug("uid");
		log.debug(uid);
		userInfoMap.setLeaderboardid(boardid);
		log.debug("boardid");
		log.debug(boardid);
		userInfoMap.setScore(score);
		log.debug("score");
		log.debug(score);
		userInfoMap.setCreateTime(CommonUtil.getNowTimeStamp());
		userInfoMap.setExpireTime(expireTime);
		log.debug("insert~~");
		return userInfoMapMapper.insertSelective(userInfoMap);
	}

	@Override
	public int updateUserInfoMap(int uid, int boardid, int score,
			int create_time,int expireTime) {
		if(uid<=0 ||boardid<=0 ||score<0)
		{
			return -1;
		}
		UserInfoMap userInfoMap=new UserInfoMap();
		userInfoMap.setUid(uid);
		log.debug("uid");
		log.debug(uid);
		userInfoMap.setLeaderboardid(boardid);
		log.debug("boardid");
		log.debug(boardid);
		userInfoMap.setScore(score);
		log.debug("score");
		log.debug(score);
		userInfoMap.setCreateTime(CommonUtil.getNowTimeStamp());
		userInfoMap.setExpireTime(expireTime);
		
		return userInfoMapMapper.updateByUidAndBoardid(userInfoMap);
	}

	@Override
	public int checkRedisCachedKey(int uid,int gid,int boardid,int score,int expireType) {
		int topScore = 0;
	
		String MaxScoreKey = gid + "_" + boardid + "_top";
		// 存取redis
		
			topScore = getTopScoreFromRedis(uid, gid, boardid);
			log.debug("topScore-before");
			log.debug(topScore);
			if (score > topScore) {
				topScore = score;
			}
			log.debug("topScore-after");
			log.debug(topScore);
			redisRepository.set(MaxScoreKey, topScore + "");
			redisRepository.setExpire(MaxScoreKey, topScore + "",CommonUtil.expireTimeByExpireType(CommonUtil.getNowTimeStamp(), expireType));

		return topScore;
	}

	@Override
	public int getTopScoreFromRedis(int uid,int gid,int boardid) {
		int topScore = 0;
		String MaxScoreKey = gid + "_" + boardid + "_top";
	
	
		String cachedContent;
		// 存取redis
	
			cachedContent = redisRepository.get(MaxScoreKey);
			if (cachedContent == null) {
				redisRepository.set(MaxScoreKey,"0");
			}
			topScore = CommonUtil.parseInt(cachedContent);
		
		return topScore;
	}

	@Override
	public int storeRedisScoreKey(int uid,int gid,int boardid,int scoreSection,int score,int expireTime) {

		//检测score在所有分区中是否存在
		//先查询数据库，找到上次的总分，删除分区键值，写入最新数据，在写入mysql
		int rtnResult=0;
		
		String cacheContent;
		
		//查询数据库
			UserInfoMap userInfoMapScore=selectUserInfoMapByUidAndBoardid(uid,boardid);
		//存在
			
		if(userInfoMapScore!=null)
		{
			log.debug("存在");
			//如果存在,检测rediskey是否存在，那么删除key
			int oldScore=userInfoMapScore.getScore();
			log.debug("oldScore");
			log.debug(oldScore);
			int oldPartion=oldScore/scoreSection;
			log.debug("oldPartion");
			log.debug(oldPartion);
			String oldScoreKey=gid+"_"+boardid+"_"+oldPartion;
				//删除key
			log.debug("oldScoreKey");
			log.debug(oldScoreKey);
			cacheContent=redisRepository.operateZsetRank(oldScoreKey, uid+"")+"";
			log.debug("cacheContent");
			log.debug(cacheContent);
			if(!cacheContent.isEmpty())
			{
				redisRepository.operateZsetRemove(oldScoreKey,  uid+"");
			}
			log.debug("update mysql");
			rtnResult=updateUserInfoMap( uid,  boardid, score,
					 CommonUtil.getNowTimeStamp(),expireTime);
		}
		else
		{
			//mysql不存在
			log.debug("insert mysql");
			rtnResult=insertUserInfoMap(uid,  boardid, score,
					CommonUtil.getNowTimeStamp(),expireTime);
		}
		//设置redis
		int newScorePartion=score/scoreSection;
		String newScoreKey=gid+"_"+boardid+"_"+newScorePartion;
		redisRepository.operateZsetAdd(newScoreKey,uid+"", score);
		if(redisRepository.operateZsetCard(newScoreKey)==1)
		{
			log.debug("设置过期时间！");
			redisRepository.setExpire(newScoreKey, expireTime);
			
		}

		
		return rtnResult;
	}

	@Override
	public UserInfoMap selectUserInfoMapByUidAndBoardid(int uid,
			int boardid) {
		UserInfoMap user=new UserInfoMap();
		user.setUid(uid);
		log.debug("uid");
		log.debug(uid);
		user.setLeaderboardid(boardid);
		log.debug("boardid");
		log.debug(boardid);
		log.debug("select~before");

		log.debug("select~after");
		UserInfoMap result=userInfoMapMapper.selectOutScore(user);
	
		return result;
	}

	@Override
	public int getScoreByUidAndBoardid(int uid, int gid, int boardid) {
		UserInfoMap user=selectUserInfoMapByUidAndBoardid(uid,boardid);
		if(user==null)
		{
			return -1;
		}
		return user.getScore();
	}

	@Override
	public JsonNode getUserID(int uid, int gid, int boardid) {
		//2017.2.9
				int topScore=getTopScoreFromRedis(uid, gid, boardid);
				log.debug("topScore");
				log.debug(topScore);
			

				String Uids =null; 

				
					String topNKey=gid+"_"+boardid+"_topN";
					log.debug("topNKey");
					log.debug(topNKey);
			
					Set<String> topNResult=redisRepository.operateZsetRevrange(topNKey, 0, -1);
					 ObjectNode node = objectMapper.createObjectNode();  
						node.put("sort_rule",  "desc");
					

				
					try {
						Uids=objectMapper.writeValueAsString(topNResult);
						node.put("uids", Uids);
						log.debug("Uids");
						log.debug(Uids);
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		return node;
	}

	@Override
	//cache缓存
	public JsonNode getUserInfo(int uid, int gid, int boardid) {
	ObjectNode userInfo=objectMapper.createObjectNode();  
	String topNKey=gid+"_"+boardid+"_topN";
//	CookieStore cookieStore = new BasicCookieStore();//new BasicCookieStore();
//	cookieStore.addCookie(new BasicClientCookie("Cookie", "uid="+uid+";"+"gid="+gid));
//	
//	log.debug("cookieStore");
//	log.debug(cookieStore.toString());
//	HttpContext localContext = new BasicHttpContext();
//	localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	Set<String> topNResult=redisRepository.operateZsetRevrange(topNKey, 0, -1);
		for (String user : topNResult) {
			HttpClient client = new DefaultHttpClient();
			StringBuilder sb=new StringBuilder(kvUrlGet);
			sb.append("?key=").append("uid_").append(user);
			log.debug("url");
			log.debug(sb);
			//发出请求
			HttpGet httpRequest = new HttpGet(kvUrlGet);
			HttpResponse response;
			httpRequest.setHeader("Cookie", "uid="+uid+";"+"gid="+gid);
			try {
				
				response = client.execute(httpRequest);
				String returnedString = new String(readEntity(response.getEntity()).getBytes("UTF-8"), "UTF-8");
				log.info("result =="+returnedString);
				ObjectMapper mapper = new ObjectMapper();
				JsonNode resultObject = mapper.readTree(returnedString);
				
				userInfo.put(user, resultObject.get("data"));
			} catch (IOException e) {
				
				e.printStackTrace();
			}	
		}
		//读取kv系统的userinfo
		
		userInfo.put("ttl", "5分钟");
		userInfo.put("sort_rule","desc");
	
			
	
		return userInfo;
	}

	private String readEntity(HttpEntity entity) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			String line;
			StringBuffer content = new StringBuffer("");
			while (((line = reader.readLine()) != null)) {
				content.append(line);
			}
			return content.toString();
		} catch (IOException e) {
			throw new RuntimeException(
					"HttpRequest JSON processing IOException", e);
		} finally {
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//	 private String getCookies(int uid,int gid,HttpClient client) {
//	        StringBuilder sb = new StringBuilder();
//	        List<Cookie> cookies = ((AbstractHttpClient)client).getCookieStore().getCookies();
////	        for(Cookie cookie: cookies)
////	            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
//
//	        // 除了HttpClient自带的Cookie，自己还可以增加自定义的Cookie
//	        // 增加代码...
//
//	        sb.append("uid=").append(uid).append(";").append("gid=").append(gid);
//	        return sb.toString();
//	    }
}
