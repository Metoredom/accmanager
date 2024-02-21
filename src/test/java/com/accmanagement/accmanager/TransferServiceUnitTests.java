package com.accmanagement.accmanager;

import com.accmanagement.accmanager.entities.AccountEntity;
import com.accmanagement.accmanager.entities.ClientEntity;
import com.accmanagement.accmanager.entities.TransferEntity;
import com.accmanagement.accmanager.exceptions.AccountNotFoundException;
import com.accmanagement.accmanager.exceptions.CurrencyConversionException;
import com.accmanagement.accmanager.exceptions.CurrencyMismatchingException;
import com.accmanagement.accmanager.exceptions.InsufficientBalanceException;
import com.accmanagement.accmanager.repositories.AccountsRepository;
import com.accmanagement.accmanager.repositories.TransfersRepository;
import com.accmanagement.accmanager.services.TimeService;
import com.accmanagement.accmanager.services.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TransferServiceUnitTests {

    @Mock
    private TransfersRepository transfersRepository;
    @Mock
    private AccountsRepository accountsRepository;
    @Mock
    private TimeService timeService;
    @InjectMocks
    private TransferService transferService;

    @Test
    public void canAddTransferRecord(){
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        AccountEntity mockAccountFrom = new AccountEntity(1L, 1.0, "EUR", mockClient);
        AccountEntity mockAccountTo = new AccountEntity(2L, 0.0, "EUR", mockClient);
        mockClient.setAccounts(List.of(mockAccountFrom, mockAccountTo));
        TransferEntity transferEntity = new TransferEntity(null, mockAccountFrom, mockAccountTo, 1.0, "EUR", Instant.MIN);

        when(timeService.now()).thenReturn(Instant.MIN);

        transferService.addTransferRecord(mockAccountFrom, mockAccountTo, 1.0, 1.0, "EUR");

        verify(transfersRepository).save(transferEntity);
    }

    @Test
    public void canMakeTransfer() throws InsufficientBalanceException, CurrencyMismatchingException, CurrencyConversionException, AccountNotFoundException {
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        AccountEntity mockAccountFrom = new AccountEntity(1L, 1.0, "EUR", mockClient);
        AccountEntity mockAccountTo = new AccountEntity(2L, 0.0, "EUR", mockClient);
        mockClient.setAccounts(List.of(mockAccountFrom, mockAccountTo));
        TransferEntity transferEntity = new TransferEntity(null, mockAccountFrom, mockAccountTo, 1.0, "EUR", Instant.MIN);

        when(timeService.now()).thenReturn(Instant.MIN);
        when(accountsRepository.findById(1L)).thenReturn(Optional.of(mockAccountFrom));
        when(accountsRepository.findById(2L)).thenReturn(Optional.of(mockAccountTo));

        transferService.makeTransfer(1L, 2L, 1.0, "EUR");

        verify(transfersRepository).save(transferEntity);
    }

    @Test
    public void cannotMakeTransferAccountFromNotFound() {
        when(accountsRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> transferService.makeTransfer(1L, 2L, 1.0, "EUR"));
    }

    @Test
    public void cannotMakeTransferAccountToNotFound() {
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        AccountEntity mockAccountFrom = new AccountEntity(1L, 1.0, "EUR", mockClient);
        mockClient.setAccounts(Collections.singletonList(mockAccountFrom));

        when(accountsRepository.findById(1L)).thenReturn(Optional.of(mockAccountFrom));
        when(accountsRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> transferService.makeTransfer(1L, 2L, 1.0, "EUR"));
    }

    @Test
    public void cannotMakeTransferCurrencyMismatching() {
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        AccountEntity mockAccountFrom = new AccountEntity(1L, 1.0, "EUR", mockClient);
        AccountEntity mockAccountTo = new AccountEntity(2L, 0.0, "USD", mockClient);
        mockClient.setAccounts(List.of(mockAccountFrom, mockAccountTo));

        mockClient.setAccounts(Collections.singletonList(mockAccountFrom));

        when(accountsRepository.findById(1L)).thenReturn(Optional.of(mockAccountFrom));
        when(accountsRepository.findById(2L)).thenReturn(Optional.of(mockAccountTo));

        assertThrows(CurrencyMismatchingException.class, () -> transferService.makeTransfer(1L, 2L, 1.0, "EUR"));
    }

    @Test
    public void cannotMakeTransferInsufficientBalance() {
        ClientEntity mockClient = new ClientEntity(1L, Collections.emptyList());
        AccountEntity mockAccountFrom = new AccountEntity(1L, 0.0, "EUR", mockClient);
        AccountEntity mockAccountTo = new AccountEntity(2L, 0.0, "EUR", mockClient);
        mockClient.setAccounts(List.of(mockAccountFrom, mockAccountTo));

        mockClient.setAccounts(Collections.singletonList(mockAccountFrom));

        when(accountsRepository.findById(1L)).thenReturn(Optional.of(mockAccountFrom));
        when(accountsRepository.findById(2L)).thenReturn(Optional.of(mockAccountTo));

        assertThrows(InsufficientBalanceException.class, () -> transferService.makeTransfer(1L, 2L, 1.0, "EUR"));
    }

}
