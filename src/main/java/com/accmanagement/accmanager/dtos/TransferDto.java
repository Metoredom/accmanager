package com.accmanagement.accmanager.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {

    private Long account_from;
    private Long account_to;
    private Double amount;
    private String currency;

}
