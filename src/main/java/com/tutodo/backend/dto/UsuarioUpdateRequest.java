package com.tutodo.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;

    @Pattern(regexp = "^\\+51[0-9]{9}$", message = "El número debe tener formato +51XXXXXXXXX")
    private String numeroWhatsapp;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @Size(max = 255, message = "La pregunta de seguridad no puede exceder 255 caracteres")
    private String preguntaSeguridad;

    @Size(max = 255, message = "La respuesta de seguridad no puede exceder 255 caracteres")
    private String respuestaSeguridad;
}