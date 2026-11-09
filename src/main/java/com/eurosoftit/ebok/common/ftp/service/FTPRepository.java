package com.eurosoftit.ebok.common.ftp.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FTPRepository extends JpaRepository<FileUploadInfoEntity, Long>, JpaSpecificationExecutor<FileUploadInfoEntity> {
    @Modifying
    @Query("Update FileUploadInfoEntity e set e.confirmed = :date WHERE e.id = :id")
    void setConfirmed(@Param("id") Long id, @Param("date") LocalDateTime date);

    @Modifying
    @Query("Update FileUploadInfoEntity e set e.viewed = :date WHERE e.id = :id")
    void setViewed(@Param("id") Long id, @Param("date") LocalDateTime date);

    @Query("SELECT (case when count(e.id) > 0 then 'true' else 'false' end) from FileUploadInfoEntity e WHERE e.path = :path " +
        "and e.name = :name and e.userId = :userId")
    boolean existsByName(@Param("path") String path, @Param("name") String name, @Param("userId") Long userId);

    @Query("SELECT e FROM FileUploadInfoEntity e WHERE e.path = :path and e.name = :name and e.userId = :userId")
    FileUploadInfoEntity findByName(@Param("path") String path, @Param("name") String name, @Param("userId") Long userId);

    @Query("SELECT (case when count(e.id) > 0 then 'true' else 'false' end) from FileUploadInfoEntity e WHERE e.id = :id " +
        "and e.confirmed is not null")
    boolean isConfirmed(@Param("id") Long id);
}
