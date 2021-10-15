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
import java.util.Objects;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(
        @Autowired))
public class ControladorFactura {

    @Autowired
    private final FacturaDAO repositorioFactura;
    private final ProductoDAO repositorioProducto;
    private final VendedorDAO repositorioVendedor;
    
    private String factura,producto,vendedor,fecha,id;

    @GetMapping("/facturas") //path del controlador
    public String getTodasLasFacturas(Model model) {
        Iterable<Factura> facturas = repositorioFactura.findAll();
        log.info("" + model.toString());
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
    
    @PostMapping("/crear/factura")
    public RedirectView procesarProducto(@ModelAttribute FacturaDto facturaDto,RedirectAttributes attribute) {
        //Se inicializan variables
        Producto producto;
        Vendedor vendedor;
        try {
            //Validaciones
            //Validacion basica (campo nulo)
            if(facturaDto.getCodigoProducto().equals(null) || facturaDto.getCodigoProducto().equals("")){
                attribute.addFlashAttribute("error", "Complete el campo de codigo producto");
                log.info("campo vacio: cod prod");
                return new RedirectView("/crear/factura/", true);
            }else if(facturaDto.getCodigoVendedor().equals("") || facturaDto.getCodigoVendedor().equals(null)){
                attribute.addFlashAttribute("error", "Complete el campo de codigo vendedor");
                log.info("campo vacio: cod vend");
                return new RedirectView("/crear/factura/", true);
            }else if(facturaDto.getNumeroFactura().equals("") || facturaDto.getNumeroFactura().equals(null)){
                attribute.addFlashAttribute("error", "Complete el campo de numero de  factura");
                log.info("campo vacio: num fact");
                return new RedirectView("/crear/factura/", true);
            }
            //Validacion de numero de factura repetido
            Factura f = repositorioFactura.findByNumeroFactura(facturaDto.getNumeroFactura());
            if(!Objects.isNull(f)){
                attribute.addFlashAttribute("error", "La factura ya se encuentra registrada");
                log.info("campo ya diligenciado: num fact");
                return new RedirectView("/crear/factura/", true);
            }
            //Post validaciones, se hace uso de la creacion de la factura
            //Primero se llama a los DAO de producto y vendedor
            producto = repositorioProducto.findByCodProducto(facturaDto.getCodigoProducto());
            vendedor = repositorioVendedor.findByCodVendedor(facturaDto.getCodigoVendedor());
            //Se inicializa factura
            Factura factura = new Factura();
            
            //Se empieza a setear las columnas de la tabla factura en funcion de los datos que se recibieron y
            //tambien en funcion de lo que se obtenga segun el codigo producto o no.
            factura.setNumeroFactura(facturaDto.getNumeroFactura());
            factura.setProducto(producto);
            factura.setVendedor(vendedor);
            factura.setFechaVenta(facturaDto.getFechaVenta());
            //Se trae el precio del producto
            //Se crea una variable para obtener el precio producto del modelo
            Double fac = producto.getPrecioProducto();
            float precio = fac.floatValue();
            //Luego se implementa esa variable a la factura
            factura.setValorFactura(precio);
            //Query para guardar datos 
            Factura facturaGuardada = repositorioFactura.save(factura);
            if (facturaGuardada == null) {
                attribute.addFlashAttribute("error", "No agregaste datos");
                log.info("No se pudieron agregar datos");
                return new RedirectView("/crear/factura/", true);
            }
            log.info("Datos añadidos");
            attribute.addFlashAttribute("success", "¡Factura creada con exito!");
            return new RedirectView("/facturas", true);
        } catch (Exception e) {
            attribute.addFlashAttribute("error", "No se pudieron agregar datos, revisa los codigos de producto,vendedor y llena todos los campos");
            log.info("No se pudo realizar la transaccion, ya que no encuentra un producto o un vendedor");
            return new RedirectView("/crear/factura", true);
        }
    }

    //Editar es para eliminar o actulizar
    @GetMapping("/editar/factura")
    public String editarFactura(Model model) {
        Iterable<Factura> facturas = repositorioFactura.findAll();
        model.addAttribute("facturas", facturas);
        return "vistaEditarFactura";
    }

    @GetMapping("/actualizar/factura") //path del controlador
    public String actualizarFactura(@ModelAttribute FacturaDto facturaDto,Factura factura,Model model) {
        this.id = String.valueOf(factura.getIdVenta());
        Factura f = repositorioFactura.findByIdVenta(factura.getIdVenta());
        //Se setea el DTO como modelo para poder editar
        facturaDto.setCodigoProducto(f.getProducto().getCodProducto());
        facturaDto.setCodigoVendedor(f.getVendedor().getCodVendedor());
        facturaDto.setFechaVenta(f.getFechaVenta());
        facturaDto.setIdVenta(f.getIdVenta());
        facturaDto.setNumeroFactura(f.getNumeroFactura());
        facturaDto.setValorFactura(f.getValorFactura());
        //Se agrega el DTO al modelo de la vista actualizar factura
        model.addAttribute("facturaDto", facturaDto);
        return "vistaActualizarFactura";
    }

    @PostMapping("/actualizar/factura")
    public RedirectView editarProducto(FacturaDto facturaDto, RedirectAttributes attribute) {
        try {
            //Validaciones basicas
            //Que no esten vacios los campos
            if (facturaDto.getNumeroFactura().equals("") || facturaDto.getNumeroFactura().equals(null) || facturaDto.getNumeroFactura().equals(" ")) {
                log.info("codigo de factura nulo");
                attribute.addFlashAttribute("error", "Complete el campo de codigo producto");
                return new RedirectView("/actualizar/factura?idVenta=" + this.id, true);
            } else if (facturaDto.getCodigoProducto().equals("") || facturaDto.getCodigoProducto().equals(null) || facturaDto.getCodigoProducto().equals(" ")) {
                log.info("codigo del producto nulo");
                attribute.addFlashAttribute("error", "Complete el campo de codigo del producto");
                return new RedirectView("/actualizar/factura?idVenta=" + this.id, true);
            }else if (facturaDto.getCodigoVendedor().equals("") || facturaDto.getCodigoVendedor().equals(null) || facturaDto.getCodigoVendedor().equals(" ")) {
                log.info("codigo del vendedor nulo");
                attribute.addFlashAttribute("error", "Complete el campo de codigo de vendedor");
                return new RedirectView("/actualizar/factura?idVenta=" + this.id, true);
            }
            //Que exista el valor al cual se va a actualizar el cod producto-vendedor
            //Para producto
            Producto p = repositorioProducto.findByCodProducto(facturaDto.getCodigoProducto());
            Vendedor v = repositorioVendedor.findByCodVendedor(facturaDto.getCodigoVendedor());
            Factura fact = repositorioFactura.findByNumeroFactura(facturaDto.getNumeroFactura());
            if(Objects.isNull(p)){
                log.info("codigo del producto no valido");
                attribute.addFlashAttribute("atencion", "El codigo de producto que diligencio, no se encuentra registrado");
                return new RedirectView("/actualizar/factura?idVenta=" + this.id, true);
            //Para producto
            }else if(Objects.isNull(v)){
                log.info("codigo del vendedor no valido");
                attribute.addFlashAttribute("atencion", "El codigo de vendedor que diligencio, no se encuentra registrado");
                return new RedirectView("/actualizar/factura?idVenta=" + this.id, true);
                //La factura ya se encuentra registrada
            }else if(!Objects.isNull(fact)){
                log.info("ya esta registrado el producto que va a cambiar");
                attribute.addFlashAttribute("atencion", "El codigo de factura que diligencio, ya se encuentra registrado");
                return new RedirectView("/actualizar/factura?idVenta=" + this.id, true);
            }
            //Luego de hacer validaciones, se pasa a editar
            Factura f = repositorioFactura.findByIdVenta(facturaDto.getIdVenta());
            //Se setean las variables del modelo que se recibe de la vista (hayan cambios o no)
            //set de elementos a cambiar
            f.setFechaVenta(facturaDto.getFechaVenta());
            f.setNumeroFactura(facturaDto.getNumeroFactura());
            f.setProducto(p);
            f.setVendedor(v);
            //Para cambiar el precio del producto nuevo escogido
            Double precio = p.getPrecioProducto();
            float pr = precio.floatValue();
            f.setValorFactura(pr);
            //Se hace el update
            Factura facturaGuardada = repositorioFactura.save(f);
            if (facturaGuardada == null) {
                attribute.addFlashAttribute("error", "No se pudo guardar la factura");
                return new RedirectView("/actualizar/factura?idVenta=" + this.id, true);
            }
            attribute.addFlashAttribute("success", "Factura actualizada");
            return new RedirectView("../editar/factura", true);
        } catch (Exception e) {
            attribute.addFlashAttribute("error", "Error inesperado");
            return new RedirectView("/actualizar/factura?idVenta=" + this.id, true);
        }
    }

    @GetMapping("/eliminar/factura")
    public String eliminarProducto(Factura factura) {
        Long idVenta = factura.getIdVenta();
        factura = repositorioFactura.findByIdVenta(idVenta);
        log.info("numero de factura seleccionado " + factura);
        repositorioFactura.delete(factura);
        return "redirect:/editar/factura";
    }
}
