package org.com.inkagob.procedureservice.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.com.inkagob.procedureservice.domain.model.aggregate.Procedure;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureState;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureType;
import org.com.inkagob.procedureservice.infrastructure.persistence.nosql.documents.ProcedureDocument;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProcedureResource(
        Long id,
        int citizenId,
        ProcedureType procedureType,
        ProcedureState procedureState,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startDate,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endDate,

        int paymentId,
        BigDecimal requiredAmount,
        int officialId,

        String procedureNoSqlId,

        List<String> data,
        List<String> attachments
) {
    public static ProcedureResource from(
            Procedure procedure,
            ProcedureDocument document) {

        return new ProcedureResource(
                procedure.getId(),
                procedure.getCitizenId(),
                procedure.getProcedureType(),
                procedure.getProcedureState(),
                procedure.getStartDate() != null ?
                        new java.sql.Timestamp(procedure.getStartDate().getTime()).toLocalDateTime() : null,
                procedure.getEndDate() != null ?
                        new java.sql.Timestamp(procedure.getEndDate().getTime()).toLocalDateTime() : null,
                procedure.getPaymentId(),
                BigDecimal.valueOf(procedure.getRequiredAmount()),
                procedure.getOfficialId(),
                procedure.getProcedureNoSqlId(),
                document != null ? document.getData() : List.of(),
                document != null ? document.getAttachments() : List.of()
        );
    }
}