package org.com.inkagob.procedureservice;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bson.types.ObjectId;
import org.com.inkagob.procedureservice.application.internal.commandservices.ProcedureCommandServiceImpl;
import org.com.inkagob.procedureservice.domain.model.aggregate.Procedure;
import org.com.inkagob.procedureservice.domain.model.commands.CreateProcedureCommand;
import org.com.inkagob.procedureservice.domain.model.commands.UpdateProcedureCommand;
import org.com.inkagob.procedureservice.infrastructure.files.cloudinary.storage.service.CloudinaryFileStorageService;
import org.com.inkagob.procedureservice.infrastructure.persistence.jpa.repositories.ProcedureRepository;
import org.com.inkagob.procedureservice.infrastructure.persistence.nosql.documents.ProcedureDocument;
import org.com.inkagob.procedureservice.infrastructure.persistence.nosql.repositories.ProcedureNoSqlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class ProcedureServiceApplicationTests {

    @Autowired
    private ProcedureCommandServiceImpl service;

    @Autowired
    private ProcedureRepository procedureRepository;

    @Autowired
    private ProcedureNoSqlRepository procedureNoSqlRepository;

    @MockBean
    private CloudinaryFileStorageService fileStorageService;

    @BeforeEach
    void setup() {
        procedureRepository.deleteAll();
        procedureNoSqlRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    @Test
    void testHandleCreateProcedure_withAttachments() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.png", "image/png", "123".getBytes()
        );

        CreateProcedureCommand command = new CreateProcedureCommand(
                "Waiting",
                "Descripcion Test",
                new Date(),
                1,
                200,
                1,
                List.of("dato1", "dato2").toArray(new String[0]),
                List.of(mockFile)
        );

        when(fileStorageService.uploadFile(any(), any(ObjectId.class)))
                .thenReturn("https://cloudinary.com/fake-url");

        Optional<ImmutablePair<Procedure, ProcedureDocument>> result =
                service.handleCreateProcedure(command, 99);

        assertTrue(result.isPresent());
        Procedure procedure = result.get().left;
        ProcedureDocument document = result.get().right;

        assertNotNull(procedure.getId());
        assertEquals("99", String.valueOf(procedure.getCitizenId()));
        assertNotNull(document.getId());
        assertEquals(1, document.getAttachments().size());

        verify(fileStorageService, times(1))
                .uploadFile(any(), any(ObjectId.class));
    }

    // -------------------------------------------------------------------------
    @Test
    void testHandleUpdateProcedure_updatesDataAndDocument() {
        // Preparar un procedimiento existente
        Procedure procedure = new Procedure();
        procedure.setCitizenId(10);

        Procedure saved = procedureRepository.save(procedure);

        ProcedureDocument document = new ProcedureDocument();
        document.setId(new ObjectId());
        document.setData(List.of("old data"));

        ProcedureDocument savedDoc = procedureNoSqlRepository.save(document);

        // vincular ambos
        saved.setProcedureNoSqlId(savedDoc.getId().toHexString());
        procedureRepository.save(saved);

        UpdateProcedureCommand command = new UpdateProcedureCommand(
                "Nuevo Título",
                "Nueva descripción",
                new Date(),
                200,
                List.of("dato1", "dato2").toArray(new String[0])
        );

        Optional<ImmutablePair<Procedure, ProcedureDocument>> result =
                service.handleUpdateProcedure(command, saved.getId());

        assertTrue(result.isPresent());

        Procedure updated = result.get().left;
        ProcedureDocument updatedDoc = result.get().right;

        assertEquals(List.of("nuevo dato"), updatedDoc.getData());
    }

    // -------------------------------------------------------------------------
    @Test
    void testHandleDeleteProcedure_removesFilesAndDocuments() throws Exception {
        Procedure procedure = new Procedure();
        procedure.setCitizenId(1);
        Procedure saved = procedureRepository.save(procedure);

        ProcedureDocument document = new ProcedureDocument();
        document.setId(new ObjectId());
        document.setAttachments(List.of("cloudinary/url1", "cloudinary/url2"));

        ProcedureDocument savedDoc = procedureNoSqlRepository.save(document);

        saved.setProcedureNoSqlId(savedDoc.getId().toHexString());
        procedureRepository.save(saved);

        // Mock delete actions
        doNothing().when(fileStorageService).deleteFile(anyString());

        service.handleDeleteProcedure(saved.getId());

        assertFalse(procedureRepository.findById(saved.getId()).isPresent());
        assertFalse(procedureNoSqlRepository.findById(savedDoc.getId().toHexString()).isPresent());

        verify(fileStorageService, times(2))
                .deleteFile(anyString());
    }

    // -------------------------------------------------------------------------
    @Test
    void testHandleUpdateStatus() {
        Procedure procedure = new Procedure();
        procedure.setCitizenId(5);

        Procedure saved = procedureRepository.save(procedure);

        Optional<Procedure> updated =
                service.handleUpdateStatus(saved.getId(), "APPROVED");

        assertTrue(updated.isPresent());
        assertEquals(updated.get(), saved);
    }

    // -------------------------------------------------------------------------
    @Test
    void testHandleAssignOfficial() {
        Procedure procedure = new Procedure();
        procedure.setCitizenId(5);

        Procedure saved = procedureRepository.save(procedure);

        Optional<Procedure> result =
                service.handleAssignOfficial(77, saved.getId());

        assertTrue(result.isPresent());
        assertEquals(77, result.get().getOfficialId());
    }

}
