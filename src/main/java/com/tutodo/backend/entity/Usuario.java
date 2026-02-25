package com.tutodo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tutodo.backend.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    private String nombre;

    private String apellido;

    @Column(name = "numero_whatsapp", unique = true)
    private String numeroWhatsapp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.USER;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean suspendido = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean eliminado = false;

    @Column(name = "pregunta_seguridad")
    private String preguntaSeguridad;

    @Column(name = "respuesta_seguridad")
    private String respuestaSeguridad;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    // Relaciones
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Producto> productos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Favorito> favoritos = new ArrayList<>();
}