package com.dsky.baas.ranklist.model;

public class UserInfoMap {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_info_map.id
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_info_map.uid
     *
     * @mbggenerated
     */
    private Integer uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_info_map.leaderboardid
     *
     * @mbggenerated
     */
    private Integer leaderboardid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_info_map.score
     *
     * @mbggenerated
     */
    private Integer score;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_info_map.create_time
     *
     * @mbggenerated
     */
    private Integer createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_info_map.extend
     *
     * @mbggenerated
     */
    private int expireTime;
    private String extend;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_info_map.id
     *
     * @return the value of user_info_map.id
     *
     * @mbggenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_info_map.id
     *
     * @param id the value for user_info_map.id
     *
     * @mbggenerated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_info_map.uid
     *
     * @return the value of user_info_map.uid
     *
     * @mbggenerated
     */
    public Integer getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_info_map.uid
     *
     * @param uid the value for user_info_map.uid
     *
     * @mbggenerated
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_info_map.leaderboardid
     *
     * @return the value of user_info_map.leaderboardid
     *
     * @mbggenerated
     */
    public Integer getLeaderboardid() {
        return leaderboardid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_info_map.leaderboardid
     *
     * @param leaderboardid the value for user_info_map.leaderboardid
     *
     * @mbggenerated
     */
    public void setLeaderboardid(Integer leaderboardid) {
        this.leaderboardid = leaderboardid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_info_map.score
     *
     * @return the value of user_info_map.score
     *
     * @mbggenerated
     */
    public Integer getScore() {
        return score;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_info_map.score
     *
     * @param score the value for user_info_map.score
     *
     * @mbggenerated
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_info_map.create_time
     *
     * @return the value of user_info_map.create_time
     *
     * @mbggenerated
     */
    public Integer getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_info_map.create_time
     *
     * @param createTime the value for user_info_map.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_info_map.extend
     *
     * @return the value of user_info_map.extend
     *
     * @mbggenerated
     */
    public String getExtend() {
        return extend;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_info_map.extend
     *
     * @param extend the value for user_info_map.extend
     *
     * @mbggenerated
     */
    public void setExtend(String extend) {
        this.extend = extend == null ? null : extend.trim();
    }

	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}
}