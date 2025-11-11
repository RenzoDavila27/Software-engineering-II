package com.car.clientead.controller;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.car.clientead.business.logic.DashboardService;
import com.car.clientead.business.logic.view.DashboardReportView;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public String mostrarDashboard(
            @RequestParam(value = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {
        DashboardReportView reporte;
        try {
            reporte = dashboardService.generarReporte(desde, hasta);
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            reporte = dashboardService.inicializarReporte(desde, hasta);
        }
        model.addAttribute("titleList", "Dashboard");
        model.addAttribute("reporte", reporte);
        model.addAttribute("vehiculos", reporte.getVehiculosAlquilados());
        model.addAttribute("recaudaciones", reporte.getRecaudacionPorModelo());
        model.addAttribute("filtroDesde", reporte.getFiltroDesde());
        model.addAttribute("filtroHasta", reporte.getFiltroHasta());
        return "dashboard.html";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportarDashboard(
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        try {
            String lowerTipo = tipo != null ? tipo.toLowerCase(Locale.ROOT) : "";
            if ("pdf".equals(lowerTipo)) {
                byte[] pdf = dashboardService.generarReportePdf(desde, hasta);
                String fileName = construirNombreArchivo("pdf", desde, hasta);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdf);
            }
            if ("excel".equals(lowerTipo)) {
                byte[] excel = dashboardService.generarReporteExcel(desde, hasta);
                String fileName = construirNombreArchivo("xlsx", desde, hasta);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(excel);
            }
            return ResponseEntity.badRequest().build();
        } catch (ApiClientException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String construirNombreArchivo(String extension, LocalDate desde, LocalDate hasta) {
        String desdeStr = desde != null ? desde.toString() : "sin-desde";
        String hastaStr = hasta != null ? hasta.toString() : "sin-hasta";
        return String.format("dashboard-%s-%s.%s", desdeStr, hastaStr, extension);
    }
}
