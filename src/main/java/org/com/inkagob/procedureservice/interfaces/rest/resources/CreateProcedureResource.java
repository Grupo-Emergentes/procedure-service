package org.com.inkagob.procedureservice.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record CreateProcedureResource(
        @NotBlank(message = "El tipo de procedimiento es obligatorio")
        String procedureType,

        @NotBlank(message = "El estado es obligatorio")
        String procedureStatus,

        @Future(message = "La fecha de fin debe ser futura")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @Positive(message = "El ID de pago debe ser positivo")
        int paymentId,

        @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
        BigDecimal requiredAmount,

        @Positive(message = "El ID del oficial debe ser positivo")
        int officialId,

        @NotEmpty(message = "Debe proporcionar al menos un dato")
        List<String> data  // Cambiado de String[] a List<String>
        // attachments se recibe como @RequestPart separado en el Controller
) {}