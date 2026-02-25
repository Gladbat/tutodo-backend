package com.tutodo.backend.controller;

import com.tutodo.backend.dto.ProductoDTO;
import com.tutodo.backend.dto.ReporteDTO;
import com.tutodo.backend.dto.UsuarioDTO;
import com.tutodo.backend.entity.Producto;
import com.tutodo.backend.entity.Reporte;
import com.tutodo.backend.entity.Usuario;
import com.tutodo.backend.repository.ProductoRepository;
import com.tutodo.backend.repository.ReporteRepository;
import com.tutodo.backend.repository.UsuarioRepository;
import com.tutodo.backend.service.AdminService;
import com.tutodo.backend.service.ProductoService;
import com.tutodo.backend.service.ReporteService;
import com.tutodo.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ReporteRepository reporteRepository;
    private final UsuarioService usuarioService;
    private final ReporteService reporteService;
    private final ProductoService productoService;

    /**
     * Verificar acceso de admin (middleware básico)
     */
    private void verificarAdmin(Long usuarioId) {
        if (!adminService.esAdmin(usuarioId)) {
            throw new RuntimeException("Acceso denegado. Se requiere rol de administrador.");
        }
    }

    /**
     * GET /api/admin/estadisticas?adminId={id}
     * Obtener estadísticas generales
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas(@RequestParam Long adminId) {
        try {
            verificarAdmin(adminId);
            return ResponseEntity.ok(adminService.obtenerEstadisticas());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/usuarios?adminId={id}
     * Obtener todos los usuarios
     */
    @GetMapping("/usuarios")
    public ResponseEntity<?> obtenerTodosUsuarios(@RequestParam Long adminId) {
        try {
            verificarAdmin(adminId);
            List<Usuario> usuarios = usuarioRepository.findAll();
            
            // Agregar contador de publicaciones a cada usuario
            List<UsuarioDTO> usuariosDTO = usuarios.stream()
                    .map(usuario -> {
                        UsuarioDTO dto = new UsuarioDTO();
                        dto.setId(usuario.getId());
                        dto.setEmail(usuario.getEmail());
                        dto.setNombre(usuario.getNombre());
                        dto.setApellido(usuario.getApellido());
                        dto.setNumeroWhatsapp(usuario.getNumeroWhatsapp());
                        dto.setFechaCreacion(usuario.getFechaCreacion());
                        dto.setRol(usuario.getRol());
                        dto.setSuspendido(usuario.getSuspendido());
                        dto.setEliminado(usuario.getEliminado());
                        
                        // Contar publicaciones del usuario
                        Long cantidadPublicaciones = productoRepository.countByUsuarioId(usuario.getId());
                        dto.setCantidadPublicaciones(cantidadPublicaciones.intValue());
                        
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(usuariosDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/productos?adminId={id}
     * Obtener todos los productos (incluso inactivos)
     */
    @GetMapping("/productos")
    public ResponseEntity<?> obtenerTodosProductos(@RequestParam Long adminId) {
        try {
            verificarAdmin(adminId);
            List<Producto> productos = productoRepository.findAll();
            
            // Convertir a DTOs para incluir nombres de categoría y usuario
            List<ProductoDTO> productosDTO = productos.stream()
                    .map(producto -> {
                        ProductoDTO dto = new ProductoDTO();
                        dto.setId(producto.getId());
                        dto.setNombre(producto.getNombre());
                        dto.setDescripcion(producto.getDescripcion());
                        dto.setPrecio(producto.getPrecio());
                        dto.setEstado(producto.getEstado());
                        dto.setEstaActivo(producto.getEstaActivo());
                        dto.setVendido(producto.getVendido());
                        dto.setEliminado(producto.getEliminado());
                        dto.setFechaCreacion(producto.getFechaCreacion());
                        dto.setFechaActualizacion(producto.getFechaActualizacion());
                        dto.setDireccion(producto.getDireccion());
                        dto.setLatitud(producto.getLatitud());
                        dto.setLongitud(producto.getLongitud());
                        
                        // Categoría
                        if (producto.getCategoria() != null) {
                            dto.setCategoriaId(producto.getCategoria().getId());
                            dto.setCategoriaNombre(producto.getCategoria().getNombre());
                        }
                        
                        // Usuario
                        if (producto.getUsuario() != null) {
                            dto.setUsuarioId(producto.getUsuario().getId());
                            dto.setUsuarioNombre(producto.getUsuario().getNombre() + " " + producto.getUsuario().getApellido());
                            dto.setUsuarioWhatsapp(producto.getUsuario().getNumeroWhatsapp());
                        }
                        
                        // Imágenes
                        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
                            // Obtener todas las URLs de imágenes
                            List<String> urls = producto.getImagenes().stream()
                                    .map(img -> img.getUrlImagen())
                                    .collect(Collectors.toList());
                            dto.setImagenesUrls(urls);
                            
                            // Obtener imagen principal
                            producto.getImagenes().stream()
                                    .filter(img -> img.getEsPrincipal() != null && img.getEsPrincipal())
                                    .findFirst()
                                    .ifPresent(img -> dto.setImagenPrincipal(img.getUrlImagen()));
                            
                            // Si no hay imagen principal marcada, usar la primera
                            if (dto.getImagenPrincipal() == null && !urls.isEmpty()) {
                                dto.setImagenPrincipal(urls.get(0));
                            }
                        }
                        
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(productosDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/reportes?adminId={id}
     * Obtener todos los reportes
     */
    @GetMapping("/reportes")
    public ResponseEntity<?> obtenerTodosReportes(@RequestParam Long adminId) {
        try {
            verificarAdmin(adminId);
            List<Reporte> reportes = reporteRepository.findAll();
            
            // Convertir a DTOs para incluir nombres de producto y usuario
            List<ReporteDTO> reportesDTO = reportes.stream()
                    .map(reporte -> {
                        ReporteDTO dto = new ReporteDTO();
                        dto.setId(reporte.getId());
                        dto.setProductoId(reporte.getProducto().getId());
                        dto.setProductoNombre(reporte.getProducto().getNombre());
                        dto.setUsuarioReportadorId(reporte.getUsuarioReportador().getId());
                        dto.setUsuarioReportadorNombre(
                                reporte.getUsuarioReportador().getNombre() + " " +
                                        reporte.getUsuarioReportador().getApellido()
                        );
                        dto.setRazon(reporte.getRazon());
                        dto.setComentario(reporte.getComentario());
                        dto.setFechaCreacion(reporte.getFechaCreacion());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(reportesDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/usuarios/{id}/suspender?adminId={adminId}
     * Suspender usuario (admin)
     */
    @PutMapping("/usuarios/{id}/suspender")
    public ResponseEntity<?> suspenderUsuario(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        try {
            verificarAdmin(adminId);
            
            // No permitir suspender la propia cuenta
            if (id.equals(adminId)) {
                throw new RuntimeException("No puedes suspender tu propia cuenta de administrador");
            }
            
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuario.setSuspendido(true);
            usuario.setFechaActualizacion(java.time.LocalDateTime.now());
            usuarioRepository.save(usuario);
            
            // Ocultar todos los productos del usuario suspendido
            List<Producto> productos = productoRepository.findByUsuarioId(id);
            productos.forEach(producto -> {
                producto.setEstaActivo(false);
                producto.setFechaActualizacion(java.time.LocalDateTime.now());
            });
            productoRepository.saveAll(productos);
            
            return ResponseEntity.ok(Map.of("message", "Usuario suspendido exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/usuarios/{id}/reactivar?adminId={adminId}
     * Reactivar usuario suspendido (admin)
     */
    @PutMapping("/usuarios/{id}/reactivar")
    public ResponseEntity<?> reactivarUsuario(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        try {
            verificarAdmin(adminId);
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // No permitir reactivar usuarios eliminados
            if (usuario.getEliminado() != null && usuario.getEliminado()) {
                throw new RuntimeException("No se puede reactivar un usuario eliminado");
            }
            
            usuario.setSuspendido(false);
            usuario.setFechaActualizacion(java.time.LocalDateTime.now());
            usuarioRepository.save(usuario);
            
            // Reactivar todos los productos del usuario
            List<Producto> productos = productoRepository.findByUsuarioId(id);
            productos.forEach(producto -> {
                if (!producto.getVendido()) { // Solo reactivar si no está vendido
                    producto.setEstaActivo(true);
                    producto.setFechaActualizacion(java.time.LocalDateTime.now());
                }
            });
            productoRepository.saveAll(productos);
            
            return ResponseEntity.ok(Map.of("message", "Usuario reactivado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/usuarios/{id}?adminId={adminId}
     * Eliminar usuario (admin) - Borrado lógico
     */
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        try {
            verificarAdmin(adminId);
            
            // No permitir eliminar al propio admin
            if (id.equals(adminId)) {
                throw new RuntimeException("No puedes eliminar tu propia cuenta de administrador");
            }
            
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Marcar usuario como eliminado
            usuario.setEliminado(true);
            usuario.setFechaActualizacion(java.time.LocalDateTime.now());
            usuarioRepository.save(usuario);
            
            // Eliminar todos los productos del usuario (cascade eliminará favoritos y reportes)
            List<Producto> productos = productoRepository.findByUsuarioId(id);
            productoRepository.deleteAll(productos);
            
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/productos/{id}/reactivar?adminId={adminId}
     * Reactivar producto inactivo (admin)
     */
    @PutMapping("/productos/{id}/reactivar")
    public ResponseEntity<?> reactivarProducto(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        try {
            verificarAdmin(adminId);
            ProductoDTO producto = productoService.reactivarProducto(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/productos/{id}/inhabilitar?adminId={adminId}
     * Inhabilitar producto activo (admin)
     */
    @PutMapping("/productos/{id}/inhabilitar")
    public ResponseEntity<?> inhabilitarProducto(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        try {
            verificarAdmin(adminId);
            
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            producto.setEstaActivo(false);
            producto.setFechaActualizacion(java.time.LocalDateTime.now());
            productoRepository.save(producto);
            
            return ResponseEntity.ok(Map.of("message", "Producto inhabilitado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/productos/{id}?adminId={adminId}
     * Eliminar producto (admin)
     */
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminarProducto(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        try {
            verificarAdmin(adminId);
            
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            producto.setEstaActivo(false);
            producto.setEliminado(true);
            // producto.setEliminadoPor("ADMIN"); // Comentado hasta que se pueda agregar la columna
            producto.setFechaActualizacion(java.time.LocalDateTime.now());
            
            productoRepository.save(producto);
            
            return ResponseEntity.ok(Map.of("message", "Producto eliminado por administrador"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}