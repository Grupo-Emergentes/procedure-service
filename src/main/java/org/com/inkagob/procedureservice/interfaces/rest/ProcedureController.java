package org.com.inkagob.procedureservice.interfaces.rest;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.com.inkagob.procedureservice.domain.model.commands.CreateProcedureCommand;
import org.com.inkagob.procedureservice.domain.services.ProcedureCommandService;
import org.com.inkagob.procedureservice.domain.services.ProcedureQueryService;
import org.com.inkagob.procedureservice.interfaces.rest.resources.CreateProcedureResource;
import org.com.inkagob.procedureservice.interfaces.rest.resources.ProcedureResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/procedure/procedures", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Procedures", description = "Procedure Management Endpoints")
public class ProcedureController {

    private final ProcedureCommandService procedureCommandService;
    private final ProcedureQueryService procedureQueryService;


    public ProcedureController(ProcedureCommandService procedureCommandService, ProcedureQueryService procedureQueryService) {
        this.procedureCommandService = procedureCommandService;
        this.procedureQueryService = procedureQueryService;
    }

    @PostMapping(value = "/{citizenId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Crear procedimiento",
            description = "Crea un nuevo procedimiento con datos JSON y archivos adjuntos"
    )

    public ResponseEntity<ProcedureResource> createProcedure(
            @PathVariable String citizenId,
            @RequestPart("data") String jsonData,
            @Parameter(description = "Archivos adjuntos", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) throws JsonProcessingException {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            CreateProcedureResource resource = objectMapper.readValue(jsonData, CreateProcedureResource.class);

            CreateProcedureCommand command = new CreateProcedureCommand(
                    resource.procedureType(),
                    resource.procedureStatus(),
                    java.sql.Date.valueOf(resource.endDate()),
                    resource.paymentId(),
                    resource.requiredAmount().floatValue(),
                    resource.officialId(),
                    resource.data() != null ? resource.data().toArray(new String[0]) : new String[0],
                    attachments
            );

            var result = procedureCommandService.handleCreateProcedure(command, citizenId);
            return result.map(pair -> ResponseEntity.status(HttpStatus.CREATED)
                            .body(ProcedureResource.from(pair.getLeft(), pair.getRight())))
                    .orElse(ResponseEntity.badRequest().build());

    }

    @GetMapping("/{procedureId}")
    @Operation(
            summary = "Obtener procedimiento por ID",
            description = "Obtiene un procedimiento por su ID"
    )
    public ResponseEntity<ProcedureResource> getProcedureById(
            @PathVariable long procedureId) {
        return procedureQueryService.findById(procedureId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/citizen/{citizenId}")
    @Operation(
            summary = "Obtener procedimientos por ID de ciudadano",
            description = "Obtiene una lista de procedimientos asociados a un ID de ciudadano"
    )
    public ResponseEntity<List<ProcedureResource>> getProceduresByCitizenId(
            @PathVariable String citizenId) {
        var procedures = procedureQueryService.findByCitizenId(citizenId);
        if (procedures.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(procedures);
    }


    @DeleteMapping("/{procedureId}")
    @Operation(
            summary = "Eliminar procedimiento por ID",
            description = "Elimina un procedimiento por su ID"
    )
    public ResponseEntity<Boolean> deleteProcedureById(
            @PathVariable long procedureId) {
        procedureCommandService.handleDeleteProcedure(procedureId);
        return ResponseEntity.ok(true);
    }

}
