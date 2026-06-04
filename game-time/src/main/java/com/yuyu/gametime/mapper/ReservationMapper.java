package com.yuyu.gametime.mapper;

import com.yuyu.gametime.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReservationMapper {

    Reservation findById(@Param("id") Long id);

    List<Reservation> findByDate(@Param("date") LocalDate date);

    List<Reservation> findByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    List<Reservation> findByUserId(@Param("userId") Long userId);

    void insert(Reservation reservation);

    void deleteById(@Param("id") Long id);

    void updateNote(@Param("id") Long id, @Param("note") String note);
}
