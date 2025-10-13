package com.books.demo.bussiness.persistance;

import com.books.demo.bussiness.domain.ArchivoLibro;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivoLibroRepository extends JpaRepository<ArchivoLibro, Long> {

    Optional<ArchivoLibro> findByLibroId(Long libroId);

    void deleteByLibroId(Long libroId);
}
