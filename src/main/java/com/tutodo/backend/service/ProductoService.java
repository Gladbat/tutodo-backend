package com.tutodo.backend.service;

import com.tutodo.backend.dto.ProductoCreateRequest;
import com.tutodo.backend.dto.ProductoDTO;
import com.tutodo.backend.dto.ProductoUpdateRequest;
import com.tutodo.backend.entity.*;
import com.tutodo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ImagenProductoRepository imagenProductoRepository;
    private final FavoritoRepository favoritoRepository;
    private final ReporteRepository reporteRepository;
    private final ModerationService moderationService;

    /**
     * Obtener todos los productos activos (sin paginación)
     */
    public List<ProductoDTO> obtenerProductosActivos() {
        return productoRepository.findByEstaActivoTrueAndVendidoFalseOrderByFechaCreacionDesc()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener productos activos filtrando los reportados por un usuario
     * y excluyendo los productos del propio usuario
     */
    public List<ProductoDTO> obtenerProductosActivos(Long usuarioId) {
        List<Producto> productos = productoRepository.findByEstaActivoTrueAndVendidoFalseOrderByFechaCreacionDesc();
        
        if (usuarioId != null) {
            List<Long> productosReportados = reporteRepository.findProductoIdsReportadosPorUsuario(usuarioId);
            productos = productos.stream()
                    .filter(p -> !productosReportados.contains(p.getId()))
                    .filter(p -> !p.getUsuario().getId().equals(usuarioId)) // Excluir productos propios
                    .collect(Collectors.toList());
        }
        
        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener producto por ID
     */
    public ProductoDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return convertirADTO(producto);
    }

    /**
     * Buscar productos por palabra clave (sin paginación)
     */
    public List<ProductoDTO> buscarProductos(String keyword) {
        return productoRepository.buscarPorPalabraClave(keyword)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar productos filtrando los reportados por un usuario
     * y excluyendo los productos del propio usuario
     */
    public List<ProductoDTO> buscarProductos(String keyword, Long usuarioId) {
        List<Producto> productos = productoRepository.buscarPorPalabraClave(keyword);
        
        if (usuarioId != null) {
            List<Long> productosReportados = reporteRepository.findProductoIdsReportadosPorUsuario(usuarioId);
            productos = productos.stream()
                    .filter(p -> !productosReportados.contains(p.getId()))
                    .filter(p -> !p.getUsuario().getId().equals(usuarioId)) // Excluir productos propios
                    .collect(Collectors.toList());
        }
        
        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener productos por categoría (sin paginación)
     */
    public List<ProductoDTO> obtenerProductosPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndEstaActivoTrue(categoriaId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener productos por categoría filtrando los reportados por un usuario
     * y excluyendo los productos del propio usuario
     */
    public List<ProductoDTO> obtenerProductosPorCategoria(Long categoriaId, Long usuarioId) {
        List<Producto> productos = productoRepository.findByCategoriaIdAndEstaActivoTrue(categoriaId);
        
        if (usuarioId != null) {
            List<Long> productosReportados = reporteRepository.findProductoIdsReportadosPorUsuario(usuarioId);
            productos = productos.stream()
                    .filter(p -> !productosReportados.contains(p.getId()))
                    .filter(p -> !p.getUsuario().getId().equals(usuarioId)) // Excluir productos propios
                    .collect(Collectors.toList());
        }
        
        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener productos por usuario
     */
    public List<ProductoDTO> obtenerProductosPorUsuario(Long usuarioId) {
        return productoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear producto
     */
    @Transactional
    public ProductoDTO crearProducto(ProductoCreateRequest request) {
        // 1. Validar palabras prohibidas
        if (moderationService.validarContenidoProducto(request.getNombre(), request.getDescripcion())) {
            List<String> palabrasEncontradas = moderationService.encontrarPalabrasProhibidas(
                    request.getNombre() + " " + request.getDescripcion()
            );
            throw new RuntimeException("Contenido inapropiado detectado. Palabras prohibidas: " + palabrasEncontradas);
        }

        // 2. Validar que la categoría existe
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // 3. Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 4. Crear el producto
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setEstado(request.getEstado());
        
        // Convertir string vacío a null para el campo JSON
        String atributosExtra = request.getAtributosExtra();
        if (atributosExtra != null && atributosExtra.trim().isEmpty()) {
            atributosExtra = null;
        }
        producto.setAtributosExtra(atributosExtra);
        
        producto.setLatitud(request.getLatitud());
        producto.setLongitud(request.getLongitud());
        producto.setDireccion(request.getDireccion());
        producto.setCategoria(categoria);
        producto.setUsuario(usuario);
        producto.setEstaActivo(true);
        producto.setVendido(false);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaActualizacion(LocalDateTime.now());

        // 5. Guardar
        producto = productoRepository.save(producto);

        return convertirADTO(producto);
    }

    /**
     * Actualizar producto
     */
    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoUpdateRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Validar palabras prohibidas si se actualiza nombre o descripción
        String nombreActualizado = request.getNombre() != null ? request.getNombre() : producto.getNombre();
        String descripcionActualizada = request.getDescripcion() != null ? request.getDescripcion() : producto.getDescripcion();

        if (moderationService.validarContenidoProducto(nombreActualizado, descripcionActualizada)) {
            throw new RuntimeException("Contenido inapropiado detectado");
        }

        // Actualizar solo los campos que no son null
        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecio() != null) {
            producto.setPrecio(request.getPrecio());
        }
        if (request.getEstado() != null) {
            producto.setEstado(request.getEstado());
        }
        if (request.getAtributosExtra() != null) {
            // Convertir string vacío a null para el campo JSON
            String atributosExtra = request.getAtributosExtra();
            if (atributosExtra.trim().isEmpty()) {
                atributosExtra = null;
            }
            producto.setAtributosExtra(atributosExtra);
        }
        if (request.getLatitud() != null) {
            producto.setLatitud(request.getLatitud());
        }
        if (request.getLongitud() != null) {
            producto.setLongitud(request.getLongitud());
        }
        if (request.getDireccion() != null) {
            producto.setDireccion(request.getDireccion());
        }
        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }
        if (request.getEstaActivo() != null) {
            producto.setEstaActivo(request.getEstaActivo());
        }
        if (request.getVendido() != null) {
            producto.setVendido(request.getVendido());
        }

        producto.setFechaActualizacion(LocalDateTime.now());

        producto = productoRepository.save(producto);

        return convertirADTO(producto);
    }

    /**
     * Marcar producto como vendido
     */
    @Transactional
    public ProductoDTO marcarComoVendido(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setVendido(true);
        producto.setEstaActivo(false);
        producto.setFechaActualizacion(LocalDateTime.now());

        producto = productoRepository.save(producto);

        return convertirADTO(producto);
    }

    /**
     * Eliminar producto (borrado lógico)
     */
    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setEstaActivo(false);
        producto.setEliminado(true);
        // producto.setEliminadoPor("USUARIO"); // Comentado hasta que se pueda agregar la columna
        producto.setFechaActualizacion(LocalDateTime.now());

        productoRepository.save(producto);
    }

    /**
     * Eliminar producto definitivamente (físico)
     */
    @Transactional
    public void eliminarProductoDefinitivamente(Long id) {
        productoRepository.deleteById(id);
    }

    /**
     * Reactivar producto (para admin)
     */
    @Transactional
    public ProductoDTO reactivarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setEstaActivo(true);
        producto.setVendido(false);
        producto.setFechaActualizacion(LocalDateTime.now());

        producto = productoRepository.save(producto);

        return convertirADTO(producto);
    }

    // ==================== MÉTODOS CON PAGINACIÓN ====================

    /**
     * Obtener productos activos con paginación
     * Filtra productos reportados y excluye productos del propio usuario
     */
    public Page<ProductoDTO> obtenerProductosActivosPaginados(int page, int size, Long usuarioId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productosPage = productoRepository.findByEstaActivoTrueAndVendidoFalseOrderByFechaCreacionDesc(pageable);
        
        // Si hay usuarioId, filtrar productos reportados y propios
        if (usuarioId != null) {
            List<Long> productosReportados = reporteRepository.findProductoIdsReportadosPorUsuario(usuarioId);
            List<ProductoDTO> productosFiltrados = productosPage.getContent().stream()
                    .filter(p -> !productosReportados.contains(p.getId()))
                    .filter(p -> !p.getUsuario().getId().equals(usuarioId)) // Excluir productos propios
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
            return new org.springframework.data.domain.PageImpl<>(
                    productosFiltrados,
                    pageable,
                    productosPage.getTotalElements()
            );
        }
        
        return productosPage.map(this::convertirADTO);
    }

    /**
     * Buscar productos con paginación
     * Filtra productos reportados y excluye productos del propio usuario
     */
    public Page<ProductoDTO> buscarProductosPaginados(String keyword, int page, int size, Long usuarioId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productosPage = productoRepository.buscarPorPalabraClave(keyword, pageable);
        
        // Si hay usuarioId, filtrar productos reportados y propios
        if (usuarioId != null) {
            List<Long> productosReportados = reporteRepository.findProductoIdsReportadosPorUsuario(usuarioId);
            List<ProductoDTO> productosFiltrados = productosPage.getContent().stream()
                    .filter(p -> !productosReportados.contains(p.getId()))
                    .filter(p -> !p.getUsuario().getId().equals(usuarioId)) // Excluir productos propios
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
            return new org.springframework.data.domain.PageImpl<>(
                    productosFiltrados,
                    pageable,
                    productosPage.getTotalElements()
            );
        }
        
        return productosPage.map(this::convertirADTO);
    }

    /**
     * Obtener productos por categoría con paginación
     * Filtra productos reportados y excluye productos del propio usuario
     */
    public Page<ProductoDTO> obtenerProductosPorCategoriaPaginados(Long categoriaId, int page, int size, Long usuarioId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productosPage = productoRepository.findByCategoriaIdAndEstaActivoTrue(categoriaId, pageable);
        
        // Si hay usuarioId, filtrar productos reportados y propios
        if (usuarioId != null) {
            List<Long> productosReportados = reporteRepository.findProductoIdsReportadosPorUsuario(usuarioId);
            List<ProductoDTO> productosFiltrados = productosPage.getContent().stream()
                    .filter(p -> !productosReportados.contains(p.getId()))
                    .filter(p -> !p.getUsuario().getId().equals(usuarioId)) // Excluir productos propios
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
            return new org.springframework.data.domain.PageImpl<>(
                    productosFiltrados,
                    pageable,
                    productosPage.getTotalElements()
            );
        }
        
        return productosPage.map(this::convertirADTO);
    }

    /**
     * Convertir Entidad a DTO
     */
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();

        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setEstado(producto.getEstado());
        dto.setAtributosExtra(producto.getAtributosExtra());
        dto.setLatitud(producto.getLatitud());
        dto.setLongitud(producto.getLongitud());
        dto.setDireccion(producto.getDireccion());
        dto.setEstaActivo(producto.getEstaActivo());
        dto.setVendido(producto.getVendido());
        dto.setEliminado(producto.getEliminado());
        // dto.setEliminadoPor(producto.getEliminadoPor()); // Comentado hasta que se pueda agregar la columna
        dto.setFechaCreacion(producto.getFechaCreacion());
        dto.setFechaActualizacion(producto.getFechaActualizacion());

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
        List<ImagenProducto> imagenes = imagenProductoRepository.findByProductoIdOrderByOrdenAsc(producto.getId());
        dto.setImagenesUrls(imagenes.stream().map(ImagenProducto::getUrlImagen).collect(Collectors.toList()));

        imagenes.stream()
                .filter(ImagenProducto::getEsPrincipal)
                .findFirst()
                .ifPresent(img -> dto.setImagenPrincipal(img.getUrlImagen()));

        // Cantidad de favoritos
        dto.setCantidadFavoritos(favoritoRepository.countByProductoId(producto.getId()));

        return dto;
    }

    /**
     * Buscar productos cercanos a una ubicación usando fórmula de Haversine
     */
    public List<ProductoDTO> buscarProductosCercanos(Double latitud, Double longitud, Double radioKm, Long categoriaId) {
        System.out.println("=== BÚSQUEDA CERCANA ===");
        System.out.println("Latitud: " + latitud + ", Longitud: " + longitud + ", Radio: " + radioKm + " km");
        System.out.println("Categoría ID: " + categoriaId);
        
        // Obtener todos los productos activos con ubicación
        List<Producto> productos;

        if (categoriaId != null) {
            productos = productoRepository.findByEstaActivoTrueAndVendidoFalseAndCategoriaIdAndLatitudIsNotNullAndLongitudIsNotNull(categoriaId);
        } else {
            productos = productoRepository.findByEstaActivoTrueAndVendidoFalseAndLatitudIsNotNullAndLongitudIsNotNull();
        }

        System.out.println("Productos con ubicación encontrados: " + productos.size());
        
        // Filtrar por distancia
        List<ProductoDTO> resultado = productos.stream()
                .filter(p -> {
                    double distancia = calcularDistancia(latitud, longitud, p.getLatitud(), p.getLongitud());
                    System.out.println("Producto: " + p.getNombre() + " - Distancia: " + distancia + " km");
                    return distancia <= radioKm;
                })
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        
        System.out.println("Productos dentro del radio: " + resultado.size());
        System.out.println("========================");
        
        return resultado;
    }


    /**
     * Calcular distancia entre dos puntos usando fórmula de Haversine
     * @return distancia en kilómetros
     */
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Radio de la Tierra en km
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
