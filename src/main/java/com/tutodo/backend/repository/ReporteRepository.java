package com.tutodo.backend.repository;

import com.tutodo.backend.entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByProductoId(Long productoId);

    Long countByProductoId(Long productoId);

    boolean existsByUsuarioReportadorIdAndProductoId(Long usuarioId, Long productoId);

    List<Reporte> findByUsuarioReportadorIdOrderByFechaCreacionDesc(Long usuarioId);

    /**
     * Obtener IDs de productos reportados por un usuario
     */
    @Query("SELECT r.producto.id FROM Reporte r WHERE r.usuarioReportador.id = :usuarioId")
    List<Long> findProductoIdsReportadosPorUsuario(@Param("usuarioId") Long usuarioId);

}
