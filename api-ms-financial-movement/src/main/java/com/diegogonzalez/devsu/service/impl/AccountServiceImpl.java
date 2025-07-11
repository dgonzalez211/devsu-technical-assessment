package com.diegogonzalez.devsu.service.impl;

import com.diegogonzalez.devsu.component.mapper.AccountMapper;
import com.diegogonzalez.devsu.dto.AccountDTO;
import com.diegogonzalez.devsu.dto.request.AccountCreateRequestDTO;
import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Customer;
import com.diegogonzalez.devsu.exception.ApplicationResponse;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import com.diegogonzalez.devsu.repository.AccountRepository;
import com.diegogonzalez.devsu.service.AccountService;
import com.diegogonzalez.devsu.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * Author: Diego González
 *
 * This code is the exclusive property of the author. Any unauthorized use,
 * distribution, or modification is prohibited without the author's explicit consent.
 *
 * Disclaimer: This code is provided "as is" without any warranties of any kind,
 * either express or implied, including but not limited to warranties of merchantability
 * or fitness for a particular purpose.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerService customerService;


    @Override
    @Transactional(readOnly = true)
    public Page<AccountDTO> listAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(AccountMapper.INSTANCE::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public AccountDTO findAccountByAccountNumber(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);

        if (account.isEmpty()) {
            throw new MicroserviceException(ApplicationResponse.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND.value());
        }

        return AccountMapper.INSTANCE.toDto(account.get());
    }

    @Override
    public Account findAccountById(UUID accountId) {
        log.info("Finding account by UUID: {}", accountId);
        Optional<Account> account = accountRepository.findByUuid(accountId);

        if (account.isEmpty()) {
            log.info("Account not found by exact UUID, trying with UUID prefix");
            String uuidPrefix = accountId.toString().substring(0, 8);
            List<Account> accounts = accountRepository.findByUuidStartingWith(uuidPrefix);

            if (!accounts.isEmpty()) {
                Account foundAccount = accounts.get(0);
                log.info("Found account with similar UUID: {}", foundAccount.getUuid());
                return foundAccount;
            }

            log.error("Account not found with UUID: {} or prefix: {}", accountId, uuidPrefix);
            throw new MicroserviceException(ApplicationResponse.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND.value());
        }

        return account.get();
    }


    @Override
    @Transactional()
    public AccountDTO createAccount(AccountCreateRequestDTO request) {

        if (request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new MicroserviceException(ApplicationResponse.INVALID_ARGS, HttpStatus.BAD_REQUEST.value());
        }

        Customer customer = customerService.findCustomer(request.getCustomerId());
        Account account = Account.builder()
                .customer(customer)
                .accountNumber(request.getAccountNumber())
                .accountType(request.getAccountType())
                .initialBalance(request.getInitialBalance())
                .status(Account.AccountStatus.ACTIVE)
                .build();
        accountRepository.save(account);
        return AccountMapper.INSTANCE.toDto(account);
    }


    @Override
    @Transactional()
    public void deleteAccount(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            throw new MicroserviceException(ApplicationResponse.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND.value());
        }

        account.get().setStatus(Account.AccountStatus.INACTIVE);
        accountRepository.save(account.get());
    }

}
