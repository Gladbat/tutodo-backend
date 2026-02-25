package com.tutodo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnore  // ← AGREGAR ESTA LÍNEA
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_reportador_id", nullable = false)
    @JsonIgnore  // ← AGREGAR ESTA LÍNEA
    private Usuario usuarioReportador;

    @Column(nullable = false, length = 100)
    private String razon;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
