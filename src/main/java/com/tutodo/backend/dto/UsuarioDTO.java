package com.tutodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.tutodo.backend.enums.Rol;  // ← AGREGAR
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String numeroWhatsapp;
    private Rol rol;
    private LocalDateTime fechaCreacion;
    private Integer cantidadPublicaciones;
    private Integer cantidadFavoritos;
    private Boolean suspendido;
    private Boolean eliminado;
}