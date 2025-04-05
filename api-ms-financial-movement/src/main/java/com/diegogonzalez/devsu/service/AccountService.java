package com.diegogonzalez.devsu.service;

import com.diegogonzalez.devsu.dto.AccountDTO;
import com.diegogonzalez.devsu.dto.request.AccountCreateRequestDTO;
import com.diegogonzalez.devsu.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
public interface AccountService {

    Page<AccountDTO> listAccounts(Pageable pageable);

    AccountDTO findAccountByAccountNumber(String accountNumber);

    Account findAccountById(UUID accountId);

    AccountDTO createAccount(AccountCreateRequestDTO request);

    void deleteAccount(String accountNumber);

}
