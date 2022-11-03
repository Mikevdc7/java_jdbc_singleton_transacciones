package org.mvallesg.java.jdbc;


import org.mvallesg.java.jdbc.modelo.Categoria;
import org.mvallesg.java.jdbc.modelo.Producto;
import org.mvallesg.java.jdbc.repositorio.ProductoRepositorioImpl;
import org.mvallesg.java.jdbc.repositorio.Repositorio;
import org.mvallesg.java.jdbc.util.ConexionBD;

import java.sql.*;
import java.util.Date;

/*
Si se captura una excepción aquí, las sentencias de close() no se van a ejecutar, por lo que quedarán abiertas tanto
la conexión como la sentencia y el resultado. Para no tener que manejar en el finally excepciones (ya que para que desde
el finally se vean estas variables deben estar declaradas a null fuera del try-catch, y si se da una excepción en el try
el método close() se aplicaría a un null y lanzaría otra excepción), se puede usar el "autoclose", que se realiza usando
el try con recursos.

Mediante el try con recursos, todos se cierran (close()) de forma automática, tanto si sale bien (en el try)
como si sale mal (en el catch). Es un auto-close.
 */


public class EjemploJdbc {
    public static void main(String[] args) {

        /* El try así está implementado para cerrar instancias que deben cerrarse; así nos olvidamos.
        Entre estos paréntesis solamente podemos definir objetos del tipo "recurso", es decir, que por
        detrás implementen el auto-close(), que se puedan cerrar de forma automática. */
        try (Connection con = ConexionBD.getInstance()) {

            Repositorio<Producto> repositorio = new ProductoRepositorioImpl();
            System.out.println("========== listar ==========");
            repositorio.listar().forEach(System.out::println);

            System.out.println("========== obtener por id ==========");
            System.out.println(repositorio.porId(1L));

            System.out.println("========== insertar nuevo producto ==========");
            Producto producto = new Producto();
            producto.setNombre("Teclado Razer mecánico");
            producto.setPrecio(550);
            producto.setFechaRegistro(new Date());

            Categoria categoria = new Categoria();
            categoria.setId(3L); // En la bbdd, el id de la categoría "Computación" sabemos que es 3
            producto.setCategoria(categoria);

            repositorio.guardar(producto);
            System.out.println("Producto guardado con éxito!");

            System.out.println("========== listar ==========");
            repositorio.listar().forEach(System.out::println);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}