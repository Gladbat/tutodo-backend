package com.tutodo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "atributos_extra", columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String atributosExtra;

    // Ubicación
    @Column
    private Double latitud;

    @Column
    private Double longitud;

    @Column(length = 500)
    private String direccion;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ← AGREGAR ESTA LÍNEA
    private List<ImagenProducto> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @JsonIgnore  // ← AGREGAR ESTA LÍNEA
    private List<Favorito> favoritos = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @JsonIgnore  // ← AGREGAR ESTA LÍNEA
    private List<Reporte> reportes = new ArrayList<>();

    // Estados
    @Column(name = "esta_activo", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean estaActivo = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean vendido = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean eliminado = false;

    // @Column(name = "eliminado_por", length = 20)
    // private String eliminadoPor; // "USUARIO" o "ADMIN" - Comentado hasta resolver timeout en Supabase

    // Timestamps
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}