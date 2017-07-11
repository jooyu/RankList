package com.dsky.baas.ranklist.service.impl;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dsky.baas.ranklist.config.RedisRepository;
import com.dsky.baas.ranklist.lib.ApiResultCode;
import com.dsky.baas.ranklist.model.LeaderBoardConfig;
import com.dsky.baas.ranklist.service.ILeaderBoardConfigService;
import com.dsky.baas.ranklist.service.IRankListService;
import com.dsky.baas.ranklist.service.IUserInfoMapService;
import com.dsky.baas.ranklist.util.CommonUtil;

@Service
public class RankListService implements IRankListService {
	private final Logger log = Logger.getLogger(RankListService.class);

	@Autowired
	private IUserInfoMapService iUserInfoMapService;
	@Autowired
	private RedisRepository redisRepository;
	@Override
	public int topNRank(int uid,int gid,int boardid,int score,LeaderBoardConfig leaderBoardConfig) {
		int expireType =CommonUtil.parseInt(leaderBoardConfig.getExpireType());
		//TopN的n  排序规则
		int n=CommonUtil.parseInt(leaderBoardConfig.getLimitRows());
		log.debug("N");
		log.debug(n); 
		//计算过期时间
		int thisNowTime=CommonUtil.getNowTimeStamp();
		log.debug("thisNowTime");
		log.debug(thisNowTime);
		int expireTime;
		expireTime=CommonUtil.expireTimeByExpireType(thisNowTime, expireType);
		log.debug("expireTime");
		log.debug(expireTime);
		//是否topN,取决于什么   limitrows=N, score_partion=-1
		//2017.2.9
		int rdCount=0;
		double lastScore=0.0;
		int lastMember=0;
		String topNKey=gid+"_"+boardid+"_topN";
			//获得当前缓存数据的数量
			rdCount=CommonUtil.parseInt(redisRepository.operateZsetCard(topNKey));
			log.debug("rdCount");
			log.debug(rdCount);
		
			if(rdCount<n)
			{
				//添加
				log.debug("topN有序集合增加");
				redisRepository.operateZsetAdd(topNKey,uid+"", score);
				//设置过期时间
				
			}
			else
			{
				//和最小的比较，如果大于等于，判断是否已存在，若已存在，更新，若不存在，替换
				Set<String> set=redisRepository.operateZsetRevrange(topNKey, -1, -1);
				
				Iterator<String> it = set.iterator();  
				while (it.hasNext()) {  
					lastMember=CommonUtil.parseInt(it.next()); 
				  System.out.println(lastMember);  
				}  
 				
				log.debug("lastMember");
				log.debug(lastMember);
			
				lastScore= redisRepository.operateZsetScore(topNKey, lastMember+""); 
				
				log.debug(lastScore);
				log.debug("lastScore");
				if(score>=lastScore)
				{
					//如果存在，删自己
					if(redisRepository.operateZsetScore(topNKey,uid+"")!=null)
					{
						log.debug("已存在的uid");
					
						redisRepository.operateZsetRemove(topNKey, uid+"");
					}
					else{
						log.debug("大于最小的分值，，删除替换，在落地，前面已经落地");
						redisRepository.operateZsetRemove(topNKey, lastMember+"");
						log.debug("删除最后一个");
					}
					redisRepository.operateZsetAdd(topNKey, uid+"",CommonUtil.parseInt(score) );
					log.debug("添加新的一个");
				}
				else{
					//如果小于，落地,前面已经落地
					log.debug("小于最小的分值，落地");
				}	
			}
			if(expireType!=0)
			{
				if(rdCount==1)
				{
					redisRepository.setExpire(topNKey, expireTime);
					log.debug("topN设置过期时间");
				}
			}
			return ApiResultCode.OK;
		
	}

	@Override
	public int totalRank(int uid,int gid,int boardid,int score,LeaderBoardConfig leaderBoardConfig) {
		//过期类型
				int expireType =CommonUtil.parseInt(leaderBoardConfig.getExpireType());
				//分区大小
				int score_section =CommonUtil.parseInt(leaderBoardConfig.getScorePartion());
			
				//计算过期时间
				int thisNowTime=CommonUtil.getNowTimeStamp();
				log.debug("thisNowTime");
				log.debug(thisNowTime);
				int expireTime;
				expireTime=CommonUtil.expireTimeByExpireType(thisNowTime, expireType);
				log.debug("expireTime");
				log.debug(expireTime);
				//获得分区
				int storeResult=iUserInfoMapService.storeRedisScoreKey(uid, gid, boardid, score_section, score,expireTime);
				log.debug("topScore");
				
				iUserInfoMapService.checkRedisCachedKey(uid, gid, boardid, score,expireType);
				if(storeResult<0)
				{
					return ApiResultCode.NORMAL_ERROR;
				}
				
				return ApiResultCode.OK;
	}

	@Override
	public int topNAndTotalRank(int uid,int gid,int boardid,int score,LeaderBoardConfig leaderBoardConfig) {

	
		topNRank( uid, gid, boardid, score, leaderBoardConfig);
		
		//是否topN,取决于什么   limitrows=N, score_partion=-1
		//2017.2.9
		totalRank(uid, gid, boardid, score, leaderBoardConfig);
			return ApiResultCode.OK;
		
	}

}
