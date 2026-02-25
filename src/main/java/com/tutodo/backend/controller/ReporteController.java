package com.tutodo.backend.controller;

import com.tutodo.backend.dto.ReporteDTO;
import com.tutodo.backend.dto.ReporteRequest;
import com.tutodo.backend.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * GET /api/reportes/producto/{productoId}
     * Obtener reportes de un producto
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ReporteDTO>> obtenerReportesPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(reporteService.obtenerReportesPorProducto(productoId));
    }


    /**
     * GET /api/reportes/producto/{productoId}/count
     * Contar reportes de un producto
     */
    @GetMapping("/producto/{productoId}/count")
    public ResponseEntity<Map<String, Long>> contarReportes(@PathVariable Long productoId) {
        Long cantidad = reporteService.contarReportesPorProducto(productoId);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    /**
     * GET /api/reportes/verificar/{productoId}/{usuarioId}
     * Verificar si un usuario ya reportó un producto
     */
    @GetMapping("/verificar/{productoId}/{usuarioId}")
    public ResponseEntity<Map<String, Boolean>> verificarReporte(
            @PathVariable Long productoId,
            @PathVariable Long usuarioId) {
        boolean yaReporto = reporteService.usuarioYaReporto(productoId, usuarioId);
        return ResponseEntity.ok(Map.of("yaReporto", yaReporto));
    }

    /**
     * GET /api/reportes/usuario/{usuarioId}
     * Obtener reportes realizados por un usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReporteDTO>> obtenerReportesPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(reporteService.obtenerReportesPorUsuario(usuarioId));
    }

    /**
     * POST /api/reportes
     * Crear un reporte
     */
    @PostMapping
    public ResponseEntity<?> crearReporte(@Valid @RequestBody ReporteRequest request) {
        try {
            ReporteDTO reporte = reporteService.crearReporte(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reporte);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/reportes/{id}
     * Eliminar un reporte (admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReporte(@PathVariable Long id) {
        try {
            reporteService.eliminarReporte(id);
            return ResponseEntity.ok().body(Map.of("message", "Reporte eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }




}