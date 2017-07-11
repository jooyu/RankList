package com.dsky.baas.ranklist.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	



	
//   public LeaderBoardConfig getConfigFromMysql(int gameid,int boardid){
//	   LeaderBoardConfig leaderBoardConfig=new LeaderBoardConfig();
//	   leaderBoardConfig.setGameId(gameid);
//	   leaderBoardConfig.setLeaderboardid(boardid);
//	   leaderBoardConfig.setCreateTime(CommonUtil.getNowTimeStamp());
//	return leaderBoardConfigMapper.selectByGidAndBoardId(leaderBoardConfig);
//	   
//   }
//
//
//
//   
//   
//@Override
//public LeaderBoardConfig getConfig(int gameid, int boardid) {
//	//1.redis为空,读mysql
//	//2.redis空，mysql空，提示无配置
//	LeaderBoardConfig leaderBoardConfig=null;
//	if(getConfigFromRedis(gameid,boardid)==null)
//	{
//		
//		if(getConfigFromMysql(gameid,boardid)==null)
//		{
//			return null;
//		}
//		else{
//			
//			leaderBoardConfig=getConfigFromMysql(gameid,boardid);
//			//写redis
//			SetConfigToRedis(leaderBoardConfig);
//			
//		}
//	}
//	else
//	{
//		leaderBoardConfig= getConfigFromRedis(gameid,boardid);
//	}
//	return leaderBoardConfig;
//	
//}
//
///**
// * 1.set redis  更新和修改
// * 2.set mysql
// */
//@Override
//public void setBackendConfig(LeaderBoardConfig leaderBoardConfig) {
//		SetConfigToRedis(leaderBoardConfig);
//		if(leaderBoardConfigMapper.selectByGidAndBoardId(leaderBoardConfig)==null)
//		{
//			leaderBoardConfigMapper.insertSelective(leaderBoardConfig);
//		}
//		else
//		{
//			//update
//			leaderBoardConfigMapper.updateByGidAndBoardId(leaderBoardConfig);
//		}
//		
//}
//
//
//
//
//
//
//
//
//
//
//
//@Override
//public List<LeaderBoardConfig> selectAllLeaderBoardConfig() {
//	
//	return leaderBoardConfigMapper.selectAllLeaderBoardConfig();
//}



}
