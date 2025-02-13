package org.example.cccgame.dto.ladder;

import lombok.Data;
import java.util.List;

@Data
public class RequestDto {
    private List<String> players;  // 참가자 리스트
}
