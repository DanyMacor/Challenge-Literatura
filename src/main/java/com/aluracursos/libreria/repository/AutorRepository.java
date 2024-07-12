package com.aluracursos.libreria.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.aluracursos.libreria.modelo.Autor;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByFechaNacimientoLessThanEqualAndFechaDefuncionGreaterThanEqual(Integer A1, Integer A2);
    Optional<Autor> findByNombreAndFechaNacimiento(String nombre, Integer nacimiento);
}
