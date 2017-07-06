package com.dsky.baas.ranklist.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.dsky.baas.ranklist.config.RedisRepository;
import com.dsky.baas.ranklist.lib.ApiResultCode;
import com.dsky.baas.ranklist.lib.ApiResultObject;
import com.dsky.baas.ranklist.lib.ApiResultPacker;
import com.dsky.baas.ranklist.model.LeaderBoardConfig;
import com.dsky.baas.ranklist.service.ILeaderBoardConfigService;
import com.dsky.baas.ranklist.service.IUserInfoMapService;
import com.dsky.baas.ranklist.util.CommonUtil;
import com.dsky.baas.ranklist.util.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping("/v1/rank")
/**
 * 
 * @author eaves.zhu
 * 通过redis实现数据分区
 *
 */
public class RankListController {
	 private static final ObjectMapper objectMapper = ObjectMapperFactory.getDefaultObjectMapper();
	private final Logger log = Logger.getLogger(RankListController.class);
	@Autowired
	private ILeaderBoardConfigService iLeaderBoardConfigService;
	@Autowired
	private IUserInfoMapService iUserInfoMapService;
	@Autowired
	private RedisRepository redisRepository;

	@RequestMapping("/submit_rank_data")
	public @ResponseBody ApiResultObject submitRankData(HttpServletRequest req) {
		// 通过openapi鉴权之后
		Map<String, String> cookie = CommonUtil.parseHeaderCookie(req);
		if (cookie == null) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.AUTH_FAIL, "鉴权失败");
		}
		log.debug("submit_rank_data controller cookieMap");
		log.debug(JSON.toJSON(cookie));
		int uid;
		int gid;
		int boardid;
		int score;
		try {
			uid = CommonUtil.parseInt(cookie.get("uid"));
			log.debug("uid");
			log.debug(uid);
		} catch (Exception e) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_PLAYER_ID, "不能缺少玩家ID参数");
		}
		try {
			gid = CommonUtil.parseInt(cookie.get("gid"));
			log.debug("gid");
			log.debug(gid);
		} catch (Exception e) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_GAME_ID, "不能缺少游戏ID参数");
		}
		boardid = CommonUtil.parseInt(req.getParameter("boardid"));
		log.debug("boardid");
		log.debug(boardid);
		if (boardid <= 0) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_BOARD_ID, "不能缺少游戏排行ID参数");
		}
		score = CommonUtil.parseInt(req.getParameter("score"));
		log.debug("score");
		log.debug(score);
		if (boardid < 0) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_SCORE_ID, "不能缺少游戏排行分数");
		}

		// 数据分块存储redis，最后写入，返回排序系列uid集合
		// 步骤1.读取redis配置
		// 步骤2.读取是否存在redis，
		// 存在，更新数据，同时数据存储mysql
		// 不存在，分区存储redis，同时落地mysql
		// getRedisConfig();
		// checkRedisKey()
		// setRedisKey
		// 存在 update
		// 不存在 insert
		//获得过期时间----------------
		LeaderBoardConfig leaderBoardConfig=iLeaderBoardConfigService.getConfigFromRedis(gid, boardid);
		//过期类型
		int expireType =CommonUtil.parseInt(leaderBoardConfig.getExpireType());
		//分区大小
		int score_section =CommonUtil.parseInt(leaderBoardConfig.getScorePartion());
		//TopN的n
		int n=CommonUtil.parseInt(leaderBoardConfig.getLimitRows());
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
			return ApiResultPacker.packToApiResultObject(ApiResultCode.NORMAL_ERROR, "存储失败");
		}
		//是否topN
		//2017.2.9
		int rdCount=0;
		double lastScore=0.0;
		int lastMember=0;
		String topNKey=gid+"_"+boardid+"_topN";
			//获得当前缓存数据的数量
			rdCount=CommonUtil.parseInt(redisRepository.operateZsetCard(topNKey));
			log.debug("rdCount");
			log.debug(rdCount);
		
			if(rdCount<=n)
			{
				//添加
				log.debug("topN有序集合增加");
				redisRepository.operateZsetAdd(topNKey,uid+"", score);
				//设置过期时间
				
			}
			else
			{
				//和最小的比较，如果大与等于，替换
				Set<String> set=redisRepository.operateZsetRevrange(topNKey, -1, -1);
				Iterator<String> it = set.iterator();  
				while (it.hasNext()) {  
					lastMember=CommonUtil.parseInt( it.next()); 
				  System.out.println(lastMember);  
				}  
 				
				log.debug("lastMember");
				log.debug(lastMember);
			
				lastScore= redisRepository.operateZsetScore(topNKey, lastMember+""); 
				
				log.debug(lastScore);
				log.debug("lastScore");
				if(score>=lastScore)
				{
					log.debug("大于最小的分值，，删除替换，在落地，前面已经落地");
					redisRepository.operateZsetRemove(topNKey, lastMember+"");
					log.debug("删除最后一个");
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
			
		return ApiResultPacker.packToApiResultObject(ApiResultCode.OK, "成功");

	}

	
	
	//获得分区数据所在的位置
	@RequestMapping("/get_data_location")
	public @ResponseBody ApiResultObject getDataLocation(HttpServletRequest req) {
		// 通过openapi鉴权之后
		Map<String, String> cookie = CommonUtil.parseHeaderCookie(req);
		if (cookie == null) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.AUTH_FAIL, "鉴权失败");
		}
		log.debug("get_data_location controller cookieMap");
		log.debug(JSON.toJSON(cookie));
		int uid;
		int gid;
		int boardid;
		try {
			uid = CommonUtil.parseInt(cookie.get("uid"));
			log.debug("uid");
			log.debug(uid);
		} catch (Exception e) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_PLAYER_ID, "不能缺少玩家ID参数");
		}
		try {
			gid = CommonUtil.parseInt(cookie.get("gid"));
			log.debug("gid");
			log.debug(gid);
		} catch (Exception e) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_GAME_ID, "不能缺少游戏ID参数");
		}
		boardid = CommonUtil.parseInt(req.getParameter("boardid"));
		log.debug("boardid");
		log.debug(boardid);
		if (boardid <= 0) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_BOARD_ID, "不能缺少游戏排行ID参数");
		}
		// 数据分块存储redis，最后写入，返回排序系列uid集合，在redis操作
		// 步骤1.count当前分区之前分区位置
		// 步骤2.排序当前分值所在分段位置
		// 步骤3.返回当前所在位置
		//获得分区
		LeaderBoardConfig leaderBoardConfig=iLeaderBoardConfigService.getConfigFromRedis(gid, boardid);
		int score_section =
				CommonUtil.parseInt(leaderBoardConfig.getScorePartion()
						);// 假设获得分区 TODO
		int score=iUserInfoMapService.getScoreByUidAndBoardid(uid, gid, boardid);
		log.debug("score");
		log.debug(score);
		if(score<0)
		{
			return ApiResultPacker.packToApiResultObject(ApiResultCode.PLAYER_DO_NOT_EXISTS,"玩家不存在");
		}
		int topScore=iUserInfoMapService.getTopScoreFromRedis(uid, gid, boardid);
		log.debug("topScore");
		log.debug(topScore);
		int topPartion=topScore/score_section;
		log.debug("topPartion");
		log.debug(topPartion);
		String rankKey=gid+"_"+boardid+"_";
		int partion=score/score_section;
		log.debug("partion");
		log.debug(partion);
		
//		if(redisRepository.get(rankKey)==null)
//		{
//			redisRepository.set(key, value);
//		}
		
		int redisCount=0;
		int location=0;
	
			for(int i=partion+1;i<=topPartion;i++)
			{
				redisCount+=redisRepository.operateZsetCard(rankKey+i);
				log.debug("redisCount");
				log.debug(redisCount);
			}
//			log.debug("rank");
//			log.debug(rdsClient.zrevrank(rankKey+partion, uid+""));
			log.debug(rankKey+partion);
			location=redisCount+CommonUtil.parseInt(redisRepository.operateZsetCard(rankKey+partion))-CommonUtil.parseInt(redisRepository.operateZsetRank(rankKey+partion, uid+""));//CommonUtil.parseInt(rdsClient.zrevrank(rankKey+partion, uid+"")+1)+
//			log.debug(CommonUtil.parseInt(redisRepository.operateZsetRank(rankKey+partion, uid+"")));
			log.debug("location");
			log.debug(location);
			
	

		 ObjectNode node = objectMapper.createObjectNode();  
		 node.put("uid", uid);
		
		 node.put("boardid", boardid);
		 node.put("location", location);
		return ApiResultPacker.packToApiResultObject(node);

	}

	//获得topN的数据
	
	
	
	@RequestMapping("/get_topN_rank")
	public @ResponseBody ApiResultObject getRankData(HttpServletRequest req) {
		// 通过openapi鉴权之后
		Map<String, String> cookie = CommonUtil.parseHeaderCookie(req);
		int pageSize = CommonUtil.parseInt(CommonUtil.pickValue(
				req.getParameter("page_size"), 10));
		int pageNum = CommonUtil.parseInt(CommonUtil.pickValue(
				req.getParameter("page_number"), 1));
		log.debug("pageSize");
		log.debug(pageSize);
		log.debug("pageNum");
		log.debug(pageNum);
		if (cookie == null) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.AUTH_FAIL, "鉴权失败");
		}
		log.debug("get_rank_data controller cookieMap");
		log.debug(JSON.toJSON(cookie));
		int uid;
		int gid;
		int boardid;
		Map<String, Object> resData = new HashMap<String, Object>();
		try {
			uid = CommonUtil.parseInt(cookie.get("uid"));
			log.debug("uid");
			log.debug(uid);
		} catch (Exception e) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_PLAYER_ID, "不能缺少玩家ID参数");
		}
		try {
			gid = CommonUtil.parseInt(cookie.get("gid"));
			log.debug("gid");
			log.debug(gid);
		} catch (Exception e) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_GAME_ID, "不能缺少游戏ID参数");
		}

		// 数据分块存储redis，最后写入，返回排序系列uid集合
		//排行数据只返回最高分段的数据
		boardid = CommonUtil.parseInt(req.getParameter("boardid"));
		log.debug("boardid");
		log.debug(boardid);
		if (boardid <= 0) {
			return ApiResultPacker.packToApiResultObject(
					ApiResultCode.MISS_REQUIRE_PARAM_BOARD_ID, "不能缺少游戏排行ID参数");
		}
		
		//2017.2.9
		int topScore=iUserInfoMapService.getTopScoreFromRedis(uid, gid, boardid);
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

		resData.put("sort_rule", "desc");
		resData.put("uids", Uids);
		
		
	
		return ApiResultPacker.packToApiResultObject(node);

	}

}
