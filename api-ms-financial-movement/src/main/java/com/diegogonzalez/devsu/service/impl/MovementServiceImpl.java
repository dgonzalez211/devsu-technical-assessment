package com.diegogonzalez.devsu.service.impl;

import com.diegogonzalez.devsu.component.mapper.MovementMapper;
import com.diegogonzalez.devsu.dto.request.MovementCreateRequestDTO;
import com.diegogonzalez.devsu.dto.response.MovementResponseDTO;
import com.diegogonzalez.devsu.entity.Account;
import com.diegogonzalez.devsu.entity.Movement;
import com.diegogonzalez.devsu.event.publisher.MovementCreatedPublisher;
import com.diegogonzalez.devsu.exception.ApplicationResponse;
import com.diegogonzalez.devsu.exception.MicroserviceException;
import com.diegogonzalez.devsu.repository.MovementRepository;
import com.diegogonzalez.devsu.service.AccountService;
import com.diegogonzalez.devsu.service.MovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
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
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;
    private final AccountService accountService;
    private final MovementCreatedPublisher movementCreatedPublisher;

    @Override
    @Transactional
    public MovementResponseDTO register(MovementCreateRequestDTO request) {

        Account account = accountService.findAccountById(request.getAccountId());
        BigDecimal movementAmount = request.getAmount();

        if (account.getCurrentBalance().add(movementAmount).compareTo(BigDecimal.ZERO) < 0) {
            throw new MicroserviceException(ApplicationResponse.INSUFFICIENT_BALANCE, HttpStatus.BAD_REQUEST.value());
        }

        Movement movement = Movement.builder()
                .account(account)
                .amount(movementAmount)
                .balance(account.getCurrentBalance().add(movementAmount))
                .build();

        movementRepository.save(movement);
        movementCreatedPublisher.publishEvent(movement);
        return MovementResponseDTO.builder()
                .accountId(account.getUuid())
                .balance(account.getCurrentBalance())
                .amount(request.getAmount())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponseDTO> report(UUID customerId, LocalDate startDate, LocalDate endDate) {

        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        LocalDate queryEndDate = Objects.requireNonNullElse(endDate, now);

        if (startDate.isAfter(now) || queryEndDate.isAfter(now)) {
            throw new MicroserviceException(ApplicationResponse.INVALID_ARGS, HttpStatus.BAD_REQUEST.value());
        }

        if (queryEndDate.isBefore(startDate)) {
            throw new MicroserviceException(ApplicationResponse.INVALID_ARGS, HttpStatus.BAD_REQUEST.value());
        }

        LocalDateTime fechaStart = startDate.atStartOfDay();
        LocalDateTime fechaEnd = startDate.atTime(23, 59, 59);
        List<Movement> movimientos = movementRepository.findAllByCustomerBetween(customerId, fechaStart, fechaEnd, Sort.by("date").descending());

        return MovementMapper.INSTANCE.toResponseDtoList(movimientos);
    }
}
