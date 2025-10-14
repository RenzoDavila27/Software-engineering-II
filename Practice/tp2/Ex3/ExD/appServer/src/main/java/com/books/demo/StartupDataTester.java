package com.books.demo;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.domain.Localidad;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.logic.service.AutorService;
import com.books.demo.bussiness.logic.service.DomicilioService;
import com.books.demo.bussiness.logic.service.LibroService;
import com.books.demo.bussiness.logic.service.LocalidadService;
import com.books.demo.bussiness.logic.service.PersonaService;
import com.books.demo.controller.rest.dto.AutorDto;
import com.books.demo.controller.rest.dto.LibroDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class StartupDataTester {

    private static final Logger log = LoggerFactory.getLogger(StartupDataTester.class);

    private final AutorService autorService;
    private final LocalidadService localidadService;
    private final DomicilioService domicilioService;
    private final PersonaService personaService;
    private final LibroService libroService;
    private final ServletWebServerApplicationContext webServerContext;

    @Autowired
    public StartupDataTester(AutorService autorService,
                             LocalidadService localidadService,
                             DomicilioService domicilioService,
                             PersonaService personaService,
                             LibroService libroService,
                             ObjectProvider<ServletWebServerApplicationContext> webServerContextProvider) {
        this.autorService = autorService;
        this.localidadService = localidadService;
        this.domicilioService = domicilioService;
        this.personaService = personaService;
        this.libroService = libroService;
        this.webServerContext = webServerContextProvider.getIfAvailable();
    }

    public void runAllChecks() {
        log.info("=== Iniciando pruebas de servicio al arrancar la aplicación ===");
        DemoTestContext context = runServiceLayerChecks();
        runRestSmokeTests(context);
        log.info("=== Pruebas de arranque finalizadas ===");
    }

    private DemoTestContext runServiceLayerChecks() {
        try {
            log.info("-> Creando datos de prueba vía servicios");

            Autor autorTemporal = autorService.crearAutor(new Autor("Carlos", "Temp", "Autor temporal"));
            autorService.eliminarAutor(autorTemporal.getId());
            log.info("   Autor temporal marcado como eliminado: id={}", autorTemporal.getId());

            Autor autorActivo = autorService.crearAutor(new Autor("Ana", "García", "Especialista en Spring"));
            Autor datosAutorActualizados = new Autor();
            datosAutorActualizados.setNombre("Ana María");
            datosAutorActualizados.setApellido("García");
            datosAutorActualizados.setBiografia("Especialista en Spring y Hibernate");
            autorActivo = autorService.modificarAutor(autorActivo.getId(), datosAutorActualizados);
            log.info("   Autor activo actualizado: {} {}", autorActivo.getNombre(), autorActivo.getApellido());

            Localidad localidad = localidadService.crearLocalidad(new Localidad("Ciudad Prueba"));
            log.info("   Localidad creada: {} (id={})", localidad.getDenominacion(), localidad.getId());

            Domicilio domicilio = new Domicilio();
                domicilio.setCalle("Calle Falsa");
                domicilio.setNumero(123);
            domicilio.setLocalidad(localidad);
            domicilio = domicilioService.crearDomicilio(domicilio);
            log.info("   Domicilio creado: {} {} (id={})", domicilio.getCalle(), domicilio.getNumero(), domicilio.getId());

            Persona persona = new Persona();
            persona.setNombre("Juan");
            persona.setApellido("Pérez");
            persona.setDni(12345678);
            persona.setDomicilio(domicilio);
            persona = personaService.crearPersona(persona);
            log.info("   Persona creada: {} {} (id={})", persona.getNombre(), persona.getApellido(), persona.getId());

            Libro libroPrincipal = new Libro();
            libroPrincipal.setTitulo("Spring Boot Avanzado");
            libroPrincipal.setGenero("Tecnología");
            libroPrincipal.setPaginas(320);
            libroPrincipal.setFecha(LocalDate.now());
            libroPrincipal.setPersona(persona);
            libroPrincipal.setAutores(Set.of(autorActivo));
            libroPrincipal = libroService.crearLibro(libroPrincipal);
            log.info("   Libro principal creado: {} (id={})", libroPrincipal.getTitulo(), libroPrincipal.getId());

            Libro libroActualizacion = new Libro();
            libroActualizacion.setTitulo("Spring Boot Avanzado (2da Edición)");
            libroActualizacion.setGenero("Tecnología");
            libroActualizacion.setPaginas(360);
            libroActualizacion.setFecha(libroPrincipal.getFecha());
            libroActualizacion.setPersona(persona);
            libroActualizacion.setAutores(Set.of(autorActivo));
            libroService.modificarLibro(libroPrincipal.getId(), libroActualizacion);
            log.info("   Libro principal actualizado con nuevas páginas");

            Libro libroTemporal = new Libro();
            libroTemporal.setTitulo("Libro Temporal");
            libroTemporal.setGenero("Notas");
            libroTemporal.setPaginas(150);
            libroTemporal.setFecha(LocalDate.now().minusDays(7));
            libroTemporal.setPersona(persona);
            libroTemporal.setAutores(Set.of(autorActivo));
            libroTemporal = libroService.crearLibro(libroTemporal);
            libroService.eliminarLibro(libroTemporal.getId());
            log.info("   Libro temporal creado y marcado como eliminado (id={})", libroTemporal.getId());

            log.info("-> Resumen después de las operaciones");
            log.info("   Autores activos: {}", autorService.listarActivos().stream()
                    .map(Autor::getNombre)
                    .toList());
            log.info("   Libros activos: {}", libroService.listarActivos().stream()
                    .map(Libro::getTitulo)
                    .toList());

            return new DemoTestContext(autorActivo.getId(), libroPrincipal.getId());
        } catch (Exception e) {
            log.error("Error durante las pruebas de servicios", e);
            return new DemoTestContext(null, null);
        }
    }

    private void runRestSmokeTests(DemoTestContext context) {
        if (webServerContext == null) {
            log.warn("No hay contexto de servidor web; se omiten pruebas REST.");
            return;
        }
        if (context.autorId() == null || context.libroId() == null) {
            log.warn("No se pudieron ejecutar las pruebas REST porque faltan identificadores válidos");
            return;
        }
        try {
            int port = webServerContext.getWebServer().getPort();
            RestClient client = RestClient.builder()
                    .baseUrl("http://localhost:" + port + "/api")
                    .build();

            log.info("-> Ejecutando pruebas REST básicas contra http://localhost:{}.", port);

            List<AutorDto> autoresActivos = client.get()
                    .uri("/autores")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AutorDto>>() {});
            log.info("   REST /api/autores -> {} autores activos: {}", autoresActivos.size(),
                    autoresActivos.stream()
                            .map(a -> Map.of("id", a.getId(), "nombre", a.getNombre(), "apellido", a.getApellido()))
                            .collect(Collectors.toList()));

            LibroDto libroDetalle = client.get()
                    .uri("/libros/{id}", context.libroId())
                    .retrieve()
                    .body(LibroDto.class);
            if (libroDetalle != null) {
                log.info("   REST /api/libros/{} -> título={}, género={}, personaId={}, autores={}",
                        libroDetalle.getId(),
                        libroDetalle.getTitulo(),
                        libroDetalle.getGenero(),
                        libroDetalle.getPersonaId(),
                        libroDetalle.getAutoresIds());
            }

            List<LibroDto> librosAsignados = client.get()
                    .uri("/libros/asignados")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<LibroDto>>() {});
            log.info("   REST /api/libros/asignados -> {} registros", librosAsignados.size());

            List<LibroDto> librosSinAsignar = client.get()
                    .uri("/libros/sin-asignar")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<LibroDto>>() {});
            log.info("   REST /api/libros/sin-asignar -> {} registros", librosSinAsignar.size());
        } catch (Exception e) {
            log.error("Error durante las pruebas REST", e);
        }
    }

    private record DemoTestContext(Long autorId, Long libroId) {
    }
}
