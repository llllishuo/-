package com.menghan.reggie.dto;

import com.menghan.reggie.entity.Setmeal;
import com.menghan.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private Long categoryId;

    private String categoryName;
}
