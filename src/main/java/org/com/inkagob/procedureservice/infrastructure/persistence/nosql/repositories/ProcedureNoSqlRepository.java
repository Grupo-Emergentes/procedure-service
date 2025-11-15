package org.com.inkagob.procedureservice.infrastructure.persistence.nosql.repositories;

import org.com.inkagob.procedureservice.infrastructure.persistence.nosql.documents.ProcedureDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcedureNoSqlRepository extends MongoRepository<ProcedureDocument, String> {

    List<ProcedureDocument> findByDataContaining(String element);

    List<ProcedureDocument> findByAttachmentsContaining(String element);

    Page<ProcedureDocument> findByDataContaining(String element, Pageable pageable);

    List<ProcedureDocument> findByIdIn(List<String> ids);

    @Query("{ 'data': ?0 }")
    List<ProcedureDocument> findByDataElement(String element);

}
