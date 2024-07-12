package com.aluracursos.libreria.repository;

import com.aluracursos.libreria.modelo.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Libro findByTituloContainsIgnoreCase(String titulo);
    List<Libro> findByIdioma(String idioma);
}
