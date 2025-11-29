package org.com.inkagob.procedureservice.infrastructure.persistence.jpa.repositories;

import org.bson.types.ObjectId;
import org.com.inkagob.procedureservice.domain.model.aggregate.Procedure;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureState;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

    List<Procedure> findByCitizenId(String citizenId);

    Optional<Procedure> findByProcedureNoSqlId(String procedureNoSqlId);

    List<Procedure> findByProcedureState(ProcedureState procedureState);

    List<Procedure> findByProcedureType(ProcedureType procedureType);

    List<Procedure> findByProcedureStateAndCitizenId(ProcedureState procedureState, String citizenId);

    Page<Procedure> findAllByOrderByStartDateDesc(Pageable pageable);


}
