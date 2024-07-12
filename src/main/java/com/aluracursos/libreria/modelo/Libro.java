package com.aluracursos.libreria.modelo;

import jakarta.persistence.*;

import java.util.Optional;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
        private Long id;

    @Column(unique = true)
    private String titulo;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Autor autor;
    private String idioma;
    private Integer numeroDescargas;

    public Libro(DatosLibro dtLibro){
        this.titulo =dtLibro.titulo();
        this.idioma = dtLibro.idiomas().get(0);
        this.numeroDescargas = dtLibro.numeroDeDescargas();
        Optional<DatosAutor> autor =dtLibro.autor().stream().findFirst();
        if (autor.isPresent()){
            this.autor = new Autor(autor.get().nombre(), autor.get().fechaNacimiento(), autor.get().fechaDefuncion());
        }else {
            System.out.println("No se encontró el autor de: " + this.titulo);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Integer numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    @Override
    public String toString(){
        return
                "Titulo de libro" + titulo + '\'' +
                "Autor" + autor + '\'' +
                "Descargas del libro" + numeroDescargas + '\'';
    }
}
