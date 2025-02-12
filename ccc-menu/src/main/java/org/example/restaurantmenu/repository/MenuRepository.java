package org.example.restaurantmenu.repository;

import org.example.restaurantmenu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    // 특정날짜 식단 조회
    List<Menu> findByMenuDate(LocalDate menuDate);
    // 특정날짜의 점심,저녁 조회
    List<Menu> findByMenuDateAndIsLunch(LocalDate menuDate, boolean isLunch);
}
