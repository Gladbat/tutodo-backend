package com.tutodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String estado; // 'nuevo' o 'usado'
    private String atributosExtra; // JSON con campos dinámicos

    // Ubicación
    private Double latitud;
    private Double longitud;
    private String direccion;

    // Relaciones (IDs y nombres)
    private Long categoriaId;
    private String categoriaNombre;

    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioWhatsapp;

    // Imágenes
    private List<String> imagenesUrls;
    private String imagenPrincipal;

    // Estados
    private Boolean estaActivo;
    private Boolean vendido;
    private Boolean eliminado;
    private String eliminadoPor; // "USUARIO" o "ADMIN"

    // Timestamps
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Extras
    private Long cantidadFavoritos;
}