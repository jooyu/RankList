package com.dsky.baas.ranklist;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.dsky.baas.ranklist.service.ILeaderBoardConfigService;





//注解有顺序 ，按这个顺序
//@Configuration
//@ComponentScan(basePackages = {"com.zy.test"})
//@EnableAutoConfiguration
//@EnableCaching

/**
 *  *EnableAspectJAutoProxy开启切面编程
 *ComponentScan指定包之后，SpringBoot会自动扫描该包下面的功能，如Controller，Model，Aspect功能
 *org.sun.spring是我的包名，各位看客自行修改成自己的package name
 * @author eaves.zhu
 * 
 */

@EnableSpringConfigured
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@SpringBootApplication
@ComponentScan(basePackages = "com.dsky.baas.ranklist")
@MapperScan(basePackages="com.dsky.baas.ranklist.dao")
public class RankListSpringBoot {
	
	public static void main(String[] args) {
		SpringApplication.run(RankListSpringBoot.class, args);
		//初始化先判断redis的配置是否存在
	}
}


