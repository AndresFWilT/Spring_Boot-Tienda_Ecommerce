package com.miempresa.aplicacion.dtos;

/**
 *
 * @author Andres Wilches
 */

import java.sql.Date;
import lombok.Data;


@Data
public class ProductoDto {
    private String codProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private double precioProducto;
}