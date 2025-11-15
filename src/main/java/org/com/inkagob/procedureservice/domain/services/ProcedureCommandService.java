package org.com.inkagob.procedureservice.domain.services;

import org.com.inkagob.procedureservice.domain.model.aggregate.Procedure;
import org.com.inkagob.procedureservice.domain.model.commands.CreateProcedureCommand;
import org.com.inkagob.procedureservice.domain.model.commands.UpdateProcedureCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.com.inkagob.procedureservice.infrastructure.persistence.nosql.documents.ProcedureDocument;

import java.util.Optional;

public interface ProcedureCommandService {

    Optional<ImmutablePair<Procedure, ProcedureDocument>> handleCreateProcedure(CreateProcedureCommand command, int citizenId);

    Optional<ImmutablePair<Procedure, ProcedureDocument>> handleUpdateProcedure(UpdateProcedureCommand command, long procedureId);

    void handleDeleteProcedure(long procedureId);

    Optional<Procedure> handleUpdateStatus(long procedureId, String newStatus);

    Optional<Procedure> handlePayProcedure(long procedureId);

    Optional<Procedure> handleAssignOfficial(long officialId, long procedureId);
}
