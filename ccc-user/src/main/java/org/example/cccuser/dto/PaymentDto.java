package org.example.cccuser.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PaymentDto {
    private String pdtId;
    private String amount;
    private String email;

    @Builder
    public PaymentDto(String pdtId, String amount, String email) {
        this.pdtId = pdtId;
        this.amount = amount;
        this.email = email;
    }
}
