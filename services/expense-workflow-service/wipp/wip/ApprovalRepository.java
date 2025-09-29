package com.smartexpense.workflow.repositories;

import com.smartexpense.workflow.entities.Approval;
import com.smartexpense.workflow.entities.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByApproverIdAndStatus(Long approverId, ApprovalStatus status);
}
