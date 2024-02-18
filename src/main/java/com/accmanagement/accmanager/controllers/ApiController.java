package com.accmanagement.accmanager.controllers;

import com.accmanagement.accmanager.dtos.TransferDto;
import com.accmanagement.accmanager.exceptions.*;
import com.accmanagement.accmanager.services.ClientsAndAccountsService;
import com.accmanagement.accmanager.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import static com.accmanagement.accmanager.configuration.LinksConfig.*;

@RestController
public class ApiController {

    private final ClientsAndAccountsService CASService;
    private final TransferService transferService;

    @Autowired
    public ApiController(ClientsAndAccountsService CASService, TransferService transferService){
        this.CASService = CASService;
        this.transferService = transferService;
    }

    @GetMapping(CLIENT_ACCOUNTS_URI)
    public ResponseEntity<?> getClientAccounts(@PathVariable Long id) throws ClientNotFoundException {
        return ResponseEntity.ok(CASService.getClientAccounts(id));
    }

    @GetMapping(ACCOUNT_HISTORY_URI)
    public ResponseEntity<?> getAccountHistory(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") Integer offset, @RequestParam(required = false, defaultValue = "0") Integer limit) throws AccountNotFoundException {
        int page = offset / limit;
        return ResponseEntity.ok(CASService.getAccountHistory(id, PageRequest.of(page, limit)));
    }

    @PostMapping(TRANSFER_FUNDS_URI)
    public ResponseEntity<?> transferFunds(@Validated @RequestBody TransferDto transferDto) throws InsufficientBalanceException, CurrencyConversionFailedException, CurrencyMismatchingException, AccountNotFoundException {
        transferService.makeTransfer(transferDto.getAccount_from(), transferDto.getAccount_to(), transferDto.getAmount(), transferDto.getCurrency());
        return ResponseEntity.ok().build();
    }

}
