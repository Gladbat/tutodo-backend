package com.tutodo.backend.service;

import com.tutodo.backend.dto.FavoritoDTO;
import com.tutodo.backend.dto.FavoritoRequest;
import com.tutodo.backend.entity.Favorito;
import com.tutodo.backend.entity.Producto;
import com.tutodo.backend.entity.Usuario;
import com.tutodo.backend.repository.FavoritoRepository;
import com.tutodo.backend.repository.ProductoRepository;
import com.tutodo.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    /**
     * Obtener favoritos de un usuario
     */
    public List<FavoritoDTO> obtenerFavoritosPorUsuario(Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Verificar si un producto está en favoritos
     */
    public boolean esFavorito(Long usuarioId, Long productoId) {
        return favoritoRepository.existsByUsuarioIdAndProductoId(usuarioId, productoId);
    }

    /**
     * Agregar producto a favoritos
     */
    @Transactional
    public FavoritoDTO agregarFavorito(FavoritoRequest request) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el producto existe
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar que no esté ya en favoritos
        if (favoritoRepository.existsByUsuarioIdAndProductoId(request.getUsuarioId(), request.getProductoId())) {
            throw new RuntimeException("El producto ya está en favoritos");
        }

        // Crear favorito
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setProducto(producto);
        favorito.setFechaCreacion(LocalDateTime.now());

        favorito = favoritoRepository.save(favorito);

        return convertirADTO(favorito);
    }

    /**
     * Quitar producto de favoritos
     */
    @Transactional
    public void quitarFavorito(Long usuarioId, Long productoId) {
        if (!favoritoRepository.existsByUsuarioIdAndProductoId(usuarioId, productoId)) {
            throw new RuntimeException("El producto no está en favoritos");
        }

        favoritoRepository.deleteByUsuarioIdAndProductoId(usuarioId, productoId);
    }

    /**
     * Eliminar favorito por ID
     */
    @Transactional
    public void eliminarFavorito(Long favoritoId) {
        if (!favoritoRepository.existsById(favoritoId)) {
            throw new RuntimeException("Favorito no encontrado");
        }

        favoritoRepository.deleteById(favoritoId);
    }

    /**
     * Obtener cantidad de favoritos de un producto
     */
    public Long contarFavoritosPorProducto(Long productoId) {
        return favoritoRepository.countByProductoId(productoId);
    }

    /**
     * Convertir Entidad a DTO
     */
    private FavoritoDTO convertirADTO(Favorito favorito) {
        FavoritoDTO dto = new FavoritoDTO();

        dto.setId(favorito.getId());
        dto.setUsuarioId(favorito.getUsuario().getId());
        dto.setProductoId(favorito.getProducto().getId());
        dto.setFechaCreacion(favorito.getFechaCreacion());

        // Incluir información completa del producto
        dto.setProducto(productoService.obtenerProductoPorId(favorito.getProducto().getId()));

        return dto;
    }
}