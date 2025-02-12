package org.example.restaurantmenu.controller;

import org.example.restaurantmenu.dto.MenuDto;
import org.example.restaurantmenu.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/meals")
public class MenuController {
    @Autowired
    private MealService mealService;

    @GetMapping("/today")
    public String getTodayMeal() {
        MenuDto menuDto = mealService.getTodayMenu();
        if (menuDto != null) {
            return menuDto.toString(); // Menu 객체를 문자열로 반환
        } else {
            return "오늘의 식단이 없습니다.";
        }
    }

    @PostMapping("/add")
    public MenuDto addMeal(@RequestBody MenuDto menuDto) {
        return mealService.saveMenu(menuDto);
    }

    @GetMapping("/{month}/{day}")
    public List<MenuDto> getMealByDate(@PathVariable int month, @PathVariable int day) {
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), month, day);
        List<MenuDto> menuDtos = mealService.getByDate(date);
        if (menuDtos != null && !menuDtos.isEmpty() ) {
            return menuDtos; // 리스트를 그대로 반환
        } else {
            // 식단이 없을 경우 빈 리스트 반환 (혹은 null 반환)
            return new ArrayList<>(); // 빈 리스트로 반환하거나
        }
    }
}
