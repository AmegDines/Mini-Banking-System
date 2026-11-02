package com.minibanking.transaction;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column private String senderAccNo;
    @Column private String receiverAccNo;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 20) private String transactionType;
    @Column(nullable = false) private LocalDateTime timestamp = LocalDateTime.now();
    @Column(nullable = false, length = 20) private String status = "SUCCESS";
    public Transaction() {}
    public Transaction(String sender, String receiver, BigDecimal amount, String type) {
        this.senderAccNo = sender; this.receiverAccNo = receiver; this.amount = amount; this.transactionType = type;
    }
    public Long getId() { return id; }
    public String getSenderAccNo() { return senderAccNo; }
    public String getReceiverAccNo() { return receiverAccNo; }
    public BigDecimal getAmount() { return amount; }
    public String getTransactionType() { return transactionType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}
