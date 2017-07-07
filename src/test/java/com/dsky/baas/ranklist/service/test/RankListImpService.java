package com.dsky.baas.ranklist.service.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dsky.baas.ranklist.RankListSpringBoot;
import com.dsky.baas.ranklist.service.ILeaderBoardConfigService;
import com.dsky.baas.ranklist.service.IRankListService;

@RunWith(SpringJUnit4ClassRunner.class)
//@ActiveProfiles(profiles = "test")
@SpringBootTest
@Import(RankListSpringBoot.class)
public class RankListImpService {

	@Autowired
	private ILeaderBoardConfigService leaderBoardConfigImpService;
	@Autowired
	private IRankListService iRankListService;
	@Test
	public void test1(){
//		iRankListService.topNRank(1, 11076, 11, 100, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(2, 11076, 11, 200, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(3, 11076, 11, 120, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(4, 11076, 11, 250, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(5, 11076, 11, 330, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(6, 11076, 11, 600, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(7, 11076, 11, 500, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(8, 11076, 11, 700, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(9, 11076, 11, 800, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(10, 11076, 11, 450, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(11, 11076, 11, 998, leaderBoardConfigImpService.getConfig(11076, 11));
//		iRankListService.topNRank(12, 11076, 11, 998, leaderBoardConfigImpService.getConfig(11076, 11));
	
		
		
	}

	@Test
	public void test2(){
		
		iRankListService.topNAndTotalRank(1, 11076, 11, 100, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(2, 11076, 11, 200, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(3, 11076, 11, 120, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(4, 11076, 11, 250, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(5, 11076, 11, 330, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(6, 11076, 11, 600, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(7, 11076, 11, 500, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(8, 11076, 11, 700, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(9, 11076, 11, 800, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(10, 11076, 11, 450, leaderBoardConfigImpService.getConfig(11076, 11));
		iRankListService.topNAndTotalRank(11, 11076, 11, 998, leaderBoardConfigImpService.getConfig(11076, 11));
		
//		iRankListService.topNRank(12, 11076, 11, 1000, leaderBoardConfigImpService.getConfig(11076, 11));
	}
	

	//redis并发读写
	//读-------->写
	//   读---------写
	@Test
	public void test3(){
		
		
	}
	
}
