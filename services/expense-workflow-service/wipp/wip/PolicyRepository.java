package com.smartexpense.workflow.repositories;



import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartexpense.workflow.entities.Policy;


@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByCategoryAndEffectiveFromBeforeAndEffectiveToAfter(String category, LocalDate now1, LocalDate now2);
}