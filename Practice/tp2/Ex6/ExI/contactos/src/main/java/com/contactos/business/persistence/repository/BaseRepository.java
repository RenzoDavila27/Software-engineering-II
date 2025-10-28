package com.contactos.business.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.contactos.business.domain.BaseEntity;


@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>, ID> extends JpaRepository<T, ID> {
}
