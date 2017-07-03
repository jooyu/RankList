package com.statsd.aspect;

import com.statsd.client.StatsdClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * This class declares the aspects for the system.  In this example is an around advice for timing the length of methods
 * marked with the Timed annotation
 */
@Order(1)
@Aspect
//AspectJ编译时织的情况
// XXX(serialx): AspectJ Compile Time Weaving 을 할 경우 @Configurable 옵션을 주지 않으면 Unit Test 에서 Autowire 가 먹지 않는다. 뭔가 설정을 미스한것 같은데...
@Configurable
// XXX(serialx): Unit Test 에서 @Component 가 안먹힘
//@Component
public class StatdPerformanceAspect {
    static Logger logger = LoggerFactory.getLogger(StatdPerformanceAspect.class);

    //注入bean
    @Autowired
    private StatsdClient statsdClient;

    /**
     * This around advice adds timing to any method annotated with the Timed annotation.
     * It binds the annotation to the parameter timedAnnotation so that the values are available at runtime.
     * Also note that the retention policy of the annotation needs to be RUNTIME.
     *
     * @param pjp             - the join point for this advice
     * @param timedAnnotation - the Timed annotation as declared on the method
     * @return
     * @throws Throwable
     */

    @Around("@annotation( timedAnnotation ) ")
    public Object processSystemRequest(final ProceedingJoinPoint pjp, Timed timedAnnotation) throws Throwable {
        try {
            long start = System.currentTimeMillis();
            Object retVal = pjp.proceed();
            long end = System.currentTimeMillis();
            long differenceMs = end - start;

            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            Method targetMethod = methodSignature.getMethod();
            //get the value of timing notes as declared in the method annotation  获得纪录方法注入的时间
            String timingNotes = timedAnnotation.timingNotes();
            //String requestInfo = param.getRequestInfo();

            /* AspectJ Compile Time Weaving 을 사용할 경우 getDeclaringClass 가 Interface 가 아니라 Class 를 돌려준다
             * 그런데 그동안 Interface.Method 로 Statsd 기록을 했으므로 이 하위호환성을 맞춰주기 위해 아래와 같이 인터페이스를 찾는다 */
            Class clazz = targetMethod.getDeclaringClass();//获得申明的类
            if (clazz.getPackage().getName().startsWith("com.devsisters.game.oven2.service")) {
                if (!clazz.isInterface()) {
                    if (clazz.getInterfaces().length > 0){
                        /* ShopServiceImpl 등의 경우 */
                        clazz = clazz.getInterfaces()[0];
                    } else if (clazz.getSuperclass() != null && clazz.getSuperclass().getInterfaces().length > 0) {
                        /* MemberServiceKakao 등의 경우 */
                        clazz = clazz.getSuperclass().getInterfaces()[0];
                    }
                }
            }

            statsdClient.increment(clazz.getName() + "." + targetMethod.getName());
            statsdClient.timing(clazz.getName() + "." + targetMethod.getName(), (int) differenceMs);
            logger.debug(clazz.getName() + "." + targetMethod.getName() + " took " + differenceMs + " ms : timing notes: " + timingNotes + " request info : ");
            return retVal;
        } catch (Throwable t) {
//            System.out.println("error occured");
            throw t;
        }

    }

}

