package com.car.business.logic.service;

import com.car.business.domain.*;
import com.car.business.domain.enums.RolUsuario;
import com.car.business.domain.enums.TipoImagen;
import com.car.business.dto.UsuarioApiDto;
import com.car.business.dto.UsuarioDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.UsuarioApiMapper;
import com.car.business.mappers.UsuarioMapper;
import com.car.business.percistence.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService extends BaseService<Usuario, UsuarioDto, String> {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final PersonaService personaService;
    private final DireccionService direccionService;
    private final LocalidadService localidadService;
    private final DepartamentoService departamentoService;
    private final ProvinciaService provinciaService;
    private final PaisService paisService;
    private final NacionalidadService nacionalidadService;
    private final ContactoCorreoElectronicoService contactoCorreoElectronicoService;
    private final ContactoTelefonicoService contactoTelefonicoService;
    private final ImagenService imagenService;
    private final UsuarioMapper usuarioApiMapper;
    private final ClienteService clienteService;

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper, PasswordEncoder passwordEncoder,
                          PersonaService personaService, DireccionService direccionService, LocalidadService localidadService,
                          DepartamentoService departamentoService, ProvinciaService provinciaService, PaisService paisService,
                          NacionalidadService nacionalidadService, ContactoCorreoElectronicoService contactoCorreoElectronicoService,
                          ContactoTelefonicoService contactoTelefonicoService, ImagenService imagenService, UsuarioMapper usuarioApiMapper, ClienteService clienteService) {
        super(repository, mapper);
        this.passwordEncoder = passwordEncoder;
        this.personaService = personaService;
        this.direccionService = direccionService;
        this.localidadService = localidadService;
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
        this.paisService = paisService;
        this.nacionalidadService = nacionalidadService;
        this.contactoCorreoElectronicoService = contactoCorreoElectronicoService;
        this.contactoTelefonicoService = contactoTelefonicoService;
        this.imagenService = imagenService;
        this.usuarioApiMapper = usuarioApiMapper;
        this.clienteService = clienteService;
        this.usuarioRepository = repository;
    }

    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        if (!StringUtils.hasText(nombreUsuario)) {
            throw new BusinessException("El nombre de usuario es obligatorio.");
        }
        return usuarioRepository.findByNombreUsuarioAndEliminadoFalse(nombreUsuario)
            .orElseThrow(() -> new BusinessException("No se encontró el usuario solicitado."));
    }

    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        if (!StringUtils.hasText(nombreUsuario)) {
            return Optional.empty();
        }
        return usuarioRepository.findByNombreUsuarioAndEliminadoFalse(nombreUsuario);
    }

    @Override
    protected void validar(Usuario entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El usuario es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getNombreUsuario())) {
            throw new BusinessException("El nombre de usuario es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getClave())) {
            throw new BusinessException("La clave es obligatoria.");
        }
        if (entidad.getRolUsuario() == null) {
            throw new BusinessException("El rol es obligatorio.");
        }
        if (entidad.getPersona() == null) {
            throw new BusinessException("La persona asociada es obligatoria.");
        }
    }

    @Override
    protected void preAlta(Usuario entidad) {
        codificarClave(entidad);
    }

    @Override
    protected void preModificacion(Usuario entidad) {
        codificarClave(entidad);
    }

    private void codificarClave(Usuario entidad) {
        String clave = entidad.getClave();
        if (StringUtils.hasText(clave) && !estaCodificada(clave)) {
            entidad.setClave(passwordEncoder.encode(clave));
        }
    }

    @Transactional
    public UsuarioDto registerUser(UsuarioApiDto apiDto) {
        if (!StringUtils.hasText(apiDto.getEmail())) {
            throw new BusinessException("El email es obligatorio y será usado como nombre de usuario.");
        }
        if (!StringUtils.hasText(apiDto.getPassword())) {
            throw new BusinessException("La clave es obligatoria.");
        }

        // Check if user already exists
        if (findByNombreUsuario(apiDto.getEmail()).isPresent()) {
            throw new BusinessException("Ya existe un usuario registrado con el email: " + apiDto.getEmail());
        }

        // 1. Create/Find Geographical Entities
        Pais pais = findOrCreatePais(apiDto.getCountry());
        Provincia provincia = findOrCreateProvincia(apiDto.getProvince(), pais);
        Departamento departamento = findOrCreateDepartamento(apiDto.getDepartment(), provincia);
        Localidad localidad = findOrCreateLocalidad(apiDto.getLocality(), apiDto.getPostalCode(), departamento);
        Nacionalidad nacionalidad = findOrCreateNacionalidad(apiDto.getCountry());

        // 2. Create Direccion
        Direccion direccion = new Direccion();
        direccion.setCalle(apiDto.getStreet());
        direccion.setNumeracion(apiDto.getNumber());
        direccion.setBarrio(apiDto.getNeighborhood());
        if (apiDto.getBlock().isEmpty()){
            direccion.setManzanaPiso(apiDto.getFloor());
        } else{
            direccion.setManzanaPiso(apiDto.getBlock());
        }
        direccion.setCasaDepartamento(apiDto.getApartment());
        direccion.setReferencia(apiDto.getLandmarks());
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);

        // 3. Create Imagen (if provided)
        Imagen imagen = new Imagen();
        if (StringUtils.hasText(apiDto.getPhotoBase64()) && StringUtils.hasText(apiDto.getPhotoContentType())) {
            byte[] foto = createImagen(apiDto.getPhotoBase64(), apiDto.getPhotoContentType());
            imagen.setContenido(foto);
        }

        imagen.setNombre("foto" + apiDto.getFirstName() + apiDto.getLastName());
        imagen.setTipoImagen(TipoImagen.PERSONA);
        imagen.setEliminado(false);



        // 4. Create Persona
        Cliente persona = new Cliente();
        persona.setNombre(apiDto.getFirstName());
        persona.setApellido(apiDto.getLastName());
        persona.setFechaNacimiento(apiDto.getDateOfBirth());
        persona.setTipoDocumento(usuarioApiMapper.mapTipoDocumento(apiDto.getDocumentType()));
        persona.setNumeroDocumento(apiDto.getDocumentNumber());
        persona.setDireccion(direccion);
        persona.setNacionalidad(nacionalidad);
        persona.setImagen(imagen);
        persona.setEliminado(false);

        // 5. Create Contactos
        List<Contacto> contactos = usuarioApiMapper.mapContactos(apiDto.getContacts(), apiDto.getEmail());
        for (Contacto contacto : contactos) {
            contacto.setPersona(persona);// Associate contact with the new persona
            persona.getContactos().add(contacto);
        }

        // 6. Create Usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(apiDto.getEmail());
        usuario.setClave(passwordEncoder.encode(apiDto.getPassword())); // Encode password
        usuario.setRolUsuario(RolUsuario.CLIENTE); // Default role for registered users
        usuario.setPersona(persona);
        usuario.setEliminado(false);
        return mapper.toDto(alta(usuario)); // Persist Usuario and return DTO

    }

    private Pais findOrCreatePais(String countryName) {
        if (!StringUtils.hasText(countryName)) {
            return null;
        }
        return paisService.findByNombre(countryName)
                .orElseGet(() -> {
                    Pais newPais = new Pais();
                    newPais.setNombre(countryName);
                    newPais.setEliminado(false);
                    return paisService.alta(newPais);
                });
    }

    private Provincia findOrCreateProvincia(String provinceName, Pais pais) {
        if (!StringUtils.hasText(provinceName) || pais == null) {
            return null;
        }
        return provinciaService.findByNombreAndPais(provinceName, pais)
                .orElseGet(() -> {
                    Provincia newProvincia = new Provincia();
                    newProvincia.setNombre(provinceName);
                    newProvincia.setPais(pais);
                    newProvincia.setEliminado(false);
                    return provinciaService.alta(newProvincia);
                });
    }

    private Departamento findOrCreateDepartamento(String departmentName, Provincia provincia) {
        if (!StringUtils.hasText(departmentName) || provincia == null) {
            return null;
        }
        return departamentoService.findByNombreAndProvincia(departmentName, provincia)
                .orElseGet(() -> {
                    Departamento newDepartamento = new Departamento();
                    newDepartamento.setNombre(departmentName);
                    newDepartamento.setProvincia(provincia);
                    newDepartamento.setEliminado(false);
                    return departamentoService.alta(newDepartamento);
                });
    }

    private Localidad findOrCreateLocalidad(String localityName, String postalCode, Departamento departamento) {
        if (!StringUtils.hasText(localityName) || departamento == null) {
            return null;
        }
        return localidadService.findByNombreAndCodigoPostalAndDepartamento(localityName, postalCode, departamento)
                .orElseGet(() -> {
                    Localidad newLocalidad = new Localidad();
                    newLocalidad.setNombre(localityName);
                    newLocalidad.setCodigoPostal(postalCode);
                    newLocalidad.setDepartamento(departamento);
                    newLocalidad.setEliminado(false);
                    return localidadService.alta(newLocalidad);
                });
    }

    private Nacionalidad findOrCreateNacionalidad(String countryName) {
        if (!StringUtils.hasText(countryName)) {
            return null;
        }
        return nacionalidadService.findByNombre(countryName)
                .orElseGet(() -> {
                    Nacionalidad newNacionalidad = new Nacionalidad();
                    newNacionalidad.setNombre(countryName);
                    newNacionalidad.setEliminado(false);
                    return nacionalidadService.alta(newNacionalidad);
                });
    }

    private boolean estaCodificada(String clave) {
        return clave.startsWith("$2a$") || clave.startsWith("$2b$") || clave.startsWith("$2y$");
    }

    public static byte[] createImagen(String base64String, String contentType) {

        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        String[] parts = base64String.split(",");
        String base64Data;

        if (parts.length > 1) {
            base64Data = parts[1];
        } else {
            base64Data = base64String;
        }

        // --- FIN DEL MANEJO DE PREFIJO ---


        // Esta es la línea clave que hace la conversión
        try {
            return Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            // Esto ocurre si el string no es un Base64 válido
            System.err.println("Error al decodificar Base64: " + e.getMessage());
            return null;
        }
    }

}
