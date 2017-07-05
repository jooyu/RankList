package com.dsky.baas.ranklist.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dsky.baas.ranklist.config.RedisRepository;
import com.dsky.baas.ranklist.dao.UserInfoMapMapper;
import com.dsky.baas.ranklist.model.UserInfoMap;
import com.dsky.baas.ranklist.service.IUserInfoMapService;
import com.dsky.baas.ranklist.service.IWarningReporterService;
import com.dsky.baas.ranklist.util.CommonUtil;

@Service("userInfoMapServcie")
public class UserInfoMapServcieimp implements IUserInfoMapService{
	private final Logger log = Logger.getLogger(UserInfoMapServcieimp.class);
	@Autowired
	private UserInfoMapMapper userInfoMapMapper;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private IWarningReporterService iWarningReporterService;
	@Override
	public int insertUserInfoMap(int uid, int boardid, int score,
			int create_time) {
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
		return userInfoMapMapper.insertSelective(userInfoMap);
	}

	@Override
	public int updateUserInfoMap(int uid, int boardid, int score,
			int create_time) {
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
		return userInfoMapMapper.updateByUidAndBoardid(userInfoMap);
	}

	@Override
	public int checkRedisCachedKey(int uid,int gid,int boardid,int score) {
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
	
		
		return topScore;
	}

	@Override
	public int getTopScoreFromRedis(int uid,int gid,int boardid) {
		int topScore = 0;
		String MaxScoreKey = gid + "_" + boardid + "_top";
		
		String cachedContent;
		// 存取redis
		
			cachedContent = redisRepository.get(MaxScoreKey);
			if (cachedContent == null && cachedContent.isEmpty()) {
				cachedContent = "0";
			}
			topScore = CommonUtil.parseInt(cachedContent);
	
		return topScore;
	}

	@Override
	public int storeRedisScoreKey(int uid,int gid,int boardid,int scoreSection,int score) {
		//检测score在所有分区中是否存在
		//先查询数据库，找到上次的总分，删除分区键值，写入最新数据，在写入mysql
		int rtnResult=0;
		String cacheContent;
		//查询数据库
		UserInfoMap userInfoMap=selectUserInfoMapByUidAndBoardid(uid,boardid);
		//存在
		if(userInfoMap!=null)
		{
			//如果存在,检测rediskey是否存在，那么删除key
			int oldScore=userInfoMap.getScore();
			int oldPartion=oldScore/scoreSection;
			String oldScoreKey=gid+"_"+boardid+"_"+oldPartion;
				//删除key
			cacheContent=redisRepository.get(oldScoreKey);
			log.debug("cacheContent");
			log.debug(cacheContent);
			if(cacheContent!=null && cacheContent.isEmpty())
			{
				redisRepository.del(oldScoreKey);
			}
			log.debug("update mysql");
			rtnResult=updateUserInfoMap( uid,  boardid, score,
					 CommonUtil.getNowTimeStamp());
		}
		else
		{
			//mysql
			log.debug("insert mysql");
			rtnResult=insertUserInfoMap(uid,  boardid, score,
					CommonUtil.getNowTimeStamp());
		}
		//设置redis
		int newScorePartion=score/scoreSection;
		String newScoreKey=gid+"_"+boardid+"_"+newScorePartion;
		Double aScore=new Double(score);
		redisRepository.operateZset(newScoreKey, uid+"", aScore);
		
		//在存一个topN的集合
		String topNKey=gid+"_"+boardid+"_topN";
	//	int redisTopCount=CommonUtil.parseInt(rdsClient.zcount(topNKey,0,getTopScoreFromRedis(uid, gid, boardid)));
		Double bScore=new Double(score);
		redisRepository.operateZset(topNKey, uid+"", bScore);
		
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
		return userInfoMapMapper.selectOutScore(user);
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
