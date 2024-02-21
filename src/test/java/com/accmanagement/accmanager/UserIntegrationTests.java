package com.accmanagement.accmanager;

import com.accmanagement.accmanager.entities.AccountEntity;
import com.accmanagement.accmanager.entities.ClientEntity;
import com.accmanagement.accmanager.repositories.AccountsRepository;
import com.accmanagement.accmanager.repositories.ClientsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.accmanagement.accmanager.configuration.LinksConfig.API_V1_URI;
import static com.accmanagement.accmanager.configuration.LinksConfig.CLIENTS_URI;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientsRepository clientsRepository;
    @Autowired
    private AccountsRepository accountsRepository;

    @BeforeEach
    public void setup(){
        //accountsRepository.deleteAll();
        //clientsRepository.deleteAll();
        if(clientsRepository.findById(1L).isEmpty())
            clientsRepository.save(new ClientEntity(1L, Collections.emptyList()));
        if(clientsRepository.findById(2L).isEmpty()) {
            ClientEntity client = clientsRepository.save(new ClientEntity(2L, Collections.emptyList()));
            accountsRepository.save(new AccountEntity(1L, 1.0, "EUR", client));
        }

    }


    @Test
    public void canGetUserAccountsNoAccounts() throws Exception {
        mockMvc.perform(
                        get(API_V1_URI + CLIENTS_URI+"/1/accounts")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    public void canGetUserAccountsHasAccounts() throws Exception {
        mockMvc.perform(
                        get(API_V1_URI + CLIENTS_URI+"/2/accounts")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"id\":1,\"balance\":1.0,\"currency\":\"EUR\"}]")));
    }

    @Test
    public void cannotGetUserAccountsClientNotFound() throws Exception {
        mockMvc.perform(
                        get(API_V1_URI + CLIENTS_URI+"/3/accounts")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Client not found")));
    }


}
