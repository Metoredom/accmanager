package com.accmanagement.accmanager.controllers;

import com.accmanagement.accmanager.dtos.TransferDto;
import com.accmanagement.accmanager.exceptions.*;
import com.accmanagement.accmanager.services.ClientsAndAccountsService;
import com.accmanagement.accmanager.services.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static com.accmanagement.accmanager.configuration.LinksConfig.*;

@Validated
@RestController
@RequestMapping(API_V1_URI)
public class ApiV1Controller {

    private final ClientsAndAccountsService CASService;
    private final TransferService transferService;

    @Autowired
    public ApiV1Controller(ClientsAndAccountsService CASService, TransferService transferService){
        this.CASService = CASService;
        this.transferService = transferService;
    }

    @GetMapping(path = CLIENT_ACCOUNTS_URI)
    public ResponseEntity<?> getClientAccounts(@PathVariable Long id) throws ClientNotFoundException {
        return ResponseEntity.ok(CASService.getClientAccounts(id));
    }

    @GetMapping(path = ACCOUNT_HISTORY_URI)
    public ResponseEntity<?> getAccountHistory(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") Integer offset, @RequestParam(required = false, defaultValue = "10") Integer limit) throws AccountNotFoundException {
        int page = offset / limit;
        return ResponseEntity.ok(CASService.getAccountHistory(id, PageRequest.of(page, limit)));
    }

    @PostMapping(path = TRANSFER_FUNDS_URI)
    public ResponseEntity<?> transferFunds(@Valid @RequestBody TransferDto transferDto) throws InsufficientBalanceException, CurrencyMismatchingException, AccountNotFoundException, CurrencyConversionException, MethodArgumentNotValidException {
        BindingResult bindingResult = new BeanPropertyBindingResult(transferDto.getAccount_to(), "Account to");
        bindingResult.addError(new FieldError("Account to", "Account to", "Accounts should be different"));
        if(transferDto.getAccount_from().equals(transferDto.getAccount_to()))
            throw new MethodArgumentNotValidException(null, bindingResult);

        transferService.makeTransfer(transferDto.getAccount_from(), transferDto.getAccount_to(), transferDto.getAmount(), transferDto.getCurrency());
        return ResponseEntity.ok().build();
    }

}
