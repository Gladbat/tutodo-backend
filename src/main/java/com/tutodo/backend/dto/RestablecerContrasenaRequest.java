package com.tutodo.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestablecerContrasenaRequest {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String nuevaContrasena;
}
