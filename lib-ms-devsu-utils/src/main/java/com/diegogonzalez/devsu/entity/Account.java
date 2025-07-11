package com.diegogonzalez.devsu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/*
 * Author: Diego González
 *
 * This code is the exclusive property of the author. Any unauthorized use,
 * distribution, or modification is prohibited without the author's explicit consent.
 *
 * Disclaimer: This code is provided "as is" without any warranties of any kind,
 * either express or implied, including but not limited to warranties of merchantability
 * or fitness for a particular purpose.
 */
@Entity
@Table(
        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_account_account_number", columnNames = {"accountNumber"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"customer", "movements"})
@EqualsAndHashCode(of = {"id", "accountNumber"})
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", updatable = false, nullable = false, unique = true)
    private UUID uuid;

    @NotBlank(message = "Account number is required")
    @Column(name = "accountNumber", length = 20, nullable = false, unique = true)
    private String accountNumber;

    @NotNull(message = "Account type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "accountType", length = 20, nullable = false)
    private AccountType accountType;

    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Initial balance must be zero or positive")
    @Column(name = "initialBalance", precision = 19, scale = 4, nullable = false)
    private BigDecimal initialBalance;

    @NotNull(message = "Current balance is required")
    @Column(name = "currentBalance", precision = 19, scale = 4, nullable = false)
    private BigDecimal currentBalance;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private AccountStatus status;

    @Column(name = "currencyCode", length = 3, nullable = false)
    @Builder.Default
    private String currencyCode = Currency.getInstance("USD").getCurrencyCode();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "openedAt", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "lastTransactionAt")
    private LocalDateTime lastTransactionAt;

    @Column(name = "closedAt")
    private LocalDateTime closedAt;

    @Column(name = "interestRate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "overdraftLimit", precision = 19, scale = 4)
    private BigDecimal overdraftLimit;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<Movement> movements = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        openedAt = LocalDateTime.now();
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        if (currentBalance == null) {
            currentBalance = initialBalance;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Account account = (Account) o;
        return getId() != null && Objects.equals(getId(), account.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public enum AccountType {
        SAVINGS,
        CHECKING,
        LOAN,
        CREDIT,
        INVESTMENT,
        FIXED_DEPOSIT
    }

    public enum AccountStatus {
        ACTIVE,
        INACTIVE,
        FROZEN,
        CLOSED,
        PENDING_APPROVAL
    }
}
