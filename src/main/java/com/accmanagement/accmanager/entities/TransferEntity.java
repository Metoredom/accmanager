package com.accmanagement.accmanager.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transfers", indexes = {
        @Index(name = "transfer_account_from", columnList = "account_from_id"),
        @Index(name = "transfer_account_to", columnList = "account_to_id"),
})
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_from_id", referencedColumnName = "id")
    private AccountEntity account_from;

    @ManyToOne
    @JoinColumn(name = "account_to_id", referencedColumnName = "id")
    private AccountEntity account_to;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant timestamp;

    public TransferEntity(AccountEntity account_from, AccountEntity account_to, Double amount, String currency, Instant timestamp) {
        this.account_from = account_from;
        this.account_to = account_to;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
    }
}
