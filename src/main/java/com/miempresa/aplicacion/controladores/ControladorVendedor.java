package com.miempresa.aplicacion.controladores;

import com.miempresa.aplicacion.dtos.ProductoDto;
import com.miempresa.aplicacion.dtos.VendedorDto;
import com.miempresa.aplicacion.modelos.Producto;
import com.miempresa.aplicacion.modelos.Vendedor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import com.miempresa.aplicacion.modelos.VendedorDAO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ControladorVendedor {
    
    private final VendedorDAO repositorioVendedor;
    
    @GetMapping("/vendedores") //path del controlador
    public String getTodosLosVendedores(Model model){
        Iterable<Vendedor> vendedores = repositorioVendedor.findAll();
        model.addAttribute("vendedores",vendedores);
        return "vistaVendedor";
    }    
    
    @GetMapping("/vendedores/{codigoVendedor}") //path del controlador
    public String getVendedorById(@PathVariable String codigoVendedor, Model model){
        Vendedor vendedores = repositorioVendedor.findByCodVendedor(codigoVendedor);
        model.addAttribute("vendedores",vendedores);
        return "vistaVendedor";
    }
    
    @GetMapping("/crear/vendedor") //path del controlador
    public String crearVendedor(Model model){
        
        model.addAttribute("vendedor",new Vendedor());
        return "vistaCrearVendedor";
    }   
    
    @PostMapping("/crear/vendedor")
    public RedirectView procesarVendedor(@ModelAttribute Vendedor vendedor){
       Vendedor vendedorGuardado = repositorioVendedor.save(vendedor);
       if (vendedorGuardado == null){
           return new RedirectView("/crear/vendedor/",true);
       }
       return new RedirectView("/vendedores/",true);
    }
    
    //Editar es para eliminar o actulizar
    @GetMapping("/editar/vendedor")
    public String editarVendedor(Model model){
        Iterable<Vendedor> vendedor = repositorioVendedor.findAll();
        model.addAttribute("vendedores", vendedor);
        return "vistaEditarVendedor";
    }
    
    @GetMapping("/actualizar/vendedor") //path del controlador
    public String actualizarVendedor(Model model) {
        model.addAttribute("vendedor", new VendedorDto());
        return "vistaActualizarVendedor";
    }
    
    @PostMapping("/actualizar/vendedor")
    public RedirectView editarVendedor(Vendedor vendedor){
        String codVendedor = vendedor.getCodVendedor();
        vendedor = repositorioVendedor.findByCodVendedor(codVendedor);
        log.info("vendedor seleccionado "+vendedor);
        Vendedor v = repositorioVendedor.findByCodVendedor(codVendedor);
        return new RedirectView("redirect:/vistaVendedor",true);
    }
    
    @GetMapping("/eliminar/vendedor")
    public String eliminarProducto(Vendedor vendedor){
        String codVendedor = vendedor.getCodVendedor();
        log.info(codVendedor);
        vendedor = repositorioVendedor.findByCodVendedor(codVendedor);
        log.info("vendedor seleccionado "+vendedor);
        repositorioVendedor.delete(vendedor);
        return "redirect:/editar/vendedor";
    }
    
}
