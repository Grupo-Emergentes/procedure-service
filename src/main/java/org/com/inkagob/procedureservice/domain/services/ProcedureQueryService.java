package org.com.inkagob.procedureservice.domain.services;

import org.com.inkagob.procedureservice.domain.model.aggregate.Procedure;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureState;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureType;
import org.com.inkagob.procedureservice.interfaces.rest.resources.ProcedureResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProcedureQueryService {


    Optional<ProcedureResource> findById(Long procedureId);

    Optional<Procedure> findByProcedureNoSqlId(UUID procedureNoSqlId);

    List<Procedure> findAll();

    List<ProcedureResource> findByCitizenId(String citizenId);

    List<Procedure> findByProcedureState(ProcedureState state);

    List<Procedure> findByProcedureType(ProcedureType type);

    List<Procedure> findByOfficialId(int officialId);

    List<Procedure> findByCitizenIdAndProcedureState(String citizenId, ProcedureState state);

}
