package com.accmanagement.accmanager.repositories;

import com.accmanagement.accmanager.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientsRepository extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findById(Long id);

}
