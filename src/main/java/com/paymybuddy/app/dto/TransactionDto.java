package com.paymybuddy.app.dto;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDto {
	private String username;
    private String description;
    private Double amount;

    
}
