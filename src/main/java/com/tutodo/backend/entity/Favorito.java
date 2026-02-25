package com.tutodo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "producto_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore  // ← AGREGAR ESTA LÍNEA
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnore  // ← AGREGAR ESTA LÍNEA
    private Producto producto;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}