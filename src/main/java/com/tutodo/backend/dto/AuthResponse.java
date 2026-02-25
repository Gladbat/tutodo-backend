package com.tutodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.tutodo.backend.enums.Rol;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long usuarioId;
    private Rol rol;
    private String email;
    private String nombre;
    private String apellido;
    private String numeroWhatsapp;
    private String mensaje;

}