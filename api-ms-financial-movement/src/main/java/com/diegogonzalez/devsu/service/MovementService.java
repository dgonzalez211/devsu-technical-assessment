package com.diegogonzalez.devsu.service;

import com.diegogonzalez.devsu.dto.request.MovementCreateRequestDTO;
import com.diegogonzalez.devsu.dto.response.MovementResponseDTO;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
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
public interface MovementService {

    MovementResponseDTO register(MovementCreateRequestDTO movimientoRequest);

    List<MovementResponseDTO> report(UUID customerId, LocalDate startDate, @Nullable LocalDate endDate);
}
