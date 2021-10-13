package com.miempresa.aplicacion.modelos;

import org.springframework.data.repository.CrudRepository;

public interface ProductoDAO extends CrudRepository<Producto,String> {
    Producto findByCodProducto(String codProducto);
}
