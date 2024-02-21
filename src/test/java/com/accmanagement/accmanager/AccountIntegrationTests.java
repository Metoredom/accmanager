package com.accmanagement.accmanager;

import com.accmanagement.accmanager.entities.AccountEntity;
import com.accmanagement.accmanager.entities.ClientEntity;
import com.accmanagement.accmanager.entities.TransferEntity;
import com.accmanagement.accmanager.repositories.AccountsRepository;
import com.accmanagement.accmanager.repositories.ClientsRepository;
import com.accmanagement.accmanager.repositories.TransfersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;

import static com.accmanagement.accmanager.configuration.LinksConfig.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class AccountIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientsRepository clientsRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private TransfersRepository transfersRepository;

    @BeforeEach
    public void testsSetup() {
        if (clientsRepository.findById(1L).isEmpty()) {
            ClientEntity client = clientsRepository.save(new ClientEntity(1L, Collections.emptyList()));
            accountsRepository.save(new AccountEntity(1L, 1.0, "EUR", client));
            AccountEntity account2 = accountsRepository.save(new AccountEntity(2L, 1.0, "EUR", client));
            AccountEntity account3 = accountsRepository.save(new AccountEntity(3L, 1.0, "EUR", client));
            AccountEntity account4 = accountsRepository.save(new AccountEntity(4L, 1.0, "EUR", client));
            transfersRepository.save(new TransferEntity(1L, account2, account3, 1.0, "EUR", Instant.MIN));
            transfersRepository.save(new TransferEntity(2L, account3, account4, 1.0, "EUR", Instant.MIN));
            transfersRepository.save(new TransferEntity(3L, account3, account4, 1.0, "EUR", Instant.MIN));
            transfersRepository.save(new TransferEntity(4L, account3, account4, 1.0, "EUR", Instant.MIN));
        }
    }
    @Test
    public void canGetAccountHistoryNoHistory() throws Exception {
        mockMvc.perform(
                        get(API_V1_URI + ACCOUNTS_URI+"/1/history")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }
    @Test
    public void canGetAccountHistoryHasHistory() throws Exception {
        mockMvc.perform(
                        get(API_V1_URI + ACCOUNTS_URI+"/2/history")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"id\":1,\"account_from\":{\"id\":2,\"balance\":1.0,\"currency\":\"EUR\"},\"account_to\":{\"id\":3,\"balance\":1.0,\"currency\":\"EUR\"},\"amount\":1.0,\"currency\":\"EUR\",\"timestamp\":\"-1000000000-01-01T00:00:00Z\"}]")));
    }
    @Test
    public void canGetAccountHistoryHasHistoryWithOffset1AndLimit1() throws Exception {
        mockMvc.perform(
                        get(API_V1_URI + ACCOUNTS_URI+"/3/history?offset=1&limit=1")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"id\":3,\"account_from\":{\"id\":3,\"balance\":1.0,\"currency\":\"EUR\"},\"account_to\":{\"id\":4,\"balance\":1.0,\"currency\":\"EUR\"},\"amount\":1.0,\"currency\":\"EUR\",\"timestamp\":\"-1000000000-01-01T00:00:00Z\"}]")));
    }
    @Test
    public void canGetAccountHistoryHasHistoryWithOffset1AndLimit2() throws Exception {
        mockMvc.perform(
                        get(API_V1_URI + ACCOUNTS_URI+"/3/history?offset=1&limit=2")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"id\":4,\"account_from\":{\"id\":3,\"balance\":1.0,\"currency\":\"EUR\"},\"account_to\":{\"id\":4,\"balance\":1.0,\"currency\":\"EUR\"},\"amount\":1.0,\"currency\":\"EUR\",\"timestamp\":\"-1000000000-01-01T00:00:00Z\"},{\"id\":3,\"account_from\":{\"id\":3,\"balance\":1.0,\"currency\":\"EUR\"},\"account_to\":{\"id\":4,\"balance\":1.0,\"currency\":\"EUR\"},\"amount\":1.0,\"currency\":\"EUR\",\"timestamp\":\"-1000000000-01-01T00:00:00Z\"}]")));
    }
    @Test
    public void cannotGetAccountHistoryAccountNotFound() throws Exception {
        mockMvc.perform(
                        get(API_V1_URI + ACCOUNTS_URI+"/5/history")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Account not found")));
    }
}
