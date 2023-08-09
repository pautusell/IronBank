package com.ironhack.ironbank.model;

import com.ironhack.ironbank.enums.AccountType;
import com.ironhack.ironbank.enums.Status;
import com.ironhack.ironbank.utils.Money;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_d_type")
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "balance")),
            @AttributeOverride( name = "currency", column = @Column(name = "balance_currency")),
    })
    private Money balance;
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;
    @ManyToOne
    @JoinColumn(name = "primary_owner", referencedColumnName = "id")
    private AccountHolder primaryOwner;
    @ManyToOne
    @JoinColumn(name = "secondary_owner", referencedColumnName = "id")
    private AccountHolder secondaryOwner;
    private BigDecimal penaltyFee = BigDecimal.valueOf(40);
    private Status status = Status.ACTIVE;
    private BigDecimal historicMaxDailySpent = BigDecimal.valueOf(300);
    @CreationTimestamp
    private LocalDateTime creationDateTime;
    @UpdateTimestamp
    private LocalDateTime lastUpdateDateTime;

    public Account(Money balance, AccountHolder primaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
    }
}
