package org.example.cccgame.dto.ladder;

import lombok.Data;

@Data
public class ResultDto {
    private String playerName;
    private String result;

    public ResultDto(String playerName, String result) {
        this.playerName = playerName;
        this.result = result;
    }
}