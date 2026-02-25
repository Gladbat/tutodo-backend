package com.tutodo.backend.controller;

import com.tutodo.backend.dto.FavoritoDTO;
import com.tutodo.backend.dto.FavoritoRequest;
import com.tutodo.backend.service.FavoritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    /**
     * GET /api/favoritos/usuario/{usuarioId}
     * Obtener favoritos de un usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FavoritoDTO>> obtenerFavoritosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(favoritoService.obtenerFavoritosPorUsuario(usuarioId));
    }

    /**
     * GET /api/favoritos/verificar?usuarioId={id}&productoId={id}
     * Verificar si un producto está en favoritos
     */
    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Boolean>> verificarFavorito(
            @RequestParam Long usuarioId,
            @RequestParam Long productoId
    ) {
        boolean esFavorito = favoritoService.esFavorito(usuarioId, productoId);
        return ResponseEntity.ok(Map.of("esFavorito", esFavorito));
    }

    /**
     * GET /api/favoritos/producto/{productoId}/count
     * Contar favoritos de un producto
     */
    @GetMapping("/producto/{productoId}/count")
    public ResponseEntity<Map<String, Long>> contarFavoritos(@PathVariable Long productoId) {
        Long cantidad = favoritoService.contarFavoritosPorProducto(productoId);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    /**
     * POST /api/favoritos
     * Agregar producto a favoritos
     */
    @PostMapping
    public ResponseEntity<?> agregarFavorito(@Valid @RequestBody FavoritoRequest request) {
        try {
            FavoritoDTO favorito = favoritoService.agregarFavorito(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(favorito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/favoritos?usuarioId={id}&productoId={id}
     * Quitar producto de favoritos
     */
    @DeleteMapping
    public ResponseEntity<?> quitarFavorito(
            @RequestParam Long usuarioId,
            @RequestParam Long productoId
    ) {
        try {
            favoritoService.quitarFavorito(usuarioId, productoId);
            return ResponseEntity.ok().body(Map.of("message", "Producto quitado de favoritos"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/favoritos/{id}
     * Eliminar favorito por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFavorito(@PathVariable Long id) {
        try {
            favoritoService.eliminarFavorito(id);
            return ResponseEntity.ok().body(Map.of("message", "Favorito eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}