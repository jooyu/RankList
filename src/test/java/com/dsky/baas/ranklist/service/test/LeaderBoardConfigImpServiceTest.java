package com.dsky.baas.ranklist.service.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dsky.baas.ranklist.RankListSpringBoot;
import com.dsky.baas.ranklist.model.LeaderBoardConfig;
import com.dsky.baas.ranklist.service.ILeaderBoardConfigService;
import com.dsky.baas.ranklist.service.impl.LeaderBoardConfigServiceImp;


@RunWith(SpringJUnit4ClassRunner.class)
//@ActiveProfiles(profiles = "test")
@SpringBootTest
@Import(RankListSpringBoot.class)
public class LeaderBoardConfigImpServiceTest {

	@Autowired
	private ILeaderBoardConfigService leaderBoardConfigImpService;


    @Test
    public void test1(){
//    	assertEquals(1,1);
    	
    	//assertEquals(leaderBoardConfigImpService.getConfigFromMysql(11076, 11),null);
    
  // 	assertEquals(leaderBoardConfigImpService.getConfigFromRedis(11076,11),null);
 //   	leaderBoardConfigImpService.SetConfigToRedis(leaderBoardConfigImpService.getConfigFromMysql(11076, 11));
    	leaderBoardConfigImpService.initLeaderBoardConfig();
    }
    
    

}

