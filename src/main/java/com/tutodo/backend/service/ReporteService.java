package com.tutodo.backend.service;

import com.tutodo.backend.dto.ReporteDTO;
import com.tutodo.backend.dto.ReporteRequest;
import com.tutodo.backend.entity.Producto;
import com.tutodo.backend.entity.Reporte;
import com.tutodo.backend.entity.Usuario;
import com.tutodo.backend.repository.ProductoRepository;
import com.tutodo.backend.repository.ReporteRepository;
import com.tutodo.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    private static final int LIMITE_REPORTES_AUTO_OCULTAR = 5;

    /**
     * Obtener reportes de un producto
     */
    public List<ReporteDTO> obtenerReportesPorProducto(Long productoId) {
        return reporteRepository.findByProductoId(productoId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }




    /**
     * Contar reportes de un producto
     */
    public Long contarReportesPorProducto(Long productoId) {
        return reporteRepository.countByProductoId(productoId);
    }

    /**
     * Verificar si un usuario ya reportó un producto
     */
    public boolean usuarioYaReporto(Long productoId, Long usuarioId) {
        return reporteRepository.existsByUsuarioReportadorIdAndProductoId(usuarioId, productoId);
    }

    /**
     * Crear un reporte
     */
    @Transactional
    public ReporteDTO crearReporte(ReporteRequest request) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.getUsuarioReportadorId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el producto existe
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar que el usuario no haya reportado ya este producto
        if (reporteRepository.existsByUsuarioReportadorIdAndProductoId(
                request.getUsuarioReportadorId(),
                request.getProductoId())) {
            throw new RuntimeException("Ya has reportado este producto anteriormente");
        }

        // Crear el reporte
        Reporte reporte = new Reporte();
        reporte.setProducto(producto);
        reporte.setUsuarioReportador(usuario);
        reporte.setRazon(request.getRazon());
        reporte.setComentario(request.getComentario());
        reporte.setFechaCreacion(LocalDateTime.now());

        reporte = reporteRepository.save(reporte);

        // Verificar si se debe auto-ocultar el producto (5+ reportes)
        verificarYAutoOcultarProducto(producto.getId());

        return convertirADTO(reporte);
    }

    /**
     * Auto-ocultar producto si tiene muchos reportes
     */
    @Transactional
    public void verificarYAutoOcultarProducto(Long productoId) {
        Long cantidadReportes = reporteRepository.countByProductoId(productoId);

        if (cantidadReportes >= LIMITE_REPORTES_AUTO_OCULTAR) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            producto.setEstaActivo(false);
            producto.setFechaActualizacion(LocalDateTime.now());

            productoRepository.save(producto);
        }
    }

    /**
     * Eliminar un reporte (admin)
     */
    @Transactional
    public void eliminarReporte(Long reporteId) {
        if (!reporteRepository.existsById(reporteId)) {
            throw new RuntimeException("Reporte no encontrado");
        }

        reporteRepository.deleteById(reporteId);
    }

    /**
     * Convertir Entidad a DTO
     */
    private ReporteDTO convertirADTO(Reporte reporte) {
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
    }

    public List<ReporteDTO> obtenerReportesPorUsuario(Long usuarioId) {
        return reporteRepository.findByUsuarioReportadorIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
}

