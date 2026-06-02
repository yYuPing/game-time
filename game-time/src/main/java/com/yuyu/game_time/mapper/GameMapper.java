package com.yuyu.game_time.mapper;

import com.yuyu.game_time.entity.Game;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameMapper {

    List<Game> findAll();

    Game findById(@Param("id") Long id);
}
