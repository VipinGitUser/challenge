package com.dws.challenge.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransferRequest {
    @NotNull
    private String sourceAccountId;

    @NotNull
    private String destinationAccountId;

    @NotNull
    @Min(value = 1, message = "Amount must be greater than zero.")
    private BigDecimal amount;

    public TransferRequest(String sourceAccountId, String destinationAccountId, BigDecimal amount) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }
}
