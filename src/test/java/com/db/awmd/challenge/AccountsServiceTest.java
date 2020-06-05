package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.FundTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void fundTransfer() throws Exception {
		String uniqueSenderAccountId = "Id-" + System.currentTimeMillis() + "-1";
		
		Account uniqueSenderAccount = new Account(uniqueSenderAccountId, new BigDecimal("12000.00"));
		this.accountsService.createAccount(uniqueSenderAccount);

		String uniqueReceiverAccountId = "Id-" + System.currentTimeMillis() + "-2";
		Account uniqueReceiverAccount = new Account(uniqueReceiverAccountId, new BigDecimal("5000.00"));
		this.accountsService.createAccount(uniqueReceiverAccount);

		FundTransfer account = new FundTransfer(uniqueSenderAccountId, uniqueReceiverAccountId,
				new BigDecimal("3000.00"));
		this.accountsService.fundTransfer(account);
		Account senderAccount = accountsService.getAccount(uniqueSenderAccountId);

		assertThat(senderAccount.getAccountId()).isEqualTo(uniqueSenderAccountId);
		assertThat(senderAccount.getBalance()).isEqualByComparingTo("9000");

		Account receiveraccount = accountsService.getAccount(uniqueReceiverAccountId);
		assertThat(receiveraccount.getAccountId()).isEqualTo(uniqueReceiverAccountId);
		assertThat(receiveraccount.getBalance()).isEqualByComparingTo("8000");
	}
}
