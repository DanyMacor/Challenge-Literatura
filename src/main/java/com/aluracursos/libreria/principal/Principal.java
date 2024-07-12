package com.aluracursos.libreria.principal;

import com.aluracursos.libreria.modelo.Autor;
import com.aluracursos.libreria.modelo.DatosGral;
import com.aluracursos.libreria.modelo.DatosLibro;
import com.aluracursos.libreria.modelo.Libro;
import com.aluracursos.libreria.repository.AutorRepository;
import com.aluracursos.libreria.repository.LibroRepository;
import com.aluracursos.libreria.servicio.ConsumoAPI;
import com.aluracursos.libreria.servicio.ConvierteDatos;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();


    private AutorRepository autorRepository;
    private LibroRepository libroRepository;

    public Principal(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    public void mostrarMenu() {

        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, DatosGral.class);
        System.out.println(datos);

        var opcion = -1;
        while(opcion != 0) {
            var menu = """
                    1 - Buscar libros 
                    2 - Mostrar libros registrados
                    3 - Mostrar autores registrados
                    4 - Mostrar autores vivos
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    try{
                        buscarLibro();
                    }catch (Exception e){
                        System.out.println("Error al buscar el libro");
                    }
                    break;
                case 2:
                    obtenerLibros();
                    break;
                case 3:
                    obtenerAutor();
                    break;
                case 4:
                    obtenerAutorVivo();
                    break;

                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }


    private void obtenerAutorVivo() {
        System.out.println("Por favor escribe el año que desees buscar:");
        var anio = teclado.nextInt();
        teclado.nextLine();
        List<Autor> autors = autorRepository.findByFechaNacimientoLessThanEqualAndFechaDefuncionGreaterThanEqual(anio, anio);
        if (autors.isEmpty()) {
            System.out.println("No se pudo encontrar a un autor que haya nacido entre el año:  " + anio);
        } else {
            for (Autor autor : autors) {
                System.out.println(autor.toString());
            }
        }
    }


    private void buscarLibro() {
        System.out.println("Por favor escribe el nombre del libro que desees buscar:");
        var tituloLibro = teclado.nextLine();
        var jsonLibro = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(jsonLibro, DatosGral.class);
        Optional<DatosLibro> libroBuscado = datosBusqueda.results().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if(libroBuscado.isPresent()) {
            System.out.println("Libro encontrado: \n" + libroBuscado.get());
            Libro libroVerificado = libroRepository.findByTituloContainsIgnoreCase(libroBuscado.get().titulo());
            if (libroVerificado == null) {
                System.out.println("Libro no encontrado en la base de datos, ¿Deseas guardarlo? (S/N)");
                var respuesta = teclado.nextLine();
                if (respuesta.equalsIgnoreCase("S")) {
                    Libro libro = new Libro(libroBuscado.get());
                    libroRepository.save(libro);
                    System.out.println("Libro guardado exitosamente");
                }
            } else {
                System.out.println("Libro no encontrado");
            }
        }
    }

    private void obtenerLibros(){
        List<Libro> libros = libroRepository.findAll();
        try{
            if(libros.isEmpty()){
                System.out.println("No hay libros registrados");
            }else{
                for(Libro libro : libros){
                    System.out.println(libro.toString());
                }
            }
        }catch (Exception e){
            System.out.println("Error al mostrar los libros");
        }
    }

    private void obtenerAutor() {
        List<Libro> libros = libroRepository.findAll();
        List<String> autores = libros.stream()
                .map(libro -> libro.getAutor().getNombre())
                .distinct()
                .collect(Collectors.toList());
        try{
            if(autores.isEmpty()){
                System.out.println("No hay autores registrados");
            }else{
                for(String autor : autores){
                    System.out.println("\n3Autor: " + autor + "\n" + "Libros: " + libros.stream()
                            .filter(libro -> libro.getAutor().getNombre().equals(autor))
                            .map(Libro::getTitulo)
                            .collect(Collectors.toList()));
                }
            }
        }catch (Exception e){
            System.out.println("Error al mostrar los autores");
        }
    }
}
