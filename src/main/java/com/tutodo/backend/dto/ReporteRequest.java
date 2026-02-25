package com.tutodo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequest {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioReportadorId;

    @NotBlank(message = "La razón es obligatoria")
    @Size(max = 100, message = "La razón no puede tener más de 100 caracteres")
    private String razon;

    @Size(max = 500, message = "El comentario no puede tener más de 500 caracteres")
    private String comentario;
}