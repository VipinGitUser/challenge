package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  private final EmailNotificationService emailNotificationService;

  private static final String TRANSFER_SUCCESS_MESSAGE = "Amount of Rs. %s transferred %s account %s";

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, EmailNotificationService emailNotificationService) {
    this.accountsRepository = accountsRepository;
    this.emailNotificationService = emailNotificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public void transferMoney(String sourceAccountId, String destinationAccountId, BigDecimal amount) {
     this.accountsRepository.transferMoney(sourceAccountId, destinationAccountId, amount);
     this.emailNotificationService.notifyAboutTransfer(getAccount(sourceAccountId), String.format(TRANSFER_SUCCESS_MESSAGE,amount.setScale(2),"to",destinationAccountId));
     this.emailNotificationService.notifyAboutTransfer(getAccount(destinationAccountId), String.format(TRANSFER_SUCCESS_MESSAGE,amount.setScale(2),"from",sourceAccountId));
  }
}
