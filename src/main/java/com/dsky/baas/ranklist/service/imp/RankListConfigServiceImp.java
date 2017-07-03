package com.dsky.baas.ranklist.service.imp;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.dsky.baas.ranklist.config.CouchbaseRankConfig;
import com.dsky.baas.ranklist.persistence.couchbase.RankListConfig;
import com.dsky.baas.ranklist.service.IRankListConfigService;

public class RankListConfigServiceImp implements IRankListConfigService{

	
	@Autowired
	private CouchbaseRankConfig couchbaseRankConfig;
	
	@Override
	public void addRankListConfig(RankListConfig rankListConfig) {
	
		try {
			couchbaseRankConfig.rankCouchbaseClient().incr("boardid:", 1, 1);
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}

}
