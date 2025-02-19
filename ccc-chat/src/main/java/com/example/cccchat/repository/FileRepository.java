package com.example.cccchat.repository;

import com.example.cccchat.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByFileUrl(String fileUrl);
    @Transactional
    void deleteByFileUrl(String fileUrl);
}
