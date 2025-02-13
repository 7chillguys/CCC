package org.example.cccgame.dto.roulette;

import lombok.Data;


@Data
public class ResultDto {
    private String result;

    public ResultDto(String result) {
        this.result = result;
    }
}
