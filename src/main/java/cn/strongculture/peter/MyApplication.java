package cn.strongculture.peter;

import cn.strongculture.mybatis.MapperProxyFactory;

import java.util.List;

public class MyApplication {
    public static void main(String[] args) {
        HotelMapper hotelMapper = MapperProxyFactory.getMapper(HotelMapper.class);
        List<Hotel> hotels = hotelMapper.selectHotelByName("7天连锁酒店(上海宝山路地铁站店)");
        System.out.println(hotels);
    }
}
