package com.miempresa.aplicacion.controladores;

import com.miempresa.aplicacion.dtos.ProductoDto;
import com.miempresa.aplicacion.modelos.Producto;
import com.miempresa.aplicacion.dtos.FacturaDto;
import com.miempresa.aplicacion.modelos.Factura;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(
        @Autowired))
public class ControladorProducto {

    private final ProductoDAO repositorioProducto;
    private String codPro,nombre,descripcion,precio;
    

    @GetMapping("/productos") //path del controlador
    public String getTodosLosProductos(Model model) {
        Iterable<Producto> productos = repositorioProducto.findAll();
        model.addAttribute("productos", productos);
        return "vistaProducto";
    }

    @GetMapping("/productos/{codigoProducto}") //path del controlador
    public String getProductoById(@PathVariable String codigoProducto, Model model) {
        List<String> listaProducto = new ArrayList<>();
        listaProducto.add(codigoProducto);
        Iterable<Producto> productos = repositorioProducto.findAllById(listaProducto);
        model.addAttribute("productos", productos);
        return "vistaProducto";
    }

    @GetMapping("/crear/producto") //path del controlador
    public String crearProducto(Model model) {

        model.addAttribute("producto", new Producto());
        return "vistaCrearProducto";
    }

    @PostMapping("/crear/producto")
    public RedirectView procesarProducto(@ModelAttribute Producto producto, RedirectAttributes attribute) {
        try {
            log.info("tipo: " + ((Object) producto.getPrecioProducto()).getClass().getSimpleName() + "valor: " + producto.getPrecioProducto());
            //Validaciones de vacio
            if (producto.getCodProducto().equals(null) || producto.getCodProducto().equals("")) {
                attribute.addFlashAttribute("error", "No agregaste el codigo del producto");
                return new RedirectView("/crear/producto/", true);
            } else if (producto.getNombreProducto().equals(null) || producto.getNombreProducto().equals("")) {
                attribute.addFlashAttribute("error", "No agregaste el nombre del producto");
                return new RedirectView("/crear/producto/", true);
            } else if (producto.getDescripcionProducto().equals(null) || producto.getDescripcionProducto().equals("")) {
                attribute.addFlashAttribute("error", "No agregaste una descripcion del producto");
                return new RedirectView("/crear/producto/", true);
            } else if (producto.getPrecioProducto() <= 0.0) {
                attribute.addFlashAttribute("error", "No agregaste una valor valido al producto");
                return new RedirectView("/crear/producto/", true);
            }
            Producto productoGuardado = repositorioProducto.save(producto);
            if (productoGuardado == null) {
                return new RedirectView("/crear/producto/", true);
            }
            return new RedirectView("/productos/", true);
        } catch (Exception e) {
            attribute.addFlashAttribute("error", "No se pudieron agregar productos, revisa los campos");
            return new RedirectView("/crear/producto/", true);
        }

    }

    //Editar es para eliminar o actulizar
    @GetMapping("/editar/producto")
    public String editarProducto(Model model) {
        Iterable<Producto> productos = repositorioProducto.findAll();
        model.addAttribute("productos", productos);
        return "vistaEditarProducto";
    }

    @GetMapping("/actualizar/producto") //path del controlador
    public String actualizarProducto(Producto producto, Model model) {
        log.info(producto.getCodProducto());
        this.codPro = producto.getCodProducto();
        Producto p = repositorioProducto.findByCodProducto(this.codPro);
        model.addAttribute("producto", p);
        return "vistaActualizarProducto";
    }

    @PostMapping("/actualizar/producto")
    public RedirectView editarProducto(Producto producto, RedirectAttributes attribute) {
        try {
            //Validaciones basicas
            if (producto.getCodProducto().equals("") || producto.getCodProducto().equals(null) || producto.getCodProducto().equals(" ")) {
                log.info("codigo de producto nulo");
                attribute.addFlashAttribute("error", "Complete el campo de codigo producto");
                return new RedirectView("redirect:../actualizar/producto?codProducto=" + this.codPro, true);
            } else if (producto.getNombreProducto().equals("") || producto.getNombreProducto().equals(null) || producto.getNombreProducto().equals(" ")) {
                log.info("nombre del producto nulo");
                attribute.addFlashAttribute("error", "Complete el campo de nombre del producto");
                return new RedirectView("../actualizar/producto?codProducto=" + this.codPro, true);
            }else if (producto.getDescripcionProducto().equals("") || producto.getDescripcionProducto().equals(null) || producto.getDescripcionProducto().equals(" ")) {
                log.info("descripcion del producto nulo");
                attribute.addFlashAttribute("error", "Complete el campo de descripcion del producto");
                return new RedirectView("../actualizar/producto?codProducto=" + this.codPro, true);
            }else if (producto.getPrecioProducto().equals("") || producto.getPrecioProducto().equals(null) || producto.getPrecioProducto().equals("0.0")||producto.getPrecioProducto()<=0) {
                log.info("precio del producto nulo");
                attribute.addFlashAttribute("error", "Complete/Que sea valido el campo de precio del producto");
                return new RedirectView("../actualizar/producto?codProducto=" + this.codPro, true);
            }
            //Luego de hacer validaciones, se pasa a editar
            Producto p = repositorioProducto.findByCodProducto(codPro);
            //Se setean las variables del modelo que se recibe de la vista (hayan cambios o no)
            //Variables globales
            nombre = producto.getNombreProducto();
            descripcion = producto.getDescripcionProducto();
            precio = String.valueOf(producto.getPrecioProducto());
            //set de elementos a cambiar
            p.setDescripcionProducto(descripcion);
            p.setNombreProducto(nombre);
            p.setPrecioProducto(Double.parseDouble(precio));
            //Se hace el update
            Producto productoGuardado = repositorioProducto.save(p);
            if (productoGuardado == null) {
                attribute.addFlashAttribute("error", "No se pudo guardar el producto");
                return new RedirectView("../actualizar/producto?codProducto=" + this.codPro, true);
            }
            return new RedirectView("../productos", true);
        } catch (Exception e) {
            return new RedirectView("redirect:../", true);
        }
    }

    @GetMapping("/eliminar/producto")
    public String eliminarProducto(Producto producto, RedirectAttributes attribute) {
        String codProducto = producto.getCodProducto();
        producto = repositorioProducto.findByCodProducto(codProducto);
        log.info("producto seleccionado " + producto);
        try {
            repositorioProducto.delete(producto);
            attribute.addFlashAttribute("success", "Producto eliminado");
        } catch (Exception e) {
            attribute.addFlashAttribute("error", "No se pudo borrar el producto, ya que se encuentra en las facturas");
        }
        return "redirect:/editar/producto";
    }
}
