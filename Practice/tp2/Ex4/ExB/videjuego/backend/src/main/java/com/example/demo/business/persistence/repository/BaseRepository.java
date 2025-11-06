package com.example.demo.business.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.demo.business.domain.ClaseBase;

@NoRepositoryBean
public interface BaseRepository<T extends ClaseBase> extends JpaRepository<T, Long> {
}
