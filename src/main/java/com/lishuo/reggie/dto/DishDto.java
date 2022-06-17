package com.lishuo.reggie.dto;


import com.lishuo.reggie.entity.Dish;
import com.lishuo.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {


    //口味列表
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
