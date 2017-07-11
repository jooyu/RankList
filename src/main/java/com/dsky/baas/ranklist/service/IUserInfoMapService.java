package com.dsky.baas.ranklist.service;

import java.util.Set;

import com.dsky.baas.ranklist.model.UserInfoMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public interface IUserInfoMapService{
	public int  insertUserInfoMap(int uid,int boardid,int score,int create_time,int expireTime);
//	public List<UserInfoMap>  selectUserInfoMapByUid(int uid);
//	public List<UserInfoMap>  selectUserInfoMapByBoardid(int boardid);
//	public List<UserInfoMap>  selectUserInfoMapByScore(int score);
	public UserInfoMap  selectUserInfoMapByUidAndBoardid(int uid,int boardid);
//	public List<UserInfoMap>  selectUserInfoMapByUidAndScore(int uid,int score);
//	public List<UserInfoMap>  selectUserInfoMapByScoreAndBoardid(int boardid,int score);
	public int updateUserInfoMap(int uid,int boardid,int score,int create_time,int expireTime);
	//redis的key是否存在
	public int  checkRedisCachedKey(int uid,int gid,int boardid,int score,int expireType);
	
	//查询redis的对应最高分
	public int 	getTopScoreFromRedis(int uid,int gid,int boardid);
	
	//存在并淘汰redis的分区key
	public int storeRedisScoreKey(int uid,int gid,int boardid, int scoreSection,int score,int expireTime);
	

	public int getScoreByUidAndBoardid(int uid,int gid,int boardid);
	
	public JsonNode getUserID(int uid,int gid,int boardid);
	
	public JsonNode getUserInfo(int uid,int gid,int boardid);
	
}
