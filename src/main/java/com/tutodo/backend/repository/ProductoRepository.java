package com.tutodo.backend.repository;

import com.tutodo.backend.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // PAGINACIÓN
    Page<Producto> findByEstaActivoTrueAndVendidoFalseOrderByFechaCreacionDesc(Pageable pageable);

    Page<Producto> findByCategoriaIdAndEstaActivoTrue(Long categoriaId, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.estaActivo = true " +
            "AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Producto> buscarPorPalabraClave(@Param("keyword") String keyword, Pageable pageable);

    // SIN PAGINACIÓN (mantener para compatibilidad)
    List<Producto> findByEstaActivoTrueOrderByFechaCreacionDesc();

    List<Producto> findByCategoriaIdAndEstaActivoTrue(Long categoriaId);

    @Query("SELECT p FROM Producto p WHERE p.estaActivo = true " +
            "AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Producto> buscarPorPalabraClave(@Param("keyword") String keyword);

    List<Producto> findByUsuarioId(Long usuarioId);

    List<Producto> findByUsuarioIdAndEstaActivoTrue(Long usuarioId);

    List<Producto> findByEstaActivoTrueAndVendidoFalseOrderByFechaCreacionDesc();

    List<Producto> findByEstadoAndEstaActivoTrue(String estado);

    List<Producto> findByCategoriaIdAndEstadoAndEstaActivoTrue(Long categoriaId, String estado);

    // Agregar estos métodos para estadísticas Dashboard
    long countByEstaActivoTrue();

    long countByVendidoTrue();
    
    // Contar publicaciones por usuario
    long countByUsuarioId(Long usuarioId);

    // Búsqueda cercana - productos con ubicación
    List<Producto> findByEstaActivoTrueAndVendidoFalseAndLatitudIsNotNullAndLongitudIsNotNull();
    
    List<Producto> findByEstaActivoTrueAndVendidoFalseAndCategoriaIdAndLatitudIsNotNullAndLongitudIsNotNull(Long categoriaId);

}
