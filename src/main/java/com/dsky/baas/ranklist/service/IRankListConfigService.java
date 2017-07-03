package com.dsky.baas.ranklist.service;

import com.dsky.baas.ranklist.persistence.couchbase.RankListConfig;


/**
 * 
 * @author yu joo
 * yujoo2008@gmail.com
 * 2017年7月3日  下午5:40:49
 * 往couchbase里面写数据
 */
public interface IRankListConfigService {

	//添加配置
	public void addRankListConfig(RankListConfig rankListConfig);
	
	
	
}
