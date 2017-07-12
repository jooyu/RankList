package com.dsky.baas.ranklist.service.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dsky.baas.ranklist.RankListSpringBoot;
import com.dsky.baas.ranklist.service.IUserInfoMapService;


@RunWith(SpringJUnit4ClassRunner.class)
//@ActiveProfiles(profiles = "test")
@SpringBootTest
@Import(RankListSpringBoot.class)
public class UserInfoMapServcieImpTest {

@Autowired
private IUserInfoMapService iUserInfoMapService;


    @Test
    public void test1(){
//    	iUserInfoMapService.getUserInfo(123456, 11292, 1);
//    	System.out.println(iUserInfoMapService.getUserID(123456, 11292, 1));
    	System.out.println(iUserInfoMapService.getUserInfo(123456, 11292, 1));
    }
    
    

}

