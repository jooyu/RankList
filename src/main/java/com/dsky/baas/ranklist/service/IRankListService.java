package com.dsky.baas.ranklist.service;

import com.dsky.baas.ranklist.model.LeaderBoardConfig;

public interface IRankListService {

	public int topNRank(int uid,int gid,int boardid,int score,LeaderBoardConfig leaderBoardConfig);
	public int totalRank(int uid,int gid,int boardid,int score,LeaderBoardConfig leaderBoardConfig);
	public int topNAndTotalRank(int uid,int gid,int boardid,int score,LeaderBoardConfig leaderBoardConfig);
}
