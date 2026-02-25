package com.tutodo.backend.controller;

import com.tutodo.backend.dto.ProductoCreateRequest;
import com.tutodo.backend.dto.ProductoDTO;
import com.tutodo.backend.dto.ProductoUpdateRequest;
import com.tutodo.backend.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.data.domain.Page;


import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * GET /api/productos
     * Obtener todos los productos activos y no vendidos
     * @param usuarioId (opcional) - Si se proporciona, excluye productos reportados por este usuario
     */
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerProductosActivos(
            @RequestParam(required = false) Long usuarioId
    ) {
        return ResponseEntity.ok(productoService.obtenerProductosActivos(usuarioId));
    }

    /**
     * GET /api/productos/{id}
     * Obtener un producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/productos/buscar?q=keyword
     * Buscar productos por palabra clave
     * @param usuarioId (opcional) - Si se proporciona, excluye productos reportados por este usuario
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarProductos(
            @RequestParam String q,
            @RequestParam(required = false) Long usuarioId
    ) {
        return ResponseEntity.ok(productoService.buscarProductos(q, usuarioId));
    }

    /**
     * GET /api/productos/categoria/{categoriaId}
     * Obtener productos por categoría
     * @param usuarioId (opcional) - Si se proporciona, excluye productos reportados por este usuario
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(required = false) Long usuarioId
    ) {
        return ResponseEntity.ok(productoService.obtenerProductosPorCategoria(categoriaId, usuarioId));
    }

    /**
     * GET /api/productos/usuario/{usuarioId}
     * Obtener productos de un usuario específico
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(productoService.obtenerProductosPorUsuario(usuarioId));
    }

    /**
     * POST /api/productos
     * Crear un nuevo producto
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoCreateRequest request) {
        try {
            ProductoDTO productoCreado = productoService.crearProducto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/productos/{id}
     * Actualizar un producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateRequest request
    ) {
        try {
            ProductoDTO productoActualizado = productoService.actualizarProducto(id, request);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/productos/{id}/vendido
     * Marcar producto como vendido
     */
    @PatchMapping("/{id}/vendido")
    public ResponseEntity<?> marcarComoVendido(@PathVariable Long id) {
        try {
            ProductoDTO productoActualizado = productoService.marcarComoVendido(id);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/productos/{id}
     * Eliminar producto (borrado lógico - marca como inactivo)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().body(Map.of("message", "Producto eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/productos/{id}/permanente
     * Eliminar producto definitivamente (borrado físico)
     */
    @DeleteMapping("/{id}/permanente")
    public ResponseEntity<?> eliminarProductoDefinitivamente(@PathVariable Long id) {
        try {
            productoService.eliminarProductoDefinitivamente(id);
            return ResponseEntity.ok().body(Map.of("message", "Producto eliminado permanentemente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/productos/paginado?page=0&size=12
     * Obtener productos activos con paginación
     * @param usuarioId (opcional) - Si se proporciona, excluye productos reportados por este usuario
     */
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProductoDTO>> obtenerProductosActivosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long usuarioId
    ) {
        return ResponseEntity.ok(productoService.obtenerProductosActivosPaginados(page, size, usuarioId));
    }

    /**
     * GET /api/productos/buscar/paginado?q=keyword&page=0&size=12
     * Buscar productos con paginación
     * @param usuarioId (opcional) - Si se proporciona, excluye productos reportados por este usuario
     */
    @GetMapping("/buscar/paginado")
    public ResponseEntity<Page<ProductoDTO>> buscarProductosPaginados(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long usuarioId
    ) {
        return ResponseEntity.ok(productoService.buscarProductosPaginados(q, page, size, usuarioId));
    }

    /**
     * GET /api/productos/categoria/{categoriaId}/paginado?page=0&size=12
     * Obtener productos por categoría con paginación
     * @param usuarioId (opcional) - Si se proporciona, excluye productos reportados por este usuario
     */
    @GetMapping("/categoria/{categoriaId}/paginado")
    public ResponseEntity<Page<ProductoDTO>> obtenerProductosPorCategoriaPaginados(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long usuarioId
    ) {
        return ResponseEntity.ok(productoService.obtenerProductosPorCategoriaPaginados(categoriaId, page, size, usuarioId));
    }

    /**
     * GET /api/productos/cercanos?latitud=-8.1116&longitud=-79.0288&radio=2&categoriaId=1
     * Buscar productos cercanos a una ubicación
     */
    @GetMapping("/cercanos")
    public ResponseEntity<Map<String, Object>> buscarProductosCercanos(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam Double radio,
            @RequestParam(required = false) Long categoriaId
    ) {
        List<ProductoDTO> productos = productoService.buscarProductosCercanos(latitud, longitud, radio, categoriaId);
        
        Map<String, Object> response = Map.of(
            "productos", productos,
            "total", productos.size()
        );
        
        return ResponseEntity.ok(response);
    }
}