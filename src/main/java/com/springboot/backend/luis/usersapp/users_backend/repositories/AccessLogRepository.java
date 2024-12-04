package com.springboot.backend.luis.usersapp.users_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.backend.luis.usersapp.users_backend.entities.AccessLog;
import com.springboot.backend.luis.usersapp.users_backend.entities.User;

import java.util.Date;
import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findByUserOrderByAccessTimeDesc(User user);
    List<AccessLog> findByAccessTimeBetween(Date startDate, Date endDate);
}
