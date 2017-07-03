package com.dsky.baas.ranklist.persistence.couchbase;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RankListConfig {
	//游戏id 一个游戏一个bucket
	private int gameId;
	//榜单id 一个榜单一条记录
	private int  boardId;
	
	//TopN的N
	private int limit_rows;
	
	//分区区间  多少分分区
	private int score_section;
	
	//过期时间
	private int expire_time;
	
	//过期类型0（永久）， 1（日），2（周），3（月）
	private int expire_type;
	
	
	//排序规则  0：升序  ，1：降序
	private int sort_rule;
	
	//redis配置
	private  String redis_config;

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public int getLimit_rows() {
		return limit_rows;
	}

	public void setLimit_rows(int limit_rows) {
		this.limit_rows = limit_rows;
	}

	public int getScore_section() {
		return score_section;
	}

	public void setScore_section(int score_section) {
		this.score_section = score_section;
	}

	public int getExpire_time() {
		return expire_time;
	}

	public void setExpire_time(int expire_time) {
		this.expire_time = expire_time;
	}

	public int getExpire_type() {
		return expire_type;
	}

	public void setExpire_type(int expire_type) {
		this.expire_type = expire_type;
	}

	public int getSort_rule() {
		return sort_rule;
	}

	public void setSort_rule(int sort_rule) {
		this.sort_rule = sort_rule;
	}

	public String getRedis_config() {
		return redis_config;
	}

	public void setRedis_config(String redis_config) {
		this.redis_config = redis_config;
	}
	
	
}
