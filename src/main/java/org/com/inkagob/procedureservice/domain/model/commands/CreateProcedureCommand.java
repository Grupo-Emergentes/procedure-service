package org.com.inkagob.procedureservice.domain.model.commands;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;


public record CreateProcedureCommand(
        String procedureType,
        String procedureStatus,
        Date endDate,
        int paymentId,
        float requiredAmount,
        int officialId,
        String[] data,
        List<MultipartFile> attachments
) {



}
