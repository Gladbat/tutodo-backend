package com.tutodo.backend.controller;

import com.tutodo.backend.dto.AuthResponse;
import com.tutodo.backend.dto.LoginRequest;
import com.tutodo.backend.dto.RegisterRequest;
import com.tutodo.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Registrar nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.registrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/login
     * Iniciar sesión
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/verificar-email
     * Verificar email y obtener pregunta de seguridad
     */
    @PostMapping("/verificar-email")
    public ResponseEntity<?> verificarEmail(@Valid @RequestBody com.tutodo.backend.dto.VerificarEmailRequest request) {
        try {
            com.tutodo.backend.dto.PreguntaSeguridadResponse response = authService.verificarEmail(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/verificar-respuesta
     * Verificar respuesta de seguridad
     */
    @PostMapping("/verificar-respuesta")
    public ResponseEntity<?> verificarRespuesta(@Valid @RequestBody com.tutodo.backend.dto.VerificarRespuestaRequest request) {
        try {
            boolean esCorrecta = authService.verificarRespuesta(request.getEmail(), request.getRespuesta());
            if (esCorrecta) {
                return ResponseEntity.ok(Map.of("valida", true, "mensaje", "Respuesta correcta"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("valida", false, "error", "Respuesta incorrecta"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/restablecer-contrasena
     * Restablecer contraseña
     */
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<?> restablecerContrasena(@Valid @RequestBody com.tutodo.backend.dto.RestablecerContrasenaRequest request) {
        try {
            authService.restablecerContrasena(request.getEmail(), request.getNuevaContrasena());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
