package me.maiz.trainning.framework.mybatis.dal.mapper;

import java.util.List;
import me.maiz.trainning.framework.mybatis.dal.entity.User;
import me.maiz.trainning.framework.mybatis.dal.entity.UserExample;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int countByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int deleteByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int deleteByPrimaryKey(Long userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    List<User> selectByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    User selectByPrimaryKey(Long userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table TJS_USER
     *
     * @mbggenerated Tue Mar 20 15:24:21 CST 2018
     */
    int updateByPrimaryKey(User record);
}