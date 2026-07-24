package com.studentautomation.service;

import com.studentautomation.entity.AuditLog;
import com.studentautomation.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(String userEmail, String userRole, String action, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserEmail(userEmail != null ? userEmail : "SYSTEM");
        auditLog.setUserRole(userRole != null ? userRole : "SYSTEM");
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }
}
