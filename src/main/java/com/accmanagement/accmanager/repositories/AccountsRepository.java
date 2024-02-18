package com.accmanagement.accmanager.repositories;

import com.accmanagement.accmanager.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountsRepository extends JpaRepository<AccountEntity, Long> {
}
