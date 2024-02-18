package com.accmanagement.accmanager.services;

import com.accmanagement.accmanager.entities.AccountEntity;
import com.accmanagement.accmanager.entities.TransferEntity;
import com.accmanagement.accmanager.exceptions.AccountNotFoundException;
import com.accmanagement.accmanager.exceptions.CurrencyConversionFailedException;
import com.accmanagement.accmanager.exceptions.CurrencyMismatchingException;
import com.accmanagement.accmanager.exceptions.InsufficientBalanceException;
import com.accmanagement.accmanager.repositories.AccountsRepository;
import com.accmanagement.accmanager.repositories.TransfersRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class TransferService {

    private final ConvertService convertService;
    private final AccountsRepository accountsRepository;
    private final TransfersRepository transfersRepository;

    @Autowired
    public TransferService(ConvertService convertService, AccountsRepository accountsRepository, TransfersRepository transfersRepository) {
        this.convertService = convertService;
        this.accountsRepository = accountsRepository;
        this.transfersRepository = transfersRepository;
    }

    public void addTransfer(AccountEntity account_from_ent, AccountEntity account_to_ent, double amount_from, double amount_to, String currency) {
        TransferEntity transferEntity = new TransferEntity(account_from_ent, account_to_ent, amount_to, currency, Instant.now());
        transfersRepository.save(transferEntity);

        account_from_ent.setBalance(account_from_ent.getBalance() - amount_from);
        account_to_ent.setBalance(account_to_ent.getBalance() + amount_to);

        accountsRepository.save(account_from_ent);
        accountsRepository.save(account_to_ent);
    }

    @Transactional
    public void makeTransfer(Long account_from, Long account_to, Double amount_to, String currency) throws AccountNotFoundException, CurrencyMismatchingException, CurrencyConversionFailedException, InsufficientBalanceException {
        AccountEntity account_from_ent = accountsRepository.findById(account_from).orElseThrow(AccountNotFoundException::new);
        AccountEntity account_to_ent = accountsRepository.findById(account_to).orElseThrow(AccountNotFoundException::new);

        if (!currency.toUpperCase().equals(account_to_ent.getCurrency()))
            throw new CurrencyMismatchingException();

        if (!account_from_ent.getCurrency().equals(account_to_ent.getCurrency())) {
            convertService.convert(account_to_ent.getCurrency(), account_from_ent.getCurrency(), amount_to).subscribe(json -> {
                double amount_from;
                if (json != null && json.has("result"))
                    amount_from = json.get("result").asDouble();
                else
                    try {
                        throw new CurrencyConversionFailedException();
                    } catch (CurrencyConversionFailedException e) {
                        throw new RuntimeException(e);
                    }

                if (account_from_ent.getBalance() < amount_from)
                    try {
                        throw new InsufficientBalanceException();
                    } catch (InsufficientBalanceException e) {
                        throw new RuntimeException(e);
                    }

                addTransfer(account_from_ent, account_to_ent, amount_from, amount_to, currency);
            });
        } else {
            double amount_from = amount_to;
            if (account_from_ent.getBalance() < amount_from)
                throw new InsufficientBalanceException();

            addTransfer(account_from_ent, account_to_ent, amount_from, amount_to, currency);
        }

    }
}
