package org.com.inkagob.procedureservice.infrastructure.persistence.nosql.documents;

import lombok.*;
import org.bson.types.ObjectId;
import org.com.inkagob.procedureservice.domain.model.commands.CreateProcedureCommand;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "procedures")
public class ProcedureDocument {

    @Id
    private ObjectId id; // UUID.toString()

    private List<String> data = new ArrayList<>();

    private List<String> attachments = new ArrayList<>();



    public static ProcedureDocument createFromCommand(CreateProcedureCommand command) {
        ProcedureDocument document = new ProcedureDocument();
        document.setData(List.of(command.data()));
        document.setAttachments(new ArrayList<>());
        return document;
    }


}
