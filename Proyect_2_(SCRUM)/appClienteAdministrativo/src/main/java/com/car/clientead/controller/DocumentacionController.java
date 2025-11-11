package com.car.clientead.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.car.clientead.business.logic.DocumentacionService;
import com.car.clientead.client.dto.DocumentacionDto;

@Controller
@RequestMapping("/documentaciones")
public class DocumentacionController {

    @Autowired
    private DocumentacionService documentacionService;

    @GetMapping("/archivo/{id}")
    public ResponseEntity<Resource> descargar(@PathVariable String id) {
        DocumentacionDto dto = documentacionService.consultar(id);
        Resource recurso = documentacionService.descargarRecurso(id);
        String nombre = dto != null && dto.getNombreArchivo() != null
                ? dto.getNombreArchivo()
                : "documentacion.zip";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .body(recurso);
    }
}
