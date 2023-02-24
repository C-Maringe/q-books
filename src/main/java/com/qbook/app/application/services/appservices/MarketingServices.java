package com.qbook.app.application.services.appservices;


import com.qbook.app.application.models.marketingModels.*;

import java.util.List;

public interface MarketingServices {

    BatchEmailClientsModel getAllClientsToBatchEmailTo();

    BatchEmailClientSetupModel setupBatchEmailList(BatchEmailClientModel batchEmailClientModel);

    BatchEmailContentSetupModel setupBatchEmailContent(BatchEmailContentModel batchEmailContentModel);

    void sendBatchEmail(String batchEmailId);

    List<BatchEmailModel> viewBatchEmails();

    BatchEmailClientsModel viewSpecificBatchEmail(String batchEmailId);
}
