package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * @author zeqi
 * @date 2026/10/22  09:59
 * @description
 */
public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);
}
