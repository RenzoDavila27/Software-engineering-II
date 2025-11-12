package com.car.clientead;

import com.car.clientead.business.logic.ClienteService;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.dto.ContactoTelefonicoDto;
import com.car.clientead.client.dto.DireccionDto;
import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.client.dto.enums.TipoContacto;
import com.car.clientead.client.dto.enums.TipoDocumento;
import com.car.clientead.client.dto.enums.TipoImagen;
import com.car.clientead.client.dto.enums.TipoTelefono;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClienteServiceSwitchIntegrationTest {

    @Autowired
    private ClienteService clienteService;

    private String clienteId;

    @AfterEach
    void cleanup() {
        if (clienteId != null) {
            try {
                clienteService.eliminar(clienteId);
            } catch (Exception ignored) {
            }
        }
    }

    @Test
    void puedeCambiarContactoCorreoATelefono() {
        ClienteDto nuevo = new ClienteDto();
        nuevo.setNombre("Switch");
        nuevo.setApellido("Test");
        nuevo.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        nuevo.setTipoDocumento(TipoDocumento.DNI);
        nuevo.setNumeroDocumento("99999991");
        nuevo.setDireccionEstadia("Temporal");
        nuevo.setNacionalidadId("0b661fe2-bf80-11f0-a724-5c93a281f19f");

        DireccionDto direccion = new DireccionDto();
        direccion.setCalle("Nueva");
        direccion.setNumeracion("123");
        direccion.setLocalidadId("0d5d5ed4-bf80-11f0-a724-5c93a281f19f");

        ImagenDto imagen = new ImagenDto();
        imagen.setNombre("switch.png");
        imagen.setMime("image/png");
        imagen.setTipoImagen(TipoImagen.PERSONA);
        imagen.setContenido("img".getBytes(StandardCharsets.UTF_8));

        ContactoCorreoElectronicoDto correo = new ContactoCorreoElectronicoDto();
        correo.setEmail("switch@test.com");
        correo.setTipoContacto(TipoContacto.PERSONAL);

        ClienteDto creado = clienteService.crearConDatosRelacionados(
                nuevo,
                direccion,
                imagen,
                null,
                correo,
                "CORREO"
        );
        clienteId = creado.getId();

        DireccionDto dirExistente = clienteService.obtenerDireccion(creado.getDireccionId());
        ImagenDto imgExistente = clienteService.obtenerImagen(creado.getImagenId());
        ContactoCorreoElectronicoDto correoExistente = clienteService.obtenerContactoCorreo(creado.getContactoId());

        ContactoTelefonicoDto telefono = new ContactoTelefonicoDto();
        telefono.setTelefono("5552222");
        telefono.setTipoTelefono(TipoTelefono.CELULAR);
        telefono.setTipoContacto(TipoContacto.PERSONAL);

        clienteService.modificarConDatosRelacionados(
                creado.getId(),
                creado,
                dirExistente,
                imgExistente,
                telefono,
                correoExistente,
                "TELEFONO"
        );
    }
}
