package com.paymybuddy.app.dto;

import com.paymybuddy.app.model.Transaction;

import lombok.Data;

@Data
public class TransactionDto {
	private int id;
    private String description;
    private Double amount;

    private String senderUsername;
    private String receiverUsername;

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.senderUsername = transaction.getUserSender().getUsername();
        this.receiverUsername = transaction.getUserReceiver().getUsername();
    }
}
