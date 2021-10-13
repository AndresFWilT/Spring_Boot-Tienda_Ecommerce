package com.miempresa.aplicacion.modelos;

import org.springframework.data.repository.CrudRepository;

public interface FacturaDAO extends CrudRepository<Factura,Long> {
    Factura findByNumeroFactura(String numeroFactura);
    Factura findByIdVenta(Long idVenta);
}
