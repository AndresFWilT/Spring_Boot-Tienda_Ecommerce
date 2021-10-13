package com.miempresa.aplicacion.controladores;

import com.miempresa.aplicacion.dtos.FacturaDto;
import com.miempresa.aplicacion.modelos.Factura;
import com.miempresa.aplicacion.modelos.FacturaDAO;
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
import lombok.extern.slf4j.Slf4j;
import com.miempresa.aplicacion.modelos.ProductoDAO;
import com.miempresa.aplicacion.modelos.VendedorDAO;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(
        @Autowired))
public class ControladorFactura {
    
    @Autowired
    private final FacturaDAO repositorioFactura;
    private final ProductoDAO repositorioProducto;
    private final VendedorDAO repositorioVendedor;

    @GetMapping("/facturas") //path del controlador
    public String getTodasLasFacturas(Model model) {
        Iterable<Factura> facturas = repositorioFactura.findAll();
        model.addAttribute("facturas", facturas);
        return "vistaFactura";
    }

    @GetMapping("/facturas/{numeroFactura}") //path del controlador
    public String getFacturaByNumero(@PathVariable String numeroFactura, Model model) {
        Factura nFactura = new Factura();
        nFactura.setNumeroFactura(numeroFactura);
        Factura factura = repositorioFactura.findByNumeroFactura(numeroFactura);
        model.addAttribute("facturas", factura);
        return "vistaFactura";
    }

    @GetMapping("/crear/factura") //path del controlador
    public String crearFactura(Model model) {
        model.addAttribute("factura", new FacturaDto());
        return "vistaCrearFactura";
    }
    
    //Editar es para eliminar o actulizar
    @GetMapping("/editar/factura")
    public String editarFactura(Model model){
        Iterable<Factura> facturas = repositorioFactura.findAll();
        model.addAttribute("facturas", facturas);
        return "vistaEditarFactura";
    }
    
    @GetMapping("/actualizar/factura") //path del controlador
    public String actualizarFactura(Model model) {
        model.addAttribute("factura", new FacturaDto());
        return "vistaActualizarFactura";
    }
    
    @PostMapping("/actualizar/factura")
    public RedirectView editarProducto(Factura factura){
        Long idVenta = factura.getIdVenta();
        factura = repositorioFactura.findByIdVenta(idVenta);
        log.info("numero de factura seleccionado "+factura);
        Factura f = repositorioFactura.findByIdVenta(idVenta);
        return new RedirectView("redirect:/vistaFactura",true);
    }

    @PostMapping("/crear/factura")
    public RedirectView procesarProducto(@ModelAttribute FacturaDto facturaDto) {
        Producto producto = repositorioProducto.findByCodProducto(facturaDto.getCodigoProducto());
        Vendedor vendedor = repositorioVendedor.findByCodVendedor(facturaDto.getCodigoVendedor());
        Factura factura = new Factura();
        factura.setNumeroFactura(facturaDto.getNumeroFactura());
        factura.setProducto(producto);
        factura.setVendedor(vendedor);
        factura.setFechaVenta(facturaDto.getFechaVenta());
        factura.setValorFactura(facturaDto.getValorFactura());
        Factura facturaGuardada = repositorioFactura.save(factura);
       if (facturaGuardada == null){
           log.info("No se pudieron agregar datos");
           return new RedirectView("/crear/factura/",true);
       }
       log.info("Datos añadidos");
       return new RedirectView("/facturas/",true);
    }
  
    @GetMapping("/eliminar/factura")
    public String eliminarProducto(Factura factura){
        Long idVenta = factura.getIdVenta();
        factura = repositorioFactura.findByIdVenta(idVenta);
        log.info("numero de factura seleccionado "+factura);
        repositorioFactura.delete(factura);
        return "redirect:/editar/factura";
    }
}
