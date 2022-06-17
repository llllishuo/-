package com.lishuo.reggie.dto;

import com.lishuo.reggie.entity.Setmeal;
import com.lishuo.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private Long categoryId;

    private String categoryName;
}
