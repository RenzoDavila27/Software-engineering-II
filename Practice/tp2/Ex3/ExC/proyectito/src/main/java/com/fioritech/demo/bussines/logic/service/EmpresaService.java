package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.domain.Empresa;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.template.CrudTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

@Service
@Transactional
public class EmpresaService extends CrudTemplateService<Empresa, Long> {

    private final EmpresaRepository empresaRepository;
    private final DireccionService direccionService;

    public EmpresaService(EmpresaRepository empresaRepository,
                          DireccionService direccionService) {
        this.empresaRepository = empresaRepository;
        this.direccionService = direccionService;
    }

    public Empresa crearEmpresa(Empresa empresa) {
        return crearEntidad(empresa);
    }

    public Empresa modificarEmpresa(Long id, Empresa cambios) {
        return modificarEntidad(id, cambios);
    }

    public void eliminarEmpresa(Long id) {
        eliminarEntidad(id);
    }

    @Transactional(readOnly = true)
    public Collection<Empresa> listarEmpresas() {
        return listarEntidades();
    }

    @Transactional(readOnly = true)
    public Empresa buscarEmpresaPorId(Long id) {
        return buscarEntidad(id);
    }

    @Transactional(readOnly = true)
    public byte[] exportarEmpresasExcel() {
        Collection<Empresa> empresas = listarEntidades();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Empresas");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Razón Social");

            int rowIndex = 1;
            for (Empresa empresa : empresas) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0)
                        .setCellValue(empresa.getId() == null ? "" : empresa.getId().toString());
                row.createCell(1).setCellValue(empresa.getRazonSocial());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el archivo de empresas", e);
        }
    }

    public void verificarAtributos(Empresa empresa) {
        if (empresa == null) {
            throw new BusinessException("La empresa es obligatoria");
        }
        if (ValidationUtils.isBlank(empresa.getRazonSocial())) {
            throw new BusinessException("La razon social es obligatoria");
        }
        if (empresa.getDireccion() == null || empresa.getDireccion().getLocalidad() == null
                || empresa.getDireccion().getLocalidad().getId() == null) {
            throw new BusinessException("La empresa debe tener una direccion asociada");
        }
    }

    @Override
    protected void validarEntidad(Empresa empresa) {
        verificarAtributos(empresa);
    }

    @Override
    protected void validarEntidadNueva(Empresa empresa) {
        if (empresa.getId() != null) {
            throw new BusinessException("La empresa ya tiene un id asignado");
        }
    }

    @Override
    protected void antesDeCrear(Empresa empresa) {
        prepararDireccionParaGuardar(empresa);
        empresa.setRazonSocial(empresa.getRazonSocial().trim());
        empresa.setEliminado(false);
    }

    @Override
    protected void antesDeModificar(Empresa existente, Empresa cambios) {
        prepararDireccionExistente(existente, cambios);
    }

    @Override
    protected void aplicarCambios(Empresa existente, Empresa cambios) {
        existente.setRazonSocial(cambios.getRazonSocial().trim());
    }

    @Override
    protected void marcarEliminado(Empresa empresa) {
        empresa.setEliminado(true);
    }

    @Override
    protected Empresa guardar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @Override
    protected Empresa obtenerPorId(Long id) {
        return obtenerEmpresaActiva(id);
    }

    @Override
    protected Collection<Empresa> obtenerListado() {
        return empresaRepository.buscarEmpresasActivas();
    }

    private void prepararDireccionParaGuardar(Empresa empresa) {
        if (empresa.getDireccion() == null) {
            throw new BusinessException("La empresa debe tener una direccion asociada");
        }
        Direccion direccion = direccionService.crearDireccion(empresa.getDireccion());
        empresa.setDireccion(direccion);
    }

    private void prepararDireccionExistente(Empresa existente, Empresa cambios) {
        if (cambios.getDireccion() == null) {
            throw new BusinessException("La empresa debe tener una direccion asociada");
        }
        Direccion direccionActualizada;
        if (cambios.getDireccion().getId() == null) {
            direccionActualizada = direccionService.crearDireccion(cambios.getDireccion());
        } else {
            direccionActualizada = direccionService.modificarDireccion(
                    cambios.getDireccion().getId(),
                    cambios.getDireccion());
        }
        existente.setDireccion(direccionActualizada);
    }

    private Empresa obtenerEmpresaActiva(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe la empresa con id " + id));
        if (empresa.isEliminado()) {
            throw new BusinessException("La empresa con id " + id + " esta eliminada");
        }
        return empresa;
    }
}

