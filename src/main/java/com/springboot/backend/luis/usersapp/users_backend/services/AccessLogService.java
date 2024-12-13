package com.springboot.backend.luis.usersapp.users_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.springboot.backend.luis.usersapp.users_backend.entities.AccessLog;
import com.springboot.backend.luis.usersapp.users_backend.entities.User;
import com.springboot.backend.luis.usersapp.users_backend.repositories.AccessLogRepository;

@Service
public class AccessLogService {

    @Autowired
    private AccessLogRepository accessLogRepository;

    public AccessLog registerAccess(User user, String accessType) {
        AccessLog log = new AccessLog();
        log.setUser(user);
        log.setAccessTime(Timestamp.valueOf(LocalDateTime.now()));
        log.setAccessType(accessType);
        return accessLogRepository.save(log);
    }

    public List<AccessLog> getUserAccessLogs(User user) {
        return accessLogRepository.findByUserOrderByAccessTimeDesc(user);
    }

    public List<AccessLog> getAccessLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return accessLogRepository.findByAccessTimeBetween(
            Timestamp.valueOf(startDate),
            Timestamp.valueOf(endDate)
        );
    }

    public List<AccessLog> getRecentLogs(int limit) {
        return accessLogRepository.findTop10ByOrderByAccessTimeDesc();
    }
}
