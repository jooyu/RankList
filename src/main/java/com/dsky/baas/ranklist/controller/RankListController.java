package com.dsky.baas.ranklist.controller;

import java.util.HashMap;
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
import com.dsky.baas.ranklist.service.IRankListService;
import com.dsky.baas.ranklist.service.IUserInfoMapService;
import com.dsky.baas.ranklist.util.CommonUtil;
import com.dsky.baas.ranklist.util.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
	private IRankListService iRankListService;
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

		//选择排行类型
		LeaderBoardConfig leaderBoardConfig=iLeaderBoardConfigService.getConfigFromRedis(gid, boardid);
		if(leaderBoardConfig==null)
		{
			return ApiResultPacker.packToApiResultObject(ApiResultCode.CONFIG_NOT_EXISTS, "配置不存在");
		}
		int limitRows=leaderBoardConfig.getLimitRows();
		log.debug("limitRows");
		log.debug(limitRows);
		int scorePartion=leaderBoardConfig.getScorePartion();
		log.debug("scorePartion");
		log.debug(scorePartion);
		
		if(limitRows>0 && scorePartion<0)
		{
			//topN
			return ApiResultPacker.packToApiResultObject(iRankListService.topNRank(uid, gid, boardid, score, leaderBoardConfig), "");
			
		}
		else if(limitRows>0 && scorePartion>0)
		{
			//topN+总排
			return ApiResultPacker.packToApiResultObject(iRankListService.topNAndTotalRank(uid, gid, boardid, score, leaderBoardConfig), "");
			
		}
		else if(limitRows<0 && scorePartion>0)
		{
			//总排
			return ApiResultPacker.packToApiResultObject(iRankListService.totalRank(uid, gid, boardid, score, leaderBoardConfig), "");
			
		}
		else
		{
			//非法
			return ApiResultPacker.packToApiResultObject(ApiResultCode.CONFIG_IS_ILLEGL, "配置不合法");
		}
		

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
		if(leaderBoardConfig==null)
		{
			return ApiResultPacker.packToApiResultObject(ApiResultCode.CONFIG_NOT_EXISTS, "配置不存在");
		}
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
		return ApiResultPacker.packToApiResultObject(iUserInfoMapService.getUserInfo(uid, gid, boardid));

	}
	
	
	@RequestMapping("/get_topN_user")
	public @ResponseBody ApiResultObject getUserData(HttpServletRequest req) {
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
		
		
		
		return null;
	}
}
