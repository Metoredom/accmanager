package com.accmanagement.accmanager;

import com.accmanagement.accmanager.entities.AccountEntity;
import com.accmanagement.accmanager.entities.ClientEntity;
import com.accmanagement.accmanager.entities.TransferEntity;
import com.accmanagement.accmanager.exceptions.AccountNotFoundException;
import com.accmanagement.accmanager.exceptions.ClientNotFoundException;
import com.accmanagement.accmanager.repositories.AccountsRepository;
import com.accmanagement.accmanager.repositories.ClientsRepository;
import com.accmanagement.accmanager.repositories.TransfersRepository;
import com.accmanagement.accmanager.services.ClientsAndAccountsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CASServiceUnitTests {

    @Mock
    private ClientsRepository clientsRepository;
    @Mock
    private AccountsRepository accountsRepository;
    @Mock
    private TransfersRepository transfersRepository;
    @InjectMocks
    private ClientsAndAccountsService casService;

    @Test
    public void canGetClientAccountsNoAccounts() throws ClientNotFoundException {
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        List<AccountEntity> mockAccounts = Collections.emptyList();

        when(clientsRepository.findById(1L)).thenReturn(Optional.of(mockClient));

        List<AccountEntity> actualAccounts = casService.getClientAccounts(1L);

        assertNotNull(actualAccounts);
        assertEquals(mockAccounts, actualAccounts);
    }

    @Test
    public void canGetClientAccountsHasAccounts() throws ClientNotFoundException {
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        List<AccountEntity> mockAccounts = Collections.singletonList(
                new AccountEntity(1L, 0.0, "EUR", mockClient));
        mockClient.setAccounts(mockAccounts);

        when(clientsRepository.findById(1L)).thenReturn(Optional.of(mockClient));

        List<AccountEntity> actualAccounts = casService.getClientAccounts(1L);

        assertNotNull(actualAccounts);
        assertEquals(mockAccounts, actualAccounts);
    }

    @Test
    public void cannotGetClientAccountsClientNotFound() {
        when(clientsRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClientNotFoundException.class, () -> casService.getClientAccounts(1L));
    }

    @Test
    public void canGetAccountHistoryNoHistory() throws AccountNotFoundException {
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        AccountEntity mockAccount = new AccountEntity(1L, 0.0, "EUR", mockClient);
        List<TransferEntity> mockTransfers = Collections.emptyList();
        mockClient.setAccounts(Collections.singletonList(mockAccount));

        when(accountsRepository.existsById(1L)).thenReturn(true);
        when(transfersRepository.findByAccount(1L, Pageable.ofSize(10))).thenReturn(Optional.of(mockTransfers));

        List<TransferEntity> actualTransfers = casService.getAccountHistory(1L, Pageable.ofSize(10));

        assertNotNull(actualTransfers);
        assertEquals(mockTransfers, actualTransfers);
    }

    @Test
    public void canGetAccountHistoryHasHistory() throws AccountNotFoundException {
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        AccountEntity mockAccount1 = new AccountEntity(1L, 0.0, "EUR", mockClient);
        AccountEntity mockAccount2 = new AccountEntity(2L, 0.0, "EUR", mockClient);
        List<TransferEntity> mockTransfers = Collections.singletonList(new TransferEntity(1L, mockAccount1, mockAccount2, 1.0, "EUR", Instant.MIN));
        mockClient.setAccounts(List.of(mockAccount1, mockAccount2));

        when(accountsRepository.existsById(1L)).thenReturn(true);
        when(transfersRepository.findByAccount(1L, Pageable.ofSize(10))).thenReturn(Optional.of(mockTransfers));

        List<TransferEntity> actualTransfers = casService.getAccountHistory(1L, Pageable.ofSize(10));

        assertNotNull(actualTransfers);
        assertEquals(mockTransfers, actualTransfers);
    }

    @Test
    public void cannotGetAccountHistoryAccountNotFound(){
        when(accountsRepository.existsById(1L)).thenReturn(false);
        assertThrows(AccountNotFoundException.class, () -> casService.getAccountHistory(1L, Pageable.ofSize(10)));
    }


}
