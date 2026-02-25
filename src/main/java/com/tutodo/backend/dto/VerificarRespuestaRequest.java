package com.tutodo.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerificarRespuestaRequest {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "La respuesta es obligatoria")
    private String respuesta;
}
