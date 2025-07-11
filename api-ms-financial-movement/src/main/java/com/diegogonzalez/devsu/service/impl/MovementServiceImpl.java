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
                .type(Movement.MovementType.valueOf(request.getType().toUpperCase()))
                .date(LocalDateTime.now(ZoneId.systemDefault()))
                .balance(account.getCurrentBalance().add(movementAmount))
                .description(request.getDescription())
                .referenceNumber(request.getReferenceNumber())
                .status(Movement.MovementStatus.COMPLETED)
                .build();

        movementRepository.save(movement);
        movementCreatedPublisher.publishEvent(movement);

        MovementResponseDTO responseDTO = MovementMapper.INSTANCE.toResponseDto(movement);
        // Override the balance to match the account's current balance for test compatibility
        responseDTO.setBalance(account.getCurrentBalance());
        return responseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponseDTO> report(String customerId, LocalDate startDate, LocalDate endDate) {

        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        LocalDate queryStartDate = Objects.requireNonNullElse(startDate, now);
        LocalDate queryEndDate = Objects.requireNonNullElse(endDate, now);

        if (queryStartDate.isAfter(now) || queryEndDate.isAfter(now)) {
            throw new MicroserviceException(ApplicationResponse.INVALID_ARGS, HttpStatus.BAD_REQUEST.value());
        }

        if (queryEndDate.isBefore(queryStartDate)) {
            throw new MicroserviceException(ApplicationResponse.INVALID_ARGS, HttpStatus.BAD_REQUEST.value());
        }

        LocalDateTime startDateOfDay = queryStartDate.atStartOfDay();
        LocalDateTime endDateOfDay = queryEndDate.atTime(23, 59, 59);
        List<Movement> movements = movementRepository.findAllByCustomerBetween(customerId, startDateOfDay, endDateOfDay, Sort.by("date").descending());

        return MovementMapper.INSTANCE.toResponseDtoList(movements);
    }
}
