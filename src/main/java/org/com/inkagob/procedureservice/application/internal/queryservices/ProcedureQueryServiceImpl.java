package org.com.inkagob.procedureservice.application.internal.queryservices;

import org.com.inkagob.procedureservice.domain.model.aggregate.Procedure;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureState;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureType;
import org.com.inkagob.procedureservice.domain.services.ProcedureQueryService;
import org.com.inkagob.procedureservice.infrastructure.files.cloudinary.storage.service.CloudinaryFileStorageService;
import org.com.inkagob.procedureservice.infrastructure.persistence.jpa.repositories.ProcedureRepository;
import org.com.inkagob.procedureservice.infrastructure.persistence.nosql.repositories.ProcedureNoSqlRepository;
import org.com.inkagob.procedureservice.interfaces.rest.resources.ProcedureResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class ProcedureQueryServiceImpl implements ProcedureQueryService {
    private final ProcedureRepository procedureRepository;
    private final ProcedureNoSqlRepository procedureNoSqlRepository;

    public ProcedureQueryServiceImpl(ProcedureRepository procedureRepository, ProcedureNoSqlRepository procedureNoSqlRepository) {
        this.procedureRepository = procedureRepository;
        this.procedureNoSqlRepository = procedureNoSqlRepository;
    }

    @Override
    public Optional<ProcedureResource> findById(Long procedureId) {
        var procedure = this.procedureRepository.findById(procedureId);

        if (procedure.isEmpty()) {
            return Optional.empty();
        }

        var procedureDocument = this.procedureNoSqlRepository.findById(procedure.get().getProcedureNoSqlId());

        if (procedureDocument.isEmpty()) {
            return Optional.of(ProcedureResource.from(procedure.get(), null));
        }

        return Optional.of(ProcedureResource.from(procedure.get(), procedureDocument.get()));
    }

    @Override
    public Optional<Procedure> findByProcedureNoSqlId(UUID procedureNoSqlId) {
        return Optional.empty();
    }

    @Override
    public List<Procedure> findAll() {
        return List.of();
    }

    @Override
    public List<Procedure> findByCitizenId(int citizenId) {
        return List.of();
    }

    @Override
    public List<Procedure> findByProcedureState(ProcedureState state) {
        return List.of();
    }

    @Override
    public List<Procedure> findByProcedureType(ProcedureType type) {
        return List.of();
    }

    @Override
    public List<Procedure> findByOfficialId(int officialId) {
        return List.of();
    }

    @Override
    public List<Procedure> findByCitizenIdAndProcedureState(int citizenId, ProcedureState state) {
        return List.of();
    }
}
