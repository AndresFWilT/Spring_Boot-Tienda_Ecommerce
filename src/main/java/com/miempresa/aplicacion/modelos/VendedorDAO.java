package com.miempresa.aplicacion.modelos;

import org.springframework.data.repository.CrudRepository;

public interface VendedorDAO extends CrudRepository<Vendedor,String> {
    Vendedor findByCodVendedor(String codVendedor);  
}
