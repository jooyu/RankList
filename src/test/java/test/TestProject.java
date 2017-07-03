//package test;
//
//import static org.junit.Assert.*;
//
//import org.apache.log4j.Logger;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.dsky.baas.pointsservice.service.IPointsPoolGameTotalService;
//import com.dsky.baas.pointsservice.service.IPointsPoolService;
//
//public class TestProject extends AbstractJUnit {
//
//
//	private Logger log = Logger.getLogger(TestProject.class);
//	@Autowired
//	private IPointsPoolService  iPointsPoolService;
//	@Autowired
//	private IPointsPoolGameTotalService iPointsPoolGameTotalService;
//    
//
//
//	@Test
//	public void test() {
//		log.debug("start test");
//		assertEquals(iPointsPoolService.getNowPersonalPoints(211,10909,39 ), 1);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(211,10909,39 ), 11);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(2211,10909,39 ), 1);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(0,0,0 ), 1);
//		log.debug("stop test");
//	}
//	
//	@Test
//	public void test1() {
//		log.debug("start test");
//		assertEquals(iPointsPoolGameTotalService.getNowPersonalPoints(211,10909 ), 1);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(211,10909,39 ), 11);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(2211,10909,39 ), 1);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(0,0,0 ), 1);
//		log.debug("stop test");
//	}
//	
//	@Test
//	public void test2() {
//		log.debug("start test");
//		assertEquals(iPointsPoolGameTotalService.getNowPersonalPoints(211,10909 ), 1);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(211,10909,39 ), 11);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(2211,10909,39 ), 1);
//		//assertEquals(iPointsPoolService.getNowPersonalPoints(0,0,0 ), 1);
//		log.debug("stop test");
//	}
//}
