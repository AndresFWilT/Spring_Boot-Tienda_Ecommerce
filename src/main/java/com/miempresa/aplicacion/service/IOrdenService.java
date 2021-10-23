package com.miempresa.aplicacion.service;

import java.util.List;
import java.util.Optional;

import com.miempresa.aplicacion.model.Orden;
import com.miempresa.aplicacion.model.Usuario;

public interface IOrdenService {

    List<Orden> findAll();

    Optional<Orden> findById(Integer id);

    Orden save(Orden orden);

    String generarNumeroOrden();

    List<Orden> findByUsuario(Usuario usuario);
}
