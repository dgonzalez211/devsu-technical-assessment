package com.diegogonzalez.devsu.repository;

import com.diegogonzalez.devsu.entity.Movement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    List<Movement> findAllByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT m, c FROM Movement m LEFT JOIN m.account c  " +
            " LEFT JOIN c.customer c1 WHERE c1.customerId = ?1 AND m.date between ?2 AND ?3")
    List<Movement> findAllByCustomerBetween(String customerId, LocalDateTime startDate, LocalDateTime endDate, Sort sort);
}
