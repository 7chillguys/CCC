package org.example.cccgame.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class RouletteService {
    public String startRoulette(List<String> items) {
        if(items == null || items.isEmpty()) {
            throw new IllegalArgumentException("룰렛의 항목이 비어 있습니다.");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(items.size()); // 당첨 인텍스 랜덤으로 선택
        return items.get(randomIndex);
    }
}
