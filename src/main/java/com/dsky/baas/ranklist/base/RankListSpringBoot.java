package com.dsky.baas.ranklist.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;





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

@SpringBootApplication
@ComponentScan(basePackages = "com.dsky.baas.ranklist")
public class RankListSpringBoot {
	public static void main(String[] args) {
		SpringApplication.run(RankListSpringBoot.class, args);

	}

}


