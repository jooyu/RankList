package com.dsky.baas.ranklist.service;

import java.util.List;

import com.dsky.baas.ranklist.model.LeaderBoardConfig;



public interface ILeaderBoardConfigService{

	public LeaderBoardConfig getConfigFromRedis(int gid,int boardid);
//	public void initLeaderBoardConfig(int gid,int boardid);
	public void SetConfigToRedis(LeaderBoardConfig leaderBoardConfig);
//	public LeaderBoardConfig getConfigFromMysql(int gameid,int boardid);
//	public LeaderBoardConfig getConfig(int gameid,int boardid);
//	public void setBackendConfig(LeaderBoardConfig leaderBoardConfig);
//	public List<LeaderBoardConfig> selectAllLeaderBoardConfig();
	
}

