package com.yuyu.game_time.mapper;

import com.yuyu.game_time.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findById(@Param("id") Long id);

    User findByUsername(@Param("username") String username);

    int countByUsername(@Param("username") String username);

    int countByEmail(@Param("email") String email);

    void insert(User user);

    void update(User user);
}
