package com.tutodo.backend.service;

import com.tutodo.backend.dto.UsuarioDTO;
import com.tutodo.backend.dto.UsuarioUpdateRequest;
import com.tutodo.backend.entity.Usuario;
import com.tutodo.backend.repository.FavoritoRepository;
import com.tutodo.backend.repository.ProductoRepository;
import com.tutodo.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final FavoritoRepository favoritoRepository;

    /**
     * Obtener todos los usuarios (solo admin)
     */
    public java.util.List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtener perfil de usuario
     */
    public UsuarioDTO obtenerPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return convertirADTO(usuario);
    }

    /**
     * Actualizar perfil de usuario
     */
    @Transactional
    public UsuarioDTO actualizarPerfil(Long usuarioId, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar solo los campos que no son null
        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }

        if (request.getApellido() != null) {
            usuario.setApellido(request.getApellido());
        }

        if (request.getNumeroWhatsapp() != null) {
            // Normalizar número de WhatsApp (remover espacios)
            String numeroWhatsapp = request.getNumeroWhatsapp().replaceAll("\\s+", "");
            
            // Validar que el nuevo número no esté en uso por otro usuario
            if (!usuario.getNumeroWhatsapp().equals(numeroWhatsapp) &&
                    usuarioRepository.existsByNumeroWhatsapp(numeroWhatsapp)) {
                throw new RuntimeException("El número de WhatsApp ya está en uso");
            }
            usuario.setNumeroWhatsapp(numeroWhatsapp);
        }

        if (request.getContrasena() != null) {
            usuario.setContrasena(request.getContrasena()); // En producción usar BCrypt
        }

        if (request.getPreguntaSeguridad() != null) {
            usuario.setPreguntaSeguridad(request.getPreguntaSeguridad());
        }

        if (request.getRespuestaSeguridad() != null) {
            usuario.setRespuestaSeguridad(request.getRespuestaSeguridad());
        }

        usuario.setFechaActualizacion(LocalDateTime.now());

        usuario = usuarioRepository.save(usuario);

        return convertirADTO(usuario);
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();

        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setNumeroWhatsapp(usuario.getNumeroWhatsapp());
        dto.setRol(usuario.getRol());  // ← AGREGAR
        dto.setFechaCreacion(usuario.getFechaCreacion());

        // Contar publicaciones del usuario
        dto.setCantidadPublicaciones(
                productoRepository.findByUsuarioId(usuario.getId()).size()
        );

        // Contar favoritos del usuario
        dto.setCantidadFavoritos(
                favoritoRepository.findByUsuarioId(usuario.getId()).size()
        );

        return dto;
    }
}