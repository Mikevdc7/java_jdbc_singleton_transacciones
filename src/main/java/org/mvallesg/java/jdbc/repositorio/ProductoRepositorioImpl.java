package org.mvallesg.java.jdbc.repositorio;

import org.mvallesg.java.jdbc.modelo.Categoria;
import org.mvallesg.java.jdbc.modelo.Producto;
import org.mvallesg.java.jdbc.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositorioImpl implements Repositorio<Producto>{

    private Connection getConnection() throws SQLException {
        return ConexionBD.getInstance();
    }

    @Override
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT prod.*, categ.nombre AS categoria " +
                                                  "FROM productos AS prod " +
                                                        "JOIN " +
                                                        "categorias AS categ " +
                                                        "ON prod.categoria_id = categ.id ")){

            while(rs.next()){
                Producto producto = crearProducto(rs);
                productos.add(producto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productos;
    }
    @Override
    public Producto porId(Long id) {
        Producto producto = null;
        try (PreparedStatement stmt = getConnection()
                .prepareStatement("SELECT prod.*, categ.nombre AS categoria " +
"                                      FROM productos AS prod " +
"                                           JOIN " +
"                                           categorias AS categ " +
"                                           ON prod.categoria_id = categ.id " +
                                      "WHERE prod.id = ? ")){
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    producto = crearProducto(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return producto;
    }

    /* Implementamos el guardar() tanto para el UPDATE como para el INSERT ->
        Si id <=0, Update, si no, Insert. */
    @Override
    public void guardar(Producto producto) {
        String sql;
        if (producto.getId() != null && producto.getId()>0) {
            sql = "UPDATE productos " +
                  "SET nombre=?, precio=?, categoria_id=? " +
                  "WHERE id=? ";
        } else{
            sql = "INSERT INTO productos (NOMBRE, PRECIO, CATEGORIA_ID, FECHA_REGISTRO) " +
                    "VALUES (?, ?, ?, ?)";
        }
        try(PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setInt(2, producto.getPrecio());
            stmt.setLong(3, producto.getCategoria().getId());

            if (producto.getId() != null && producto.getId()>0) {
                stmt.setLong(4, producto.getId());
            } else{
                stmt.setDate(4, new Date(producto.getFechaRegistro().getTime()));
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try(PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM productos " +
                                                                          "WHERE id=?")){
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Producto crearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getLong(1));
        producto.setNombre(rs.getString(2));
        producto.setPrecio(rs.getInt(3));
        producto.setFechaRegistro(rs.getDate(4));

        Categoria categoria = new Categoria();
        categoria.setId(rs.getLong("categoria_id"));
        categoria.setNombre(rs.getString("categoria"));
        producto.setCategoria(categoria);

        return producto;
    }
}