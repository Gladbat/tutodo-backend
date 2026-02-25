package com.tutodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaSeguridadResponse {
    private String pregunta;
    private boolean tienePregunta;
}
