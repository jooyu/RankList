package com.dsky.baas.ranklist.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.dsky.baas.ranklist.config.RedisRepository;
import com.dsky.baas.ranklist.lib.ApiResultCode;
import com.dsky.baas.ranklist.lib.ApiResultObject;
import com.dsky.baas.ranklist.lib.ApiResultPacker;
import com.dsky.baas.ranklist.service.IUserInfoMapService;
import com.dsky.baas.ranklist.service.IWarningReporterService;
import com.dsky.baas.ranklist.util.CommonUtil;

@Controller
@RequestMapping("/v1/rank")
/**
 * 
 * @author eaves.zhu
 * 通过redis实现数据分区
 *
 */
public class RankListController {
	private final Logger log = Logger.getLogger(RankListController.class);
//	@Autowired
//	private ILeaderBoardConfigService iLeaderBoardConfigService;
	@Autowired
	private IUserInfoMapService iUserInfoMapService;
	@Autowired
	private IWarningReporterService iWarningReporterService;
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

		//获得分区
		int score_section =100;
//				CommonUtil.parseInt(iLeaderBoardConfigService
//				.getLeaderBoardConfig());// 假设获得分区 TODO

		int storeResult=iUserInfoMapService.storeRedisScoreKey(uid, gid, boardid, score_section, score);
		
		if(storeResult<0)
		{
			return ApiResultPacker.packToApiResultObject(ApiResultCode.NORMAL_ERROR, "存储失败");
		}
		return ApiResultPacker.packToApiResultObject(ApiResultCode.OK, "成功");

	}

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
		int score_section =100;
//				CommonUtil.parseInt(iLeaderBoardConfigService
//						.getLeaderBoardConfig());// 假设获得分区 TODO
		int score=iUserInfoMapService.getScoreByUidAndBoardid(uid, gid, boardid);
		int topScore=iUserInfoMapService.getTopScoreFromRedis(uid, gid, boardid);
		int topPartion=topScore/score_section;
		log.debug("score");
		log.debug(score);
		String rankKey=gid+"_"+boardid+"_";
		int partion=score/score_section;
		
		int redisCount=0;
		int location=0;
	
		
			for(int i=partion;i<topPartion;i++)
			{
				redisCount+=redisRepository.operateZsetCount(rankKey+i, i*score_section, (i+1)*score_section);
				log.debug("redisCount");
				log.debug(redisCount);
			}
			location=redisRepository.operateZsetRevrank(rankKey+partion, uid+"")+redisCount;
			log.debug("location");
			log.debug(location);

		Map<String, Object> resData = new HashMap<String, Object>();
		resData.put("uid", uid);
		resData.put("boardid", boardid);
		resData.put("location", location);
		return ApiResultPacker.packToApiResultObject(resData);

	}

	@RequestMapping("/get_topN_rank")
	public @ResponseBody ApiResultObject getRankData(HttpServletRequest req) {
		// 通过openapi鉴权之后
		Map<String, String> cookie = CommonUtil.parseHeaderCookie(req);
		int resultSize = CommonUtil.parseInt(CommonUtil.pickValue(
				req.getParameter("result_size"), 10));
		int pageNum = CommonUtil.parseInt(CommonUtil.pickValue(
				req.getParameter("page_number"), 1));
		log.debug("resultSize");
		log.debug(resultSize);
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
		//TODO
		int score_section = 100;
//				CommonUtil.parseInt(iLeaderBoardConfigService
//				.getLeaderBoardConfig());// 假设获得分区 TODO
		int n=100;//前多少
		int topScore=iUserInfoMapService.getTopScoreFromRedis(uid, gid, boardid);
		int topPartion=topScore/score_section;
		String rankData=gid+"_"+boardid+"_"+topPartion;
		
		
	
		List<String> result=null;
		List<String> Uids=null;
		
			String topNKey=gid+"_"+boardid+"_topN";
		
			Set<String> topNResult=redisRepository.operateZsetRevrange(topNKey, 0, topScore);
			Iterator<String> it = topNResult.iterator();
			while(it.hasNext())
			{
				if(result.size()>n)
				{
					break;
				}
				result.add(it.next());
			}//判断是否有下一个
			
			//取出topN中所需的个数
			for(int i=(pageNum-1)*resultSize;i<pageNum*resultSize;i++)
			{
				Uids.add(result.get(i));
			}
		
		Map<String, Object> resData = new HashMap<String, Object>();

		resData.put("Uids", Uids);
		return ApiResultPacker.packToApiResultObject(resData);

	}

}
