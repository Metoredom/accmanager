package com.accmanagement.accmanager;

import com.accmanagement.accmanager.dtos.TransferDto;
import com.accmanagement.accmanager.entities.AccountEntity;
import com.accmanagement.accmanager.entities.ClientEntity;
import com.accmanagement.accmanager.repositories.AccountsRepository;
import com.accmanagement.accmanager.repositories.ClientsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.accmanagement.accmanager.configuration.LinksConfig.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class TransferIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientsRepository clientsRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(){
        if(clientsRepository.findById(1L).isEmpty()) {
            ClientEntity client = clientsRepository.save(new ClientEntity(1L, Collections.emptyList()));
            accountsRepository.save(new AccountEntity(1L, 1.0, "EUR", client));
            accountsRepository.save(new AccountEntity(2L, 1.0, "EUR", client));
            accountsRepository.save(new AccountEntity(3L, 1.0, "USD", client));
        }
        if(accountsRepository.findById(1L).get().getBalance() < 1) {
            AccountEntity account = accountsRepository.findById(1L).get();
            account.setBalance(1.0);
            accountsRepository.save(account);
        }
    }

    @Test
    public void cannotMakeTransferAccountFromNotFound() throws Exception {

        TransferDto dto = new TransferDto();
        dto.setAccount_from(4L);
        dto.setAccount_to(2L);
        dto.setAmount(1.0);
        dto.setCurrency("EUR");

        mockMvc.perform(
                        post(API_V1_URI + TRANSFER_FUNDS_URI)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Account not found")));
    }

    @Test
    public void cannotMakeTransferAccountToNotFound() throws Exception {

        TransferDto dto = new TransferDto();
        dto.setAccount_from(1L);
        dto.setAccount_to(4L);
        dto.setAmount(1.0);
        dto.setCurrency("EUR");

        mockMvc.perform(
                        post(API_V1_URI + TRANSFER_FUNDS_URI)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Account not found")));
    }

    @Test
    public void cannotMakeTransferCurrencyMismatching() throws Exception {

        TransferDto dto = new TransferDto();
        dto.setAccount_from(1L);
        dto.setAccount_to(2L);
        dto.setAmount(1.0);
        dto.setCurrency("USD");

        mockMvc.perform(
                        post(API_V1_URI + TRANSFER_FUNDS_URI)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Currency does not match with the receiver's account")));
    }

    @Test
    public void cannotMakeTransferCurrencyInsufficientBalance() throws Exception {

        TransferDto dto = new TransferDto();
        dto.setAccount_from(1L);
        dto.setAccount_to(2L);
        dto.setAmount(10.0);
        dto.setCurrency("EUR");

        mockMvc.perform(
                        post(API_V1_URI + TRANSFER_FUNDS_URI)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Insufficient balance")));
    }

    @Test
    public void canMakeTransferSameCurrencies() throws Exception {

        TransferDto dto = new TransferDto();
        dto.setAccount_from(1L);
        dto.setAccount_to(2L);
        dto.setAmount(1.0);
        dto.setCurrency("EUR");

        mockMvc.perform(
                        post(API_V1_URI + TRANSFER_FUNDS_URI)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                get(API_V1_URI + ACCOUNTS_URI + "/1/history")
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"account_from\":{\"id\":1,\"balance\":0.0,\"currency\":\"EUR\"},\"account_to\":{\"id\":2,\"balance\":2.0,\"currency\":\"EUR\"},\"amount\":1.0,\"currency\":\"EUR\"")));

        mockMvc.perform(
                        get(API_V1_URI + ACCOUNTS_URI + "/2/history")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"account_from\":{\"id\":1,\"balance\":0.0,\"currency\":\"EUR\"},\"account_to\":{\"id\":2,\"balance\":2.0,\"currency\":\"EUR\"},\"amount\":1.0,\"currency\":\"EUR\"")));
    }

}
