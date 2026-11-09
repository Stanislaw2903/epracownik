package com.eurosoftit.ebok.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserViewRepository extends JpaRepository<UserView, Long> {

    @Query("SELECT u FROM UserView u WHERE " +
        "(u.birthMonth = :birthMonth and u.birthDay = :birthDay) and u.activated = true")
    List<UserView> findByBirthDate(@Param("birthDay") int day, @Param("birthMonth") int month);
}
