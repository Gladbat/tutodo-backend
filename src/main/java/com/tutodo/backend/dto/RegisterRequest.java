package com.tutodo.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;

    @NotBlank(message = "El número de WhatsApp es obligatorio")
    @Pattern(regexp = "^\\+51[0-9]{9}$", message = "El número debe tener formato +51XXXXXXXXX")
    private String numeroWhatsapp;

    @NotBlank(message = "La pregunta de seguridad es obligatoria")
    private String preguntaSeguridad;

    @NotBlank(message = "La respuesta de seguridad es obligatoria")
    private String respuestaSeguridad;
}