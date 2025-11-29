package org.com.inkagob.procedureservice.domain.model.aggregate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.com.inkagob.procedureservice.domain.model.commands.CreateProcedureCommand;
import org.com.inkagob.procedureservice.domain.model.commands.UpdateProcedureCommand;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureState;
import org.com.inkagob.procedureservice.domain.model.valueobjects.ProcedureType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "procedures")
@EntityListeners(AuditingEntityListener.class)
public class Procedure {

    @Id
    @GeneratedValue(strategy  = GenerationType.IDENTITY)
    private Long id;

    private String citizenId;

    @Enumerated(EnumType.STRING)
    private ProcedureType procedureType;

    @Enumerated(EnumType.STRING)
    private ProcedureState procedureState;

    private Date startDate;

    private Date endDate;

    @Column(length = 24)
    private String procedureNoSqlId;

    private int paymentId;

    private float requiredAmount;

    private int officialId;


    public Procedure ConstructProcedureFromCommand(CreateProcedureCommand command, String citizenId) {
        Procedure procedure = new Procedure();
        procedure.setCitizenId(citizenId);
        procedure.setProcedureType(ProcedureType.valueOf(command.procedureType().toUpperCase(java.util.Locale.ROOT)));
        procedure.setProcedureState(ProcedureState.valueOf(command.procedureStatus().toUpperCase(java.util.Locale.ROOT)));
        procedure.setStartDate(new Date());
        procedure.setEndDate(command.endDate());
        procedure.setPaymentId(command.paymentId());
        procedure.setRequiredAmount(command.requiredAmount());
        procedure.setOfficialId(command.officialId());
        return procedure;
    }

    public Procedure SetProcedureNoSqlId(Procedure procedure, ObjectId procedureNoSqlId) {
        if (procedure != null && procedureNoSqlId != null) {
            procedure.setProcedureNoSqlId(procedureNoSqlId.toHexString());
        }
        return procedure;
    }

    public Procedure UpdateProcedureFromCommand(Procedure procedure, UpdateProcedureCommand command) {
        if (command == null || procedure == null) {
            return procedure;
        }

        String pt = command.procedureType();
        if (pt != null && !pt.isBlank()) {
            procedure.procedureType = ProcedureType.valueOf(pt.toUpperCase(java.util.Locale.ROOT));
        }

        String ps = command.procedureStatus();
        if (ps != null && !ps.isBlank()) {
            procedure.procedureState = ProcedureState.valueOf(ps.toUpperCase(java.util.Locale.ROOT));
        }

        if (command.endDate() != null) {
            procedure.endDate = command.endDate();
        }

        procedure.requiredAmount = command.requiredAmount();

        return procedure;
    }
    public ObjectId getProcedureNoSqlObjectId() {
        if (this.procedureNoSqlId != null && !this.procedureNoSqlId.isBlank()) {
            return new ObjectId(this.procedureNoSqlId);
        }
        return null;
    }

    public void updateProcedureState(ProcedureState newState) {
        this.procedureState = newState;
    }

}
