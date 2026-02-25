package com.tutodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long usuarioReportadorId;
    private String usuarioReportadorNombre;
    private String razon;
    private String comentario;
    private LocalDateTime fechaCreacion;
}