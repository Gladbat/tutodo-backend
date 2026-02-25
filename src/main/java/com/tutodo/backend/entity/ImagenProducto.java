package com.tutodo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "imagenes_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnore  // ← AGREGAR ESTA LÍNEA
    private Producto producto;

    @Column(name = "url_imagen", nullable = false, length = 500)
    private String urlImagen;

    @Column(name = "es_principal", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean esPrincipal = false;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer orden = 0;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}