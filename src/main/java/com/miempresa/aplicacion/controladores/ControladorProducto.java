package com.miempresa.aplicacion.controladores;

import com.miempresa.aplicacion.dtos.ProductoDto;
import com.miempresa.aplicacion.modelos.Factura;
import com.miempresa.aplicacion.modelos.Producto;
import java.util.ArrayList;
import java.util.List;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import com.miempresa.aplicacion.modelos.ProductoDAO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ControladorProducto {
    
    private final ProductoDAO repositorioProducto;
    
    @GetMapping("/productos") //path del controlador
    public String getTodosLosProductos(Model model){
        Iterable<Producto> productos = repositorioProducto.findAll();
        model.addAttribute("productos",productos);
        return "vistaProducto";
    }    
    
    @GetMapping("/productos/{codigoProducto}") //path del controlador
    public String getProductoById(@PathVariable String codigoProducto, Model model){
        List<String> listaProducto = new ArrayList<>();
        listaProducto.add(codigoProducto);
        Iterable<Producto> productos = repositorioProducto.findAllById(listaProducto);
        model.addAttribute("productos",productos);
        return "vistaProducto";
    }
 
    @GetMapping("/crear/producto") //path del controlador
    public String crearProducto(Model model){
        
        model.addAttribute("producto",new Producto());
        return "vistaCrearProducto";
    }   
    
    @PostMapping("/crear/producto")
    public RedirectView procesarProducto(@ModelAttribute Producto producto){
       Producto productoGuardado = repositorioProducto.save(producto);
       if (productoGuardado == null){
           return new RedirectView("/crear/producto/",true);
       }
       return new RedirectView("/productos/",true);
    }    
    
    //Editar es para eliminar o actulizar
    @GetMapping("/editar/producto")
    public String editarProducto(Model model){
        Iterable<Producto> productos = repositorioProducto.findAll();
        model.addAttribute("productos", productos);
        return "vistaEditarProducto";
    }
    
    @GetMapping("/actualizar/producto") //path del controlador
    public String actualizarProducto(Model model) {
        model.addAttribute("producto", new ProductoDto());
        return "vistaActualizarProducto";
    }
    
    @PostMapping("/actualizar/producto")
    public RedirectView editarProducto(Producto producto){
        String codProducto = producto.getCodProducto();
        producto = repositorioProducto.findByCodProducto(codProducto);
        log.info("producto seleccionado "+producto);
        Producto p = repositorioProducto.findByCodProducto(codProducto);
        return new RedirectView("redirect:/vistaProducto",true);
    }
    
    @GetMapping("/eliminar/producto")
    public String eliminarProducto(Producto producto){
        String codProducto = producto.getCodProducto();
        producto = repositorioProducto.findByCodProducto(codProducto);
        log.info("producto seleccionado "+producto);
        repositorioProducto.delete(producto);
        return "redirect:/editar/producto";
    }
}
