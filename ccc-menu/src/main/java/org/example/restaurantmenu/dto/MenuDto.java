package org.example.restaurantmenu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MenuDto {
    private LocalDate menuDate;  // 메뉴 날짜
    private String menu;        // 메뉴 목록
    private boolean isLunch;    // 점심/저녁 구분

    @Builder
    public MenuDto(LocalDate menuDate, String menu, boolean isLunch) {
        this.menuDate = menuDate;
        this.menu = menu;
        this.isLunch = isLunch;
    }
}
