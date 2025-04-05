package com.diegogonzalez.devsu.event.listener;

import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Movement;
import com.diegogonzalez.devsu.event.movement.MovementCreatedEvent;
import com.diegogonzalez.devsu.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
@Component
@RequiredArgsConstructor
public class MovementCreatedListener implements ApplicationListener<MovementCreatedEvent> {
    private final AccountRepository accountRepository;

    @Override
    public void onApplicationEvent(MovementCreatedEvent event) {
        Movement movement = event.getMovement();
        Account account = accountRepository.findByAccountNumber(movement.getAccount().getAccountNumber()).orElseThrow();
        BigDecimal newBalance = account.getCurrentBalance().add(movement.getAmount());
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);
    }
}
