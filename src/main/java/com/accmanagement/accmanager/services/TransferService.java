package com.accmanagement.accmanager.services;

import com.accmanagement.accmanager.entities.AccountEntity;
import com.accmanagement.accmanager.entities.TransferEntity;
import com.accmanagement.accmanager.exceptions.AccountNotFoundException;
import com.accmanagement.accmanager.exceptions.CurrencyConversionException;
import com.accmanagement.accmanager.exceptions.CurrencyMismatchingException;
import com.accmanagement.accmanager.exceptions.InsufficientBalanceException;
import com.accmanagement.accmanager.repositories.AccountsRepository;
import com.accmanagement.accmanager.repositories.TransfersRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransferService {

    private final ConvertService convertService;
    private final TimeService timeService;
    private final AccountsRepository accountsRepository;
    private final TransfersRepository transfersRepository;
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    public TransferService(ConvertService convertService, TimeService timeService, AccountsRepository accountsRepository, TransfersRepository transfersRepository) {
        this.convertService = convertService;
        this.timeService = timeService;
        this.accountsRepository = accountsRepository;
        this.transfersRepository = transfersRepository;
    }

    public void addTransferRecord(AccountEntity account_from_ent, AccountEntity account_to_ent, double amount_from, double amount_to, String currency) {
        TransferEntity transferEntity = new TransferEntity(account_from_ent, account_to_ent, amount_to, currency, timeService.now());
        transfersRepository.save(transferEntity);

        account_from_ent.setBalance(account_from_ent.getBalance() - amount_from);
        account_to_ent.setBalance(account_to_ent.getBalance() + amount_to);

        accountsRepository.save(account_from_ent);
        accountsRepository.save(account_to_ent);
        logger.debug(String.format("Funds transferred successfully from %s, to %s, with amount %s %s", account_from_ent.getId(), account_to_ent.getId(), amount_to, currency));
    }

    @Transactional
    public void makeTransfer(Long account_from, Long account_to, Double amount_to, String currency) throws AccountNotFoundException, CurrencyMismatchingException, InsufficientBalanceException, CurrencyConversionException {
        AccountEntity account_from_ent = accountsRepository.findById(account_from).orElseThrow(AccountNotFoundException::new);
        AccountEntity account_to_ent = accountsRepository.findById(account_to).orElseThrow(AccountNotFoundException::new);

        if (!currency.toUpperCase().equals(account_to_ent.getCurrency()))
            throw new CurrencyMismatchingException();

        double amount_from = amount_to;
        if (!account_from_ent.getCurrency().equals(account_to_ent.getCurrency())) {
            JsonNode result = convertService.convert(account_from_ent.getCurrency(), account_to_ent.getCurrency(), amount_to).block();
            if(result != null && result.has("result"))
                amount_from = result.get("result").asDouble();
            else
                throw new CurrencyConversionException();
        }
        if (account_from_ent.getBalance() < amount_from)
            throw new InsufficientBalanceException();

        addTransferRecord(account_from_ent, account_to_ent, amount_from, amount_to, currency);
    }
}
