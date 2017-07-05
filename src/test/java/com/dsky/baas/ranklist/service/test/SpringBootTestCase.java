package com.dsky.baas.ranklist.service.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dsky.baas.ranklist.RankListSpringBoot;


@RunWith(SpringJUnit4ClassRunner.class)
//@ActiveProfiles(profiles = "test")
@SpringBootTest
@Import(RankListSpringBoot.class)
public class SpringBootTestCase {


    @Test
    public void test1(){
    	assertEquals(1,1);
    }
    
    

}

