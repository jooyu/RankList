package com.dsky.baas.ranklist.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dsky.baas.ranklist.service.ILeaderBoardConfigService;


@Configuration
public class LeaderBoardInitConfig implements InitializingBean{

	@Autowired
	private ILeaderBoardConfigService iLeaderBoardConfigService;
	@Override
	public void afterPropertiesSet() throws Exception {
	System.out.println("Config~~~~");
		iLeaderBoardConfigService.initLeaderBoardConfig();
		
	}



}
