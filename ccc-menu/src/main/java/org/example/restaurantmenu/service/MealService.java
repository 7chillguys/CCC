package org.example.restaurantmenu.service;

import org.example.restaurantmenu.dto.MenuDto;
import org.example.restaurantmenu.entity.Menu;
import org.example.restaurantmenu.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealService {
    @Autowired
    private MenuRepository menuRepository;

    // 오늘의 식단을 DTO로 반환
    public MenuDto getTodayMenu() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        boolean isLunch = now.isBefore(LocalTime.of(13, 0)); // 오후 1시 이전이면 점심
        List<Menu> menus = menuRepository.findByMenuDateAndIsLunch(today, isLunch);
        return menus.isEmpty() ? null : toDto(menus.get(0));  // Menu -> MenuDto 변환
    }



    // 특정 날짜의 식단을 반환
    public List<MenuDto> getByDate(LocalDate date) {
        List<Menu> menus = menuRepository.findByMenuDate(date);
        return menus.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 메뉴 저장 테스트
    public MenuDto saveMenu(MenuDto menuDto) {
        Menu menu = new Menu(menuDto.getMenuDate(), menuDto.getMenu(), menuDto.isLunch());
        Menu savedMenu = menuRepository.save(menu);
        return toDto(savedMenu);
    }


    // Menu 엔티티를 MenuDto로 변환하는 메서드
    private MenuDto toDto(Menu menu) {
        return MenuDto.builder()
                .menuDate(menu.getMenuDate())
                .menu(menu.getMenu())
                .isLunch(menu.isLunch())
                .build();
    }
}
