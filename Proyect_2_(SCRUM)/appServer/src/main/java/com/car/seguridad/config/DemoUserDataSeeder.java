package com.car.seguridad.config;

import com.car.business.domain.Cliente;
import com.car.business.domain.ContactoCorreoElectronico;
import com.car.business.domain.Departamento;
import com.car.business.domain.Direccion;
import com.car.business.domain.Imagen;
import com.car.business.domain.Localidad;
import com.car.business.domain.Nacionalidad;
import com.car.business.domain.Pais;
import com.car.business.domain.Persona;
import com.car.business.domain.Provincia;
import com.car.business.domain.enums.RolUsuario;
import com.car.business.domain.enums.TipoContacto;
import com.car.business.domain.enums.TipoDocumento;
import com.car.business.domain.enums.TipoImagen;
import com.car.business.dto.UsuarioDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.logic.service.UsuarioService;
import com.car.business.percistence.repository.ContactoCorreoElectronicoRepository;
import com.car.business.percistence.repository.DepartamentoRepository;
import com.car.business.percistence.repository.DireccionRepository;
import com.car.business.percistence.repository.ImagenRepository;
import com.car.business.percistence.repository.LocalidadRepository;
import com.car.business.percistence.repository.NacionalidadRepository;
import com.car.business.percistence.repository.PaisRepository;
import com.car.business.percistence.repository.PersonaRepository;
import com.car.business.percistence.repository.ProvinciaRepository;
import com.car.business.percistence.repository.UsuarioRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DemoUserDataSeeder implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;
    private final ContactoCorreoElectronicoRepository contactoCorreoElectronicoRepository;
    private final DireccionRepository direccionRepository;
    private final LocalidadRepository localidadRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;
    private final NacionalidadRepository nacionalidadRepository;
    private final ImagenRepository imagenRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        try {
            Persona persona = crearPersonaDemo();
            crearUsuarioDemo(persona.getId());
            log.info("Usuario demo creado: usuario='demo', clave='Demo1234!'");
        } catch (BusinessException e) {
            log.error("No se pudo crear el usuario demo", e);
        }
    }

    private void crearUsuarioDemo(String personaId) throws BusinessException {
        UsuarioDto dto = new UsuarioDto();
        dto.setNombreUsuario("demo");
        dto.setClave("Demo1234!");
        dto.setRolUsuario(RolUsuario.CLIENTE);
        dto.setPersonaId(personaId);
        usuarioService.crear(dto);
    }

    private Persona crearPersonaDemo() {
        Pais pais = paisRepository.findByNombre("Argentina").orElseGet(() -> {
            Pais newPais = new Pais();
            newPais.setNombre("Argentina");
            return paisRepository.save(newPais);
        });

        Provincia provincia = provinciaRepository.findByNombre("Buenos Aires").orElseGet(() -> {
            Provincia newProvincia = new Provincia();
            newProvincia.setNombre("Buenos Aires");
            newProvincia.setPais(pais);
            return provinciaRepository.save(newProvincia);
        });

        Departamento departamento = departamentoRepository.findByNombre("La Plata").orElseGet(() -> {
            Departamento newDepartamento = new Departamento();
            newDepartamento.setNombre("La Plata");
            newDepartamento.setProvincia(provincia);
            return departamentoRepository.save(newDepartamento);
        });

        Localidad localidad = localidadRepository.findByNombre("La Plata").orElseGet(() -> {
            Localidad newLocalidad = new Localidad();
            newLocalidad.setNombre("La Plata");
            newLocalidad.setCodigoPostal("1900");
            newLocalidad.setDepartamento(departamento);
            return localidadRepository.save(newLocalidad);
        });

        Direccion direccion = new Direccion();
        direccion.setCalle("Calle Falsa");
        direccion.setNumeracion("123");
        direccion.setBarrio("Centro");
        direccion.setManzanaPiso(null);
        direccion.setCasaDepartamento(null);
        direccion.setReferencia("Cerca de la plaza");
        direccion.setLocalidad(localidad);
        direccion = direccionRepository.save(direccion);

        Imagen imagen = new Imagen();
        imagen.setNombre("avatar-demo");
        imagen.setMime("image/png");
        imagen.setContenido(new byte[] {0});
        imagen.setTipoImagen(TipoImagen.PERSONA);
        imagen = imagenRepository.save(imagen);

        ContactoCorreoElectronico contacto = new ContactoCorreoElectronico();
        contacto.setTipoContacto(TipoContacto.PERSONAL);
        contacto.setObservacion("Contacto demo");
        contacto.setEmail("demo@example.com");
        contacto = contactoCorreoElectronicoRepository.save(contacto);

        Nacionalidad nacionalidad = nacionalidadRepository.findByNombre("Argentina").orElseGet(() -> {
            Nacionalidad newNacionalidad = new Nacionalidad();
            newNacionalidad.setNombre("Argentina");
            return nacionalidadRepository.save(newNacionalidad);
        });

        Cliente cliente = new Cliente();
        cliente.setNombre("Demo");
        cliente.setApellido("Usuario");
        cliente.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        cliente.setTipoDocumento(TipoDocumento.DNI);
        cliente.setNumeroDocumento("12345678");
        cliente.getContactos().add(contacto);
        cliente.setDireccion(direccion);
        cliente.setImagen(imagen);
        cliente.setDireccionEstadia("Dirección temporal");
        cliente.setNacionalidad(nacionalidad);
        return personaRepository.save(cliente);
    }
}
