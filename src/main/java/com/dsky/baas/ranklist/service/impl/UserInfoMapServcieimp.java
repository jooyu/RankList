package com.dsky.baas.ranklist.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.dsky.baas.ranklist.config.RedisRepository;
import com.dsky.baas.ranklist.dao.UserInfoMapMapper;
import com.dsky.baas.ranklist.model.UserInfoMap;
import com.dsky.baas.ranklist.service.IUserInfoMapService;
import com.dsky.baas.ranklist.util.CommonUtil;

@Service("userInfoMapServcie")
public class UserInfoMapServcieImp implements IUserInfoMapService{
	private final Logger log = Logger.getLogger(UserInfoMapServcieImp.class);
	@Autowired
	private RedisRepository redisRepository;
	


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




	

}
