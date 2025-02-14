package org.example.cccgame.dto.roulette;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResultDto {
    private String result;

    public ResultDto(String result) {
        this.result = result;
    }
}
