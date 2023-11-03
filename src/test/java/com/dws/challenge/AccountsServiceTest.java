package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.exception.InvalidAccountException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Mock
  private EmailNotificationService emailNotificationService;

  @Autowired
  private AccountsRepository accountsRepository;

  @InjectMocks
  private AccountsService accountsService;


  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    accountsService = new AccountsService(accountsRepository, emailNotificationService);
  }

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
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
  public void testTransferMoneySuccess() {
    // Arrange
    Account sourceAccount = new Account("Id-124");
    sourceAccount.setBalance(new BigDecimal(1000));
    Account destinationAccount = new Account("Id-125");
    accountsService.createAccount(sourceAccount);
    accountsService.createAccount(destinationAccount);

    // Act
    accountsService.transferMoney("Id-124", "Id-125", new BigDecimal(500));

    // Assert
    assertEquals(new BigDecimal(500), accountsService.getAccount("Id-124").getBalance());
    assertEquals(new BigDecimal(500), accountsService.getAccount("Id-125").getBalance());

    verify(emailNotificationService, times(1)).notifyAboutTransfer(eq(accountsService.getAccount("Id-124")), eq("Amount of Rs. 500.00 transferred to account Id-125"));
    verify(emailNotificationService, times(1)).notifyAboutTransfer(eq(accountsService.getAccount("Id-125")), eq("Amount of Rs. 500.00 transferred from account Id-124"));
  }

  @Test
  public void testTransferMoneyInvalidSourceAccount() {
    // Arrange
    Account destinationAccount = new Account("destinationAccountId");
    accountsService.createAccount(destinationAccount);

    // Act and Assert
    assertThrows(InvalidAccountException.class, () -> {
      accountsService.transferMoney("nonExistentSourceAccount", "destinationAccountId", new BigDecimal(500));
    });
  }

  @Test
  public void testTransferMoneyInvalidDestinationAccount() {
    // Arrange
    Account sourceAccount = new Account("Id-126");
    sourceAccount.setBalance(new BigDecimal(1000));
    accountsService.createAccount(sourceAccount);

    // Act and Assert
    assertThrows(InvalidAccountException.class, () -> {
      accountsService.transferMoney("Id-126", "nonExistentDestinationAccount", new BigDecimal(500));
    });
  }

  @Test
  public void testTransferMoneyInsufficientBalance() {
    // Arrange
    Account sourceAccount = new Account("Id-127");
    sourceAccount.setBalance(new BigDecimal(100));
    Account destinationAccount = new Account("Id-128");
    accountsService.createAccount(sourceAccount);
    accountsService.createAccount(destinationAccount);

    // Act and Assert
    assertThrows(InsufficientBalanceException.class, () -> {
      accountsService.transferMoney("Id-127", "Id-128", new BigDecimal(500));
    });
  }

}
