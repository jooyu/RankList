package com.dsky.baas.ranklist.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dsky.baas.ranklist.config.RedisRepository;
import com.dsky.baas.ranklist.controller.RankListController;
import com.dsky.baas.ranklist.dao.LeaderBoardConfigMapper;
import com.dsky.baas.ranklist.model.LeaderBoardConfig;
import com.dsky.baas.ranklist.service.ILeaderBoardConfigService;
import com.dsky.baas.ranklist.util.CommonUtil;
@Service
public class LeaderBoardConfigServiceImp implements ILeaderBoardConfigService {
	private final Logger log = Logger.getLogger(RankListController.class);
	@Autowired
	private LeaderBoardConfigMapper leaderBoardConfigMapper;

//	@Value("${gameid}")
//	private String gameid;
//	@Value("${boardid}")
//	private String leaderboardid;
	@Autowired
	private RedisRepository redisRepository;
	@Override
	public LeaderBoardConfig getConfigFromRedis(int gid,int boardid) {
		
		LeaderBoardConfig leaderBoardConfig = null;
		String leaderConfigKey=gid+"_"+boardid+"_config";
	
		String leaderString=redisRepository.get(leaderConfigKey);
		leaderBoardConfig= JSON.parseObject(leaderString,
					LeaderBoardConfig.class);
		log.debug("获取redis存储配置信息");
		
		return leaderBoardConfig;
	}
	
	
	public void  SetConfigToRedis(LeaderBoardConfig leaderBoardConfig) {
		
		
		String leaderConfigKey=leaderBoardConfig.getGameId()+"_"+leaderBoardConfig.getLeaderboardid()+"_config";
		
		redisRepository.set(leaderConfigKey,JSON.toJSONString(leaderBoardConfig));

		log.debug("获取redis存储配置信息");
		
		
	}
	



//	public void  initLeaderBoardConfig(int gid,int boardid)
//	{
////		int gid=CommonUtil.parseInt(gameid);
////		int boardid=CommonUtil.parseInt(leaderboardid);
////		//启动时从mysql读取配置，没有就用默认配置
//		LeaderBoardConfig LeaderBoardConfigRedis=getConfigFromRedis(gid,boardid);
//		LeaderBoardConfig LeaderBoardConfigMysql=getConfigFromMysql(gid,boardid);
//		if(LeaderBoardConfigRedis==null)
//		{
//			//redis 为空
//			if(LeaderBoardConfigMysql==null){
//				//mysql为空
//				LeaderBoardConfig record=new LeaderBoardConfig();
//				record.setGameId(gid);
//				record.setLeaderboardid(boardid);
//				record.setCreateTime(CommonUtil.getNowTimeStamp());
//				record.setLimitRows(30);
//				record.setScorePartion(100);
//				record.setExpireType("1");
//				record.setSortRule("desc");
//				leaderBoardConfigMapper.insertSelective(record);
//				//写redis
//				SetConfigToRedis(record);
//			}
//			else
//			{
//				SetConfigToRedis(getConfigFromMysql(gid,boardid));
//			}
//			
//		} 
//		
//	}
	
   public LeaderBoardConfig getConfigFromMysql(int gameid,int boardid){
	   LeaderBoardConfig leaderBoardConfig=new LeaderBoardConfig();
	   leaderBoardConfig.setGameId(gameid);
	   leaderBoardConfig.setLeaderboardid(boardid);
	   leaderBoardConfig.setCreateTime(CommonUtil.getNowTimeStamp());
	return leaderBoardConfigMapper.selectByGidAndBoardId(leaderBoardConfig);
	   
   }



   
   
@Override
public LeaderBoardConfig getConfig(int gameid, int boardid) {
	//1.redis为空,读mysql
	//2.redis空，mysql空，提示无配置
	LeaderBoardConfig leaderBoardConfig=null;
	if(getConfigFromRedis(gameid,boardid)==null)
	{
		
		if(getConfigFromMysql(gameid,boardid)==null)
		{
			return null;
		}
		else{
			
			leaderBoardConfig=getConfigFromMysql(gameid,boardid);
			//写redis
			SetConfigToRedis(leaderBoardConfig);
			
		}
	}
	else
	{
		leaderBoardConfig= getConfigFromRedis(gameid,boardid);
	}
	return leaderBoardConfig;
	
}

/**
 * 1.set redis
 * 2.set mysql
 */
@Override
public void setBackendConfig(LeaderBoardConfig leaderBoardConfig) {
		SetConfigToRedis(leaderBoardConfig);
		if(leaderBoardConfigMapper.selectByGidAndBoardId(leaderBoardConfig)==null)
		{
			leaderBoardConfigMapper.insertSelective(leaderBoardConfig);
		}
		else
		{
			//update
			leaderBoardConfigMapper.updateByGidAndBoardId(leaderBoardConfig);
		}
		
}



}
