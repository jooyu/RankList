package com.dsky.baas.ranklist.lib;

public final class ApiResultCode {
	public static final int OK = 0;//正常返回，没有错误
	
	public static final int NORMAL_ERROR = 100; //一般错误
	
	public static final int IS_BLACKLIST = 500; //黑名单
	
	public static final int AUTH_FAIL = 1000; //鉴权失败
	
	public static final int MISS_REQUIRE_PARAM_BOARD_ID=2000;
	public static final int MISS_REQUIRE_PARAM_DEVICE_ID = 2010;
	public static final int MISS_REQUIRE_PARAM_GAME_ID = 2020;

	public static final int MISS_REQUIRE_PARAM_PLAYER_ID = 2030;
	public static final int MISS_REQUIRE_PARAM_LEVEL = 2040;
	public static final int MISS_REQUIRE_PARAM_INVITE_CODE = 2050;
	public static final int MISS_REQUEST_PARAM_PAY_INFO = 2060;
	public static final int MISS_REQUIRE_PARAM_SCORE_ID=2070;

	
	public static final int NO_ACT_IS_AVAILABLE = 3010; //没有可用活动ID
	public static final int ACT_NO_AVAILABLE = 3011; //活动不可用
	public static final int LEVEL_LOW = 3020; //等级太低
	public static final int CODE_GENERATE_DEVICE_UPPER_LIMIT = 3030; //设备上的code生成到了上限
	public static final int CODE_NOT_EXISTS = 3040;//邀请码不存在
	public static final int PLAYER_HAVE_BEEN_INVITED = 3050; //玩家已经被邀请
	public static final int CODE_APPLY_DEVICE_UPPER_LIMIT = 3060; //设备上的code使用到了上限
	public static final int USER_CREATE_TIME_ERROR = 3070; //用户注册时间参数错误
	public static final int OLD_USER_CAN_NOT_TAKE_PART_IN_ACT = 3080; //老用户不能参加此活动
	public static final int INVITED_RELATIONSHIP_DO_NOT_EXISTS = 3090; //邀请关系不存在
	public static final int TRANSACTION_ALREADY_DONE = 3100; //业务已完成
	public static final int PLAYER_DO_NOT_EXISTS = 3110; //玩家不存在
	public static final int NOT_ALLOWED_TO_USE_YOUR_OWN_CODE = 3120; //不能使用自己的邀请码
	
	public static final int EXCHANGE_SPENT_POINTS_WRONG = 4010; //兑换小号积分错误
	public static final int EXCHANGE_SPENT_NO_CODE = 4020; //兑换码消耗完毕
	
	public static final int THERE_IS_INCOMPLETE_EXCHANGE_ORDER = 5010; //有未完成的订单
	public static final int INSUFFICIENT_POINTS = 5020; //积分不足
	public static final int DO_NOT_HAVE_EXCHANGE_PLAN = 5030; //无兑换方案
	public static final int IT_IS_NOT_EXCHANGE_TIME = 5040; //今日不可提交兑换
	
	
	private ApiResultCode(){}
	
	
}
