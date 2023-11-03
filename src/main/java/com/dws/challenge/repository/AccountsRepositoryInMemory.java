package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.exception.InvalidAccountException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, Lock> accountLocks = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    @Override
    public void transferMoney(String fromAccountId, String toAccountId, BigDecimal amount)
            throws InsufficientBalanceException, InvalidAccountException {
        // Acquire locks for both accounts
        Lock fromAccountLock = accountLocks.computeIfAbsent(fromAccountId, k -> new ReentrantLock());
        Lock toAccountLock = accountLocks.computeIfAbsent(toAccountId, k -> new ReentrantLock());

        fromAccountLock.lock();
        toAccountLock.lock();

        try {
            Account fromAccount = accounts.get(fromAccountId);
            Account toAccount = accounts.get(toAccountId);

            if (fromAccount == null || toAccount == null) {
                throw new InvalidAccountException("One or both account IDs are invalid.");
            }

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance in the source account.");
            }

            accounts.put(fromAccountId, Account.builder().accountId(fromAccountId)
                    .balance(fromAccount.getBalance().subtract(amount)).build());
            accounts.put(toAccountId, Account.builder().accountId(toAccountId)
                    .balance(toAccount.getBalance().add(amount)).build());
        } finally {
            // Always release the locks to avoid deadlocks
            fromAccountLock.unlock();
            toAccountLock.unlock();
        }
    }

}
