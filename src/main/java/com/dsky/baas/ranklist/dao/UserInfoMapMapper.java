package com.dsky.baas.ranklist.dao;

import org.apache.ibatis.annotations.Mapper;

import com.dsky.baas.ranklist.model.UserInfoMap;

public interface UserInfoMapMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info_map
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info_map
     *
     * @mbggenerated
     */
    int insert(UserInfoMap record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info_map
     *
     * @mbggenerated
     */
    int insertSelective(UserInfoMap record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info_map
     *
     * @mbggenerated
     */
    UserInfoMap selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info_map
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(UserInfoMap record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info_map
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UserInfoMap record);

	int updateByUidAndBoardid(UserInfoMap record);

	UserInfoMap selectOutScore(UserInfoMap user);
}