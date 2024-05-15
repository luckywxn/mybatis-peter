package cn.strongculture.peter;

import cn.strongculture.mybatis.Param;
import cn.strongculture.mybatis.Select;

import java.util.List;

public interface HotelMapper {
    @Select("SELECT id,name,address FROM tb_hotel WHERE name = #{name}")
    public List<Hotel> selectHotelByName(@Param("name") String name);
    public Hotel selectHotelById(int id);
}
