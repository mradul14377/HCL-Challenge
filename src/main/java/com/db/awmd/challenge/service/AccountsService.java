package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.FundTransfer;
import com.db.awmd.challenge.exception.FundTransferException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.web.AccountsController;

@Service
public class AccountsService {

	static Logger LOG = LoggerFactory.getLogger(AccountsController.class);
	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	private EmailNotificationService emailNotificationService;

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public AccountsRepository getAccountsRepository() {
		return accountsRepository;
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public EmailNotificationService getEmailNotificationService() {
		return emailNotificationService;
	}

	public void setEmailNotificationService(EmailNotificationService emailNotificationService) {
		this.emailNotificationService = emailNotificationService;
	}

	public void fundTransfer(FundTransfer accountInformation) {
		Account senderAccountDetails = accountsRepository.getAccount(accountInformation.getSenderAccountId());
		Account receiverAccountDetails = accountsRepository.getAccount(accountInformation.getReceiverAccountId());
		BigDecimal remainingAmount;
		if (checkSenderAccountExist(senderAccountDetails) && checkRecieverAccountExist(receiverAccountDetails)) {
			if (senderAccountDetails.getBalance().compareTo(accountInformation.getAmountToTransfer()) == 1) {
				LOG.info("Amount to be transfered: {}", accountInformation.getAmountToTransfer());
				remainingAmount = senderAccountDetails.getBalance().subtract(accountInformation.getAmountToTransfer());
				LOG.info("Remaining amount after successfull transaction {}", remainingAmount);
				emailNotificationService.notifyAboutTransfer(senderAccountDetails,
						"Amount Successfully debited from your account");
				emailNotificationService.notifyAboutTransfer(receiverAccountDetails,
						"Amount Successfully Credited to your account");
				senderAccountDetails.setBalance(remainingAmount);
				receiverAccountDetails
						.setBalance(receiverAccountDetails.getBalance().add(accountInformation.getAmountToTransfer()));
			} else {
				throw new FundTransferException(
						"Your total balance must be greater than or equal to the transfer amount");
			}
		} else if (!StringUtils
				.isEmpty(accountsRepository.getAccount(accountInformation.getReceiverAccountId()).getAccountId()))
			throw new FundTransferException("Receiver Account does not exist .Please check the account number again");

	}

	private boolean checkSenderAccountExist(Account senderAccountDetails) {
		boolean accountExist = true;
		if (senderAccountDetails != null) {
			LOG.info("Account number and balance of the sender {} : {}", senderAccountDetails.getAccountId(),
					senderAccountDetails.getBalance());
			return accountExist;
		} else {
			accountExist = false;
			throw new FundTransferException("Sender Account not present in the system. Please check again");
		}

	}

	private boolean checkRecieverAccountExist(Account receiverAccountDetails) {
		boolean accountExist = true;
		if (receiverAccountDetails != null) {
			LOG.info("Account number and balance of the receiver {} : {}", receiverAccountDetails.getAccountId(),
					receiverAccountDetails.getBalance());
			return accountExist;
		} else {
			accountExist = false;
			throw new FundTransferException("Receiver Account not present in the system. Please check again");
		}
		
	}

}
