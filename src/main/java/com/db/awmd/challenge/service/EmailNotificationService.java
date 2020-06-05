package com.db.awmd.challenge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.web.AccountsController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailNotificationService implements NotificationService {
	
	static Logger LOG = LoggerFactory.getLogger(AccountsController.class);

  @Override
  public void notifyAboutTransfer(Account account, String transferDescription) {
    //THIS METHOD SHOULD NOT BE CHANGED - ASSUME YOUR COLLEAGUE WILL IMPLEMENT IT
	  LOG
      .info("Sending notification to owner of {}: {}", account.getAccountId(), transferDescription);
  }

}
