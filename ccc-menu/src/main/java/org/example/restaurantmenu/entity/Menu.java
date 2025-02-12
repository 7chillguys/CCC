package org.example.restaurantmenu.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@ToString
@Entity
@Getter
@Table(name = "restaurant_menu")  // DB 테이블 이름 수정
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "menu_date")  // DB 컬럼 이름 지정
    private LocalDate menuDate;  // 메뉴 날짜

    private String menu;  // 메뉴 목록
    private boolean isLunch; // 점심/저녁 구분

    @Builder
    public Menu(LocalDate menuDate, String menu, boolean isLunch) {
        this.menuDate = menuDate;
        this.menu = menu;
        this.isLunch = isLunch;
    }
}
