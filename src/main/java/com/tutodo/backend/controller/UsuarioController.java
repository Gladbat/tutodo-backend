package com.tutodo.backend.controller;

import com.tutodo.backend.dto.UsuarioDTO;
import com.tutodo.backend.dto.UsuarioUpdateRequest;
import com.tutodo.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * GET /api/usuarios
     * Obtener todos los usuarios (solo admin)
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            return ResponseEntity.ok(usuarioService.obtenerTodos());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/usuarios/{id}
     * Obtener perfil de usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Long id) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerPerfil(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/usuarios/{id}
     * Actualizar perfil de usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request
    ) {
        try {
            UsuarioDTO usuarioActualizado = usuarioService.actualizarPerfil(id, request);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
