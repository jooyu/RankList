package com.dsky.baas.ranklist.dao;

import com.dsky.baas.ranklist.model.LeaderBoardConfig;

public interface LeaderBoardConfigMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table leader_board_config
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table leader_board_config
     *
     * @mbggenerated
     */
    int insert(LeaderBoardConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table leader_board_config
     *
     * @mbggenerated
     */
    int insertSelective(LeaderBoardConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table leader_board_config
     *
     * @mbggenerated
     */
    LeaderBoardConfig selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table leader_board_config
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(LeaderBoardConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table leader_board_config
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(LeaderBoardConfig record);
}