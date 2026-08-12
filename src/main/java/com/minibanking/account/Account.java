package com.minibanking.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * The Account entity represents a table in the database.
 * @Entity tells Spring Data JPA that this class is mapped to a database table.
 * @Table specifies the name of the table in the database.
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    /**
     * @Id marks this field as the primary key.
     * @GeneratedValue specifies how the primary key should be generated (auto-increment in MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @NotBlank ensures this field cannot be null or just whitespace.
     */
    @NotBlank(message = "Owner name is required")
    @Column(nullable = false)
    private String ownerName;

    /**
     * BigDecimal is generally preferred over double for financial calculations to avoid rounding errors.
     * @PositiveOrZero ensures the balance cannot be negative.
     */
    @PositiveOrZero(message = "Balance cannot be negative")
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    public Account(String ownerName, BigDecimal balance) {
        this.ownerName = ownerName;
        this.balance = balance;
    }
}
