package com.miempresa.aplicacion.controladores;

import com.miempresa.aplicacion.dtos.VendedorDto;
import com.miempresa.aplicacion.modelos.Vendedor;
import com.miempresa.aplicacion.dtos.FacturaDto;
import com.miempresa.aplicacion.modelos.Factura;
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
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(
        @Autowired))
public class ControladorVendedor {

    private final VendedorDAO repositorioVendedor;
    //Atributos para hacer los edit
    private String venSelec, nomSelec;

    @GetMapping("/vendedores") //path del controlador
    public String getTodosLosVendedores(Model model) {
        Iterable<Vendedor> vendedores = repositorioVendedor.findAll();
        model.addAttribute("vendedores", vendedores);
        return "vistaVendedor";
    }

    @GetMapping("/vendedores/{codigoVendedor}") //path del controlador
    public String getVendedorById(@PathVariable String codigoVendedor, Model model) {
        Vendedor vendedores = repositorioVendedor.findByCodVendedor(codigoVendedor);
        model.addAttribute("vendedores", vendedores);
        return "vistaVendedor";
    }

    @GetMapping("/crear/vendedor") //path del controlador
    public String crearVendedor(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "vistaCrearVendedor";
    }

    @PostMapping("/crear/vendedor")
    public RedirectView procesarVendedor(@ModelAttribute Vendedor vendedor, RedirectAttributes attribute) {
        try {
            //Validaciones basicas
            if (vendedor.getCodVendedor().equals("") || vendedor.getCodVendedor().equals(null)) {
                attribute.addFlashAttribute("error", "No agregaste un codigo para el vendedor");
                return new RedirectView("/crear/vendedor/", true);
            } else if (vendedor.getNombreVendedor().equals("") || vendedor.getNombreVendedor().equals(null)) {
                attribute.addFlashAttribute("error", "No agregaste un nombre para el vendedor");
                return new RedirectView("/crear/vendedor/", true);
            }
            //Validacion de factura ya repetida
            Vendedor ven = repositorioVendedor.findByCodVendedor(vendedor.getCodVendedor());
            if(!Objects.isNull(ven)){
                attribute.addFlashAttribute("error", "Codigo del vendedor ya esta registrado");
                return new RedirectView("/crear/vendedor/", true);
            }
            //Hechas las validaciones se pasa a guardar el vendedor
            Vendedor vendedorGuardado = repositorioVendedor.save(vendedor);
            if (vendedorGuardado == null) {
                attribute.addFlashAttribute("error", "No se pudo agregar al vendedor");
                return new RedirectView("/crear/vendedor/", true);
            }
            attribute.addFlashAttribute("success", "¡Vendedor creado con exito!");
            return new RedirectView("/vendedores/", true);
        } catch (Exception e) {
            attribute.addFlashAttribute("error", "No se pudo agregar al vendedor, revisa los campos");
            return new RedirectView("/crear/vendedor/", true);
        }
    }

    //Editar es para eliminar o actulizar
    @GetMapping("/editar/vendedor")
    public String editarVendedor(Model model) {
        Iterable<Vendedor> vendedor = repositorioVendedor.findAll();
        model.addAttribute("vendedores", vendedor);
        return "vistaEditarVendedor";
    }

    @GetMapping("/actualizar/vendedor") //path del controlador
    public String actualizarVendedor(Vendedor vendedor, Model model) {
        String codVendedor = vendedor.getCodVendedor();
        venSelec = vendedor.getCodVendedor();
        nomSelec = vendedor.getNombreVendedor();
        Vendedor ven = repositorioVendedor.findByCodVendedor(codVendedor);
        model.addAttribute("vendedor", ven);
        return "vistaActualizarVendedor";
    }

    @PostMapping("/actualizar/vendedor")
    public RedirectView editarVendedor(Vendedor vendedor, RedirectAttributes attribute) {
        try {
            //Validaciones basicas
            if (vendedor.getCodVendedor().equals("") || vendedor.getCodVendedor().equals(null) || vendedor.getCodVendedor().equals(" ")) {
                log.info("codigo de vendedor nulo");
                attribute.addFlashAttribute("error", "Complete el campo de codigo vendedor");
                return new RedirectView("../actualizar/vendedor?codVendedor=" + this.venSelec, true);
            } else if (vendedor.getNombreVendedor().equals("") || vendedor.getNombreVendedor().equals(null) || vendedor.getNombreVendedor().equals(" ")) {
                log.info("nombre de vendedor nulo");
                attribute.addFlashAttribute("error", "Complete el campo de nombre de vendedor");
                return new RedirectView("../actualizar/vendedor?codVendedor=" + this.venSelec, true);
            }
            //Luego de hacer validaciones, se pasa a editar
            String codVendedor = vendedor.getCodVendedor();
            String nomVendedor = vendedor.getNombreVendedor();
            log.info("vendedor seleccionado: " + venSelec + ", vendedor a cambiar: " + codVendedor);
            Vendedor v = repositorioVendedor.findByCodVendedor(this.venSelec);
            v.setNombreVendedor(nomVendedor);
            Vendedor vendedorGuardado = repositorioVendedor.save(v);
            if (vendedorGuardado == null) {
                attribute.addFlashAttribute("error", "No se pudo guardar al vendedor");
                return new RedirectView("../actualizar/vendedor?codVendedor=" + this.venSelec, true);
            }
            attribute.addFlashAttribute("success", "¡vendedor actualizado!");
            return new RedirectView("../editar/vendedor", true);
        } catch (Exception e) {
            return new RedirectView("redirect:../", true);
        }
    }

    @GetMapping("/eliminar/vendedor")
    public String eliminarProducto(Vendedor vendedor, RedirectAttributes attribute) {
        String codVendedor = vendedor.getCodVendedor();
        log.info(codVendedor);
        vendedor = repositorioVendedor.findByCodVendedor(codVendedor);
        log.info("vendedor seleccionado " + vendedor);
        try {
            repositorioVendedor.delete(vendedor);
            attribute.addFlashAttribute("success", "Vendedor borrado");
        } catch (Exception e) {
            attribute.addFlashAttribute("error", "No se pudo borrar el vendedor, ya que se encuentra en las facturas");
        }
        return "redirect:/editar/vendedor";
    }

}
