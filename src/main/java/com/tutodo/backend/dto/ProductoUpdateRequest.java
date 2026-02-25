package com.tutodo.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateRequest {

    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nombre;

    @Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
    private String descripcion;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Pattern(regexp = "nuevo|usado", message = "El estado debe ser 'nuevo' o 'usado'")
    private String estado;

    private String atributosExtra;

    private Double latitud;
    private Double longitud;
    private String direccion;

    private Long categoriaId;

    private Boolean estaActivo;
    private Boolean vendido;
}