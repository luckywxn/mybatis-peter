package cn.strongculture.peter;

import cn.strongculture.mybatis.Select;

import java.util.List;

public interface HotelMapper {
    @Select("SELECT * FROM tb_hotel = #0")
    public List<Hotel> selectHotelByName(String name);
    public Hotel selectHotelById(int id);
}
