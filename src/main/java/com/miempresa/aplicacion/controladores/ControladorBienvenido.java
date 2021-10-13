package com.miempresa.aplicacion.controladores;

import com.miempresa.aplicacion.modelos.Producto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ControladorBienvenido {

    @GetMapping("/bienvenido") //path del controlador
    public String bienvenido() {
        return "vistaBienvenido";
    }

    @PostMapping(value = "/vistaAgregarProducto")
    public String agregarProducto(@ModelAttribute Producto producto) {
        return "redirect:/vistaCrearProducto";
    }
    
    
}
