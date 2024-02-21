package com.accmanagement.accmanager.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    @NotBlank(message = "Account from field is mandatory")
    private Long account_from;
    @NotBlank(message = "Account to field is mandatory")
    private Long account_to;
    @NotBlank(message = "Amount field is mandatory")
    @Min(value = 0, message = "The amount should be non-negative")
    private Double amount;
    @NotBlank(message = "Currency field is mandatory")
    private String currency;
}
