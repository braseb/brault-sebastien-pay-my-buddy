package com.paymybuddy.app.dto;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionFormDto {
	@NotBlank(message = "An Email is required")
	private String email;
	
    private String description;
    @NotNull
    @DecimalMin(value = "0.01", message = "The minimum amount is 0,01 €")
    private Double amount;

    
}
