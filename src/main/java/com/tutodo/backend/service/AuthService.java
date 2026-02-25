package com.tutodo.backend.service;

import com.tutodo.backend.dto.AuthResponse;
import com.tutodo.backend.dto.LoginRequest;
import com.tutodo.backend.dto.RegisterRequest;
import com.tutodo.backend.entity.Usuario;
import com.tutodo.backend.enums.Rol;
import com.tutodo.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    @Value("${app.soporte.email}")
    private String soporteEmail;

    /**
     * Registrar nuevo usuario
     */
    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        // Normalizar número de WhatsApp (remover espacios)
        String numeroWhatsapp = request.getNumeroWhatsapp().replaceAll("\\s+", "");
        
        // Validar que el email no exista o esté eliminado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            Usuario usuarioExistente = usuarioRepository.findByEmail(request.getEmail()).get();
            if (usuarioExistente.getEliminado() != null && usuarioExistente.getEliminado()) {
                throw new RuntimeException("Este correo no puede ser registrado. Para más información contacte a: " + soporteEmail);
            }
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar que el número de WhatsApp no exista (normalizado)
        if (usuarioRepository.existsByNumeroWhatsapp(numeroWhatsapp)) {
            throw new RuntimeException("El número de WhatsApp ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setContrasena(request.getContrasena());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setNumeroWhatsapp(numeroWhatsapp); // Guardar sin espacios
        usuario.setPreguntaSeguridad(request.getPreguntaSeguridad());
        usuario.setRespuestaSeguridad(request.getRespuestaSeguridad());
        usuario.setRol(Rol.USER);  // ← Siempre USER por defecto
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());

        usuario = usuarioRepository.save(usuario);

        return new AuthResponse(
                usuario.getId(),
                usuario.getRol(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getNumeroWhatsapp(),
                "Usuario registrado exitosamente"
        );
    }

    /**
     * Iniciar sesión
     */
    public AuthResponse login(LoginRequest request) {
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        // Validar contraseña
        if (!usuario.getContrasena().equals(request.getContrasena())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        // Validar si la cuenta está eliminada
        if (usuario.getEliminado() != null && usuario.getEliminado()) {
            throw new RuntimeException("Cuenta eliminada. Si necesita información puede contactar a este correo para más información: " + soporteEmail);
        }

        // Validar si la cuenta está suspendida
        if (usuario.getSuspendido() != null && usuario.getSuspendido()) {
            throw new RuntimeException("Cuenta suspendida. Si necesita información puede contactar a este correo para más información: " + soporteEmail);
        }

        return new AuthResponse(
                usuario.getId(),
                usuario.getRol(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getNumeroWhatsapp(),
                "Inicio de sesión exitoso"
        );
    }

    /**
     * Verificar email y obtener pregunta de seguridad
     */
    public com.tutodo.backend.dto.PreguntaSeguridadResponse verificarEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe una cuenta con este email"));

        // Validar que no esté eliminado
        if (usuario.getEliminado() != null && usuario.getEliminado()) {
            throw new RuntimeException("Esta cuenta ha sido eliminada");
        }

        // Verificar si tiene pregunta de seguridad configurada
        if (usuario.getPreguntaSeguridad() == null || usuario.getPreguntaSeguridad().isEmpty()) {
            return new com.tutodo.backend.dto.PreguntaSeguridadResponse(
                    "Esta cuenta no tiene pregunta de seguridad configurada. Contacte a soporte: " + soporteEmail,
                    false
            );
        }

        return new com.tutodo.backend.dto.PreguntaSeguridadResponse(
                usuario.getPreguntaSeguridad(),
                true
        );
    }

    /**
     * Verificar respuesta de seguridad
     */
    public boolean verificarRespuesta(String email, String respuesta) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRespuestaSeguridad() == null) {
            throw new RuntimeException("No hay respuesta de seguridad configurada");
        }

        // Comparar respuestas (case insensitive y sin espacios)
        String respuestaUsuario = respuesta.trim().toLowerCase();
        String respuestaGuardada = usuario.getRespuestaSeguridad().trim().toLowerCase();

        return respuestaUsuario.equals(respuestaGuardada);
    }

    /**
     * Restablecer contraseña
     */
    @Transactional
    public void restablecerContrasena(String email, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setContrasena(nuevaContrasena);
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}
