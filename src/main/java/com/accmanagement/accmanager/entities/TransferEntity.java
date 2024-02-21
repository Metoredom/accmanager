package com.accmanagement.accmanager.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "transfers", indexes = {
        @Index(name = "transfer_account_from", columnList = "account_from_id"),
        @Index(name = "transfer_account_to", columnList = "account_to_id")
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TransferEntity that = (TransferEntity) o;
        if(getId() != null && Objects.equals(getId(), that.getId()))
            return true;
        else if(getId() == null && that.getId() == null)
            return Objects.equals(getAmount(), that.getAmount())
                    && Objects.equals(getAccount_from(), that.getAccount_from())
                    && Objects.equals(getAccount_to(), that.getAccount_to())
                    && Objects.equals(getCurrency(), that.getCurrency())
                    && Objects.equals(getTimestamp(), that.getTimestamp());
        return false;
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
