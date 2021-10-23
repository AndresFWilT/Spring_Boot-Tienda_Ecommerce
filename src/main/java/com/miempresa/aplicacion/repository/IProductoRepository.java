package com.miempresa.aplicacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miempresa.aplicacion.model.Producto;
import java.util.List;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findBynombre(String nombre);
}
