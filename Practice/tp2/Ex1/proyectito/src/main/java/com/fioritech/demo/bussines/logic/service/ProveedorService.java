package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Proveedor;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

@Service
@Transactional
public class ProveedorService {

    private final PersonaService personaService;
    private final ProveedorRepository proveedorRepository;

    public ProveedorService(PersonaService personaService, ProveedorRepository proveedorRepository) {
        this.personaService = personaService;
        this.proveedorRepository = proveedorRepository;
    }

    public Proveedor crearProveedor(Proveedor proveedor) {
        verificarAtributos(proveedor);
        if (proveedor.getId() != null) {
            throw new BusinessException("El proveedor ya tiene un id asignado");
        }
        proveedor.setNombre(proveedor.getNombre().trim());
        proveedor.setApellido(proveedor.getApellido().trim());
        proveedor.setTelefono(proveedor.getTelefono().trim());
        proveedor.setCorreo(proveedor.getCorreo().trim());
        proveedor.setCuit(proveedor.getCuit().trim());
        proveedor.setEliminado(false);
        return proveedorRepository.save(proveedor);
    }

    public Proveedor modificarProveedor(Long id, Proveedor cambios) {
        Proveedor existente = obtenerProveedorActivo(id);
        verificarAtributos(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setApellido(cambios.getApellido().trim());
        existente.setTelefono(cambios.getTelefono().trim());
        existente.setCorreo(cambios.getCorreo().trim());
        existente.setCuit(cambios.getCuit().trim());
        return proveedorRepository.save(existente);
    }

    public void eliminarProveedor(Long id) {
        Proveedor existente = obtenerProveedorActivo(id);
        existente.setEliminado(true);
        proveedorRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Proveedor> listarProveedores() {
        return proveedorRepository.buscarProveedoresActivos();
    }

    @Transactional(readOnly = true)
    public Proveedor buscarProveedorPorId(Long id) {
        return obtenerProveedorActivo(id);
    }

    @Transactional(readOnly = true)
    public byte[] exportarProveedoresPdf() {
        Collection<Proveedor> proveedores = proveedorRepository.buscarProveedoresActivos();

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            escribirListadoProveedores(document, proveedores);
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el PDF de proveedores", e);
        }
    }

    private void escribirListadoProveedores(PDDocument document, Collection<Proveedor> proveedores) throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);

        float margin = 50f;
        float leading = 16f;
        float yPosition = page.getMediaBox().getHeight() - margin;

        PDPageContentStream content = new PDPageContentStream(document, page);
        try {
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.newLineAtOffset(margin, yPosition);
            content.showText("Listado de Proveedores");
            content.endText();

            yPosition -= 2 * leading;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(margin, yPosition);

            if (proveedores.isEmpty()) {
                content.showText("No hay proveedores registrados");
            } else {
                for (Proveedor proveedor : proveedores) {
                    if (yPosition <= margin) {
                        content.endText();
                        content.close();

                        page = new PDPage();
                        document.addPage(page);
                        content = new PDPageContentStream(document, page);

                        yPosition = page.getMediaBox().getHeight() - margin;

                        content.beginText();
                        content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                        content.newLineAtOffset(margin, yPosition);
                        content.showText("Listado de Proveedores");
                        content.endText();

                        yPosition -= 2 * leading;

                        content.beginText();
                        content.setFont(PDType1Font.HELVETICA, 12);
                        content.newLineAtOffset(margin, yPosition);
                    }

                    String linea = construirLineaProveedor(proveedor);
                    content.showText(linea);
                    content.newLineAtOffset(0, -leading);
                    yPosition -= leading;
                }
            }
            content.endText();
        } finally {
            content.close();
        }
    }

    private String construirLineaProveedor(Proveedor proveedor) {
        String id = proveedor.getId() == null ? "" : proveedor.getId().toString();
        String nombre = proveedor.getNombre() == null ? "" : proveedor.getNombre();
        String apellido = proveedor.getApellido() == null ? "" : proveedor.getApellido();
        String cuit = proveedor.getCuit() == null ? "" : proveedor.getCuit();
        String telefono = proveedor.getTelefono() == null ? "" : proveedor.getTelefono();

        return String.format("ID: %s | %s %s | CUIT: %s | Tel: %s",
                id,
                nombre,
                apellido,
                cuit,
                telefono);
    }

    public void verificarAtributos(Proveedor proveedor) {
        personaService.verificarAtributos(proveedor);
        if (ValidationUtils.isBlank(proveedor.getCuit())) {
            throw new BusinessException("El CUIT es obligatorio");
        }
    }

    private Proveedor obtenerProveedorActivo(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el proveedor con id " + id));
        if (proveedor.isEliminado()) {
            throw new BusinessException("El proveedor con id " + id + " esta eliminado");
        }
        return proveedor;
    }
}
