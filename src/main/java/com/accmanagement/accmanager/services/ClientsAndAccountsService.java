package com.accmanagement.accmanager.services;

import com.accmanagement.accmanager.entities.AccountEntity;
import com.accmanagement.accmanager.entities.TransferEntity;
import com.accmanagement.accmanager.exceptions.AccountNotFoundException;
import com.accmanagement.accmanager.exceptions.ClientNotFoundException;
import com.accmanagement.accmanager.repositories.AccountsRepository;
import com.accmanagement.accmanager.repositories.ClientsRepository;
import com.accmanagement.accmanager.repositories.TransfersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientsAndAccountsService {
    private final ClientsRepository clientsRepository;
    private final AccountsRepository accountsRepository;
    private final TransfersRepository transfersRepository;

    @Autowired
    public ClientsAndAccountsService(ClientsRepository clientsRepository, AccountsRepository accountsRepository, TransfersRepository transfersRepository) {
        this.clientsRepository = clientsRepository;
        this.accountsRepository = accountsRepository;
        this.transfersRepository = transfersRepository;
    }

    public List<AccountEntity> getClientAccounts(Long client_id) throws ClientNotFoundException {
        return clientsRepository.findById(client_id).orElseThrow(ClientNotFoundException::new).getAccounts();
    }

    public List<TransferEntity> getAccountHistory(Long account_id, Pageable pageable) throws AccountNotFoundException {
        if (!accountsRepository.existsById(account_id)) throw new AccountNotFoundException();
        return transfersRepository.findByAccount(account_id, pageable).orElse(new ArrayList<>());
    }
}
