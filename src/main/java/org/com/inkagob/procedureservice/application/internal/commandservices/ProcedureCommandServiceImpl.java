package org.com.inkagob.procedureservice.application.internal.commandservices;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bson.types.ObjectId;
import org.com.inkagob.procedureservice.domain.model.aggregate.Procedure;
import org.com.inkagob.procedureservice.domain.model.commands.CreateProcedureCommand;
import org.com.inkagob.procedureservice.domain.model.commands.UpdateProcedureCommand;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureState;
import org.com.inkagob.procedureservice.domain.services.ProcedureCommandService;
import org.com.inkagob.procedureservice.infrastructure.files.cloudinary.storage.service.CloudinaryFileStorageService;
import org.com.inkagob.procedureservice.infrastructure.persistence.jpa.repositories.ProcedureRepository;
import org.com.inkagob.procedureservice.infrastructure.persistence.nosql.documents.ProcedureDocument;
import org.com.inkagob.procedureservice.infrastructure.persistence.nosql.repositories.ProcedureNoSqlRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProcedureCommandServiceImpl implements ProcedureCommandService {

    private final ProcedureRepository procedureRepository;
    private final ProcedureNoSqlRepository procedureNoSqlRepository;
    private final CloudinaryFileStorageService fileStorageService;

    public ProcedureCommandServiceImpl(ProcedureRepository procedureRepository, ProcedureNoSqlRepository procedureNoSqlRepository, CloudinaryFileStorageService fileStorageService) {
        this.procedureRepository = procedureRepository;
        this.procedureNoSqlRepository = procedureNoSqlRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Optional<ImmutablePair<Procedure, ProcedureDocument>> handleCreateProcedure(CreateProcedureCommand command, String citizenId) {
        try {
            var procedure = new Procedure().ConstructProcedureFromCommand(command, citizenId);
            var savedProcedure = procedureRepository.save(procedure);

            var procedureDocument = ProcedureDocument.createFromCommand(command);
            var savedProcedureDocument = procedureNoSqlRepository.save(procedureDocument);

            List<String> attachmentUrls = new ArrayList<>();
            if (command.attachments() != null && !command.attachments().isEmpty()) {
                for (MultipartFile file : command.attachments()) {
                    ObjectId procedureId = savedProcedureDocument.getId();
                    String url = fileStorageService.uploadFile(file, procedureId);
                    attachmentUrls.add(url);
                }
            }

            savedProcedureDocument.setAttachments(attachmentUrls);
            procedureNoSqlRepository.save(savedProcedureDocument);

            savedProcedure.setProcedureNoSqlId(savedProcedureDocument.getId().toHexString());
            procedureRepository.save(savedProcedure);

            return Optional.of(ImmutablePair.of(savedProcedure, savedProcedureDocument));

        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivos a Cloudinary: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear procedimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ImmutablePair<Procedure, ProcedureDocument>> handleUpdateProcedure(UpdateProcedureCommand command, long procedureId) {
        try {
            var existingProcedureOpt = procedureRepository.findById(procedureId);

            if (existingProcedureOpt.isEmpty()) {
                throw new RuntimeException("No se encontró el procedimiento: " + procedureId);
            }

            var existingProcedure = existingProcedureOpt.get();

            var updatedProcedure = new Procedure().UpdateProcedureFromCommand(existingProcedure, command);
            var savedProcedure = procedureRepository.save(updatedProcedure);

            String procedureNoSqlId = savedProcedure.getProcedureNoSqlId();
            if (procedureNoSqlId != null) {
                var procedureDocumentOpt = procedureNoSqlRepository.findById(procedureNoSqlId);

                if (procedureDocumentOpt.isPresent()) {
                    var procedureDocument = procedureDocumentOpt.get();
                    procedureDocument.setData(List.of(command.data()));
                    var savedDocument = procedureNoSqlRepository.save(procedureDocument);
                    return Optional.of(ImmutablePair.of(savedProcedure, savedDocument));
                }
            }

            return Optional.of(ImmutablePair.of(savedProcedure, null));

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar procedimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public void handleDeleteProcedure(long procedureId) {
        try {
            var procedureOpt = procedureRepository.findById(procedureId);

            if (procedureOpt.isEmpty()) {
                throw new RuntimeException("No se encontró el procedimiento: " + procedureId);
            }

            var procedure = procedureOpt.get();
            String procedureNoSqlId = procedure.getProcedureNoSqlId();
            if (procedureNoSqlId != null) {
                var procedureDocumentOpt = procedureNoSqlRepository.findById(procedureNoSqlId);

                if (procedureDocumentOpt.isPresent()) {
                    var procedureDocument = procedureDocumentOpt.get();

                    // Eliminar archivos de Cloudinary
                    if (procedureDocument.getAttachments() != null && !procedureDocument.getAttachments().isEmpty()) {
                        for (String fileUrl : procedureDocument.getAttachments()) {
                            try {
                                fileStorageService.deleteFile(fileUrl);
                            } catch (Exception e) {
                                System.err.println("Error al eliminar archivo: " + fileUrl + " - " + e.getMessage());
                            }
                        }
                    }

                    procedureNoSqlRepository.deleteById(procedureNoSqlId);
                }
            }
           procedureRepository.deleteById(procedureId);

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar procedimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Procedure> handleUpdateStatus(long procedureId, String newStatus) {
        try {
            var procedureOpt = procedureRepository.findById(procedureId);

            if (procedureOpt.isEmpty()) {
                throw new RuntimeException("No se encontró el procedimiento: " + procedureId);
            }

            var procedure = procedureOpt.get();
            var procedureState = ProcedureState.valueOf(newStatus.toUpperCase(java.util.Locale.ROOT));
            procedure.setProcedureState(procedureState);
            var savedProcedure = procedureRepository.save(procedure);
            return Optional.of(savedProcedure);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar estado: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Procedure> handlePayProcedure(long procedureId) {
        return Optional.empty();
    }

    @Override
    public Optional<Procedure> handleAssignOfficial(long officialId, long procedureId) {
        try {
            var procedureOpt = procedureRepository.findById(procedureId);

            if (procedureOpt.isEmpty()) {
                throw new RuntimeException("No se encontró el procedimiento: " + procedureId);
            }

            var procedure = procedureOpt.get();
            procedure.setOfficialId((int) officialId);
            var savedProcedure = procedureRepository.save(procedure);

            return Optional.of(savedProcedure);

        } catch (Exception e) {
            throw new RuntimeException("Error al asignar funcionario: " + e.getMessage(), e);
        }
    }
}
