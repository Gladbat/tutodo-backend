package com.tutodo.backend.service;

import com.tutodo.backend.entity.Usuario;
import com.tutodo.backend.enums.Rol;
import com.tutodo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ReporteRepository reporteRepository;
    private final FavoritoRepository favoritoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Verificar si un usuario es admin
     */
    public boolean esAdmin(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(usuario -> usuario.getRol() == Rol.ADMIN)
                .orElse(false);
    }

    /**
     * Obtener estadísticas generales del sistema
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        // Usuarios
        long totalUsuarios = usuarioRepository.count();
        long totalAdmins = usuarioRepository.countByRol(Rol.ADMIN);

        // Productos
        long totalProductos = productoRepository.count();
        long productosActivos = productoRepository.countByEstaActivoTrue();
        long productosVendidos = productoRepository.countByVendidoTrue();

        // Reportes
        long totalReportes = reporteRepository.count();

        // Favoritos
        long totalFavoritos = favoritoRepository.count();

        // Categorías
        long totalCategorias = categoriaRepository.count();

        stats.put("totalUsuarios", totalUsuarios);
        stats.put("totalAdmins", totalAdmins);
        stats.put("totalProductos", totalProductos);
        stats.put("productosActivos", productosActivos);
        stats.put("productosVendidos", productosVendidos);
        stats.put("totalReportes", totalReportes);
        stats.put("totalFavoritos", totalFavoritos);
        stats.put("totalCategorias", totalCategorias);

        return stats;
    }
}