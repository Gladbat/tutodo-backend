package com.tutodo.backend.controller;

import com.tutodo.backend.entity.ImagenProducto;
import com.tutodo.backend.service.ImagenProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
public class ImagenProductoController {

    private final ImagenProductoService imagenProductoService;

    /**
     * POST /api/imagenes/producto/{productoId}
     * Subir una imagen a un producto
     */
    @PostMapping("/producto/{productoId}")
    public ResponseEntity<?> subirImagen(
            @PathVariable Long productoId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Boolean esPrincipal,
            @RequestParam(required = false) Integer orden
    ) {
        try {
            ImagenProducto imagen = imagenProductoService.subirImagen(productoId, file, esPrincipal, orden);
            return ResponseEntity.status(HttpStatus.CREATED).body(imagen);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/imagenes/producto/{productoId}/multiple
     * Subir múltiples imágenes a un producto
     */
    @PostMapping("/producto/{productoId}/multiple")
    public ResponseEntity<?> subirMultiplesImagenes(
            @PathVariable Long productoId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        try {
            List<ImagenProducto> imagenes = imagenProductoService.subirMultiplesImagenes(productoId, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(imagenes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/imagenes/producto/{productoId}
     * Obtener todas las imágenes de un producto
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ImagenProducto>> obtenerImagenesProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(imagenProductoService.obtenerImagenesProducto(productoId));
    }

    /**
     * PATCH /api/imagenes/{imagenId}/principal
     * Establecer una imagen como principal
     */
    @PatchMapping("/{imagenId}/principal")
    public ResponseEntity<?> establecerImagenPrincipal(@PathVariable Long imagenId) {
        try {
            ImagenProducto imagen = imagenProductoService.establecerImagenPrincipal(imagenId);
            return ResponseEntity.ok(imagen);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/imagenes/{imagenId}
     * Eliminar una imagen
     */
    @DeleteMapping("/{imagenId}")
    public ResponseEntity<?> eliminarImagen(@PathVariable Long imagenId) {
        try {
            imagenProductoService.eliminarImagen(imagenId);
            return ResponseEntity.ok().body(Map.of("message", "Imagen eliminada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
