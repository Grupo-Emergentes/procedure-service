package org.com.inkagob.procedureservice.domain.model.commands;

import java.util.Date;

public record UpdateProcedureCommand(String procedureType, String procedureStatus, Date endDate, float requiredAmount, String[] data) {

}
