package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@SuppressWarnings("deprecation")
@Data
public class FundTransfer {

	@SuppressWarnings("deprecation")
	@NotNull
	  @NotEmpty
	  private String senderAccountId;
	  
	  @SuppressWarnings("deprecation")
	@NotNull
	  @NotEmpty
	  private String receiverAccountId;

	  @NotNull
	  @Positive(message = "Amount to transfer must be positive.")
	  private BigDecimal amountToTransfer;
	  
	  @JsonCreator
	  public FundTransfer(@NotNull @NotEmpty @JsonProperty("senderAccountId") String senderAccountId, @NotNull @NotEmpty @JsonProperty("receiverAccountId") String receiverAccountId,
				@NotNull @Positive(message = "Amount to transfer must be positive.")@JsonProperty("amountToTransfer") BigDecimal amountToTransfer) {
			super();
			this.senderAccountId = senderAccountId;
			this.receiverAccountId = receiverAccountId;
			this.amountToTransfer = amountToTransfer;
		}

	public String getSenderAccountId() {
		return senderAccountId;
	}

	public void setSenderAccountId(String senderAccountId) {
		this.senderAccountId = senderAccountId;
	}

	public String getReceiverAccountId() {
		return receiverAccountId;
	}

	public void setReceiverAccountId(String receiverAccountId) {
		this.receiverAccountId = receiverAccountId;
	}

	public BigDecimal getAmountToTransfer() {
		return amountToTransfer;
	}

	public void setAmountToTransfer(BigDecimal amountToTransfer) {
		this.amountToTransfer = amountToTransfer;
	}

	
	
	
	  
}
