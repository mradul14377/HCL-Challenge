package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.FundTransfer;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void createAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		Account account = accountsService.getAccount("Id-123");
		assertThat(account.getAccountId()).isEqualTo("Id-123");
		assertThat(account.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void createDuplicateAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoBalance() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoBody() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNegativeBalance() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountEmptyAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void getAccount() throws Exception {
		String uniqueAccountId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId)).andExpect(status().isOk())
				.andExpect(content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
	}

	@Test
	public void fundTransferAccount() throws Exception {

		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-10012\",\"balance\":30000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts/createAccount").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-10005\",\"balance\":20000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts/fundTransfer").contentType(MediaType.APPLICATION_JSON).content(
				"{\"senderAccountId\":\"Id-10012\",\"receiverAccountId\":\"Id-10005\",\"amountToTransfer\":1000}"))
				.andExpect(status().isCreated());

		Account senderAccount = accountsService.getAccount("Id-10012");
		assertThat(senderAccount.getAccountId()).isEqualTo("Id-10012");
		assertThat(senderAccount.getBalance()).isEqualByComparingTo("29000");

		Account receiveraccount = accountsService.getAccount("Id-10005");
		assertThat(receiveraccount.getAccountId()).isEqualTo("Id-10005");
		assertThat(receiveraccount.getBalance()).isEqualByComparingTo("21000");

	}

	@Test
	public void fundTransfer() throws Exception {
		String uniqueSenderAccountId = "Id-" + System.currentTimeMillis() + "-1";
		;
		Account uniqueSenderAccount = new Account(uniqueSenderAccountId, new BigDecimal("12000.00"));
		this.accountsService.createAccount(uniqueSenderAccount);

		String uniqueReceiverAccountId = "Id-" + System.currentTimeMillis() + "-2";
		Account uniqueReceiverAccount = new Account(uniqueReceiverAccountId, new BigDecimal("5000.00"));
		this.accountsService.createAccount(uniqueReceiverAccount);
		FundTransfer account = new FundTransfer(uniqueSenderAccountId, uniqueReceiverAccountId,
				new BigDecimal("3000.00"));
		this.accountsService.fundTransfer(account);
		this.mockMvc.perform(get("/v1/accounts/" + uniqueSenderAccountId)).andExpect(status().isOk())
				.andExpect(content().string("{\"accountId\":\"" + uniqueSenderAccountId + "\",\"balance\":9000.00}"));
	}
}
