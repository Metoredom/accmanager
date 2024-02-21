package com.accmanagement.accmanager.repositories;

import com.accmanagement.accmanager.entities.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransfersRepository extends JpaRepository<TransferEntity, Long> {

    @Query(value = "SELECT transfer FROM TransferEntity transfer WHERE transfer.account_from.id = :account_number OR transfer.account_to.id = :account_number ORDER BY transfer.id DESC")
    Optional<List<TransferEntity>> findByAccount(Long account_number, Pageable pageable);

}
