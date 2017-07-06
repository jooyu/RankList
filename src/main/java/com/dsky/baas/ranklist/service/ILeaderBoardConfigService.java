package com.dsky.baas.ranklist.service;

import com.dsky.baas.ranklist.model.LeaderBoardConfig;



public interface ILeaderBoardConfigService{

	public LeaderBoardConfig getConfigFromRedis(int gid,int boardid);
	public void initLeaderBoardConfig();
	public void SetConfigToRedis(LeaderBoardConfig leaderBoardConfig);
	public LeaderBoardConfig getConfigFromMysql(int gameid,int boardid);
}

