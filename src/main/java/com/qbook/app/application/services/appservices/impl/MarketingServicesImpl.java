package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.marketingModels.*;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.application.services.appservices.MarketingServices;
import com.qbook.app.domain.models.BatchEmail;
import com.qbook.app.domain.models.BatchEmailMetaInfo;
import com.qbook.app.domain.models.BatchEmailStatus;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.repository.BatchEmailMetaInfoRepository;
import com.qbook.app.domain.repository.BatchEmailRepository;
import com.qbook.app.domain.repository.ClientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class MarketingServicesImpl implements MarketingServices {
    private final ClientRepository clientRepository;
    private final BatchEmailRepository batchEmailRepository;
    private final BatchEmailMetaInfoRepository batchEmailMetaInfoRepository;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;
    @Override
    public BatchEmailClientsModel getAllClientsToBatchEmailTo() {

        List<com.qbook.app.application.models.marketingModels.Client> clients = clientRepository
                .findAllByReceiveMarketingEmails(true)
                .stream()
                .map(client -> {
                    try {
                        com.qbook.app.application.models.marketingModels.Client client1 = new com.qbook.app.application.models.marketingModels.Client();
                        client1.setEmailAddress(client.getContactDetails().getEmailAddress());
                        client1.setExistsInList(false);
                        client1.setFullName(client.getFirstName() + " " + client.getLastName());
                        client1.setUserId(client.getId().toString());

                        return client1;
                    } catch (Exception e) {
                        log.severe("Unable to extract client profile. " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        BatchEmailClientsModel batchEmailClientsModel = new BatchEmailClientsModel();
        batchEmailClientsModel.setResending(false);
        batchEmailClientsModel.setClients(clients);

        return batchEmailClientsModel;
    }

    @Override
    public BatchEmailClientSetupModel setupBatchEmailList(BatchEmailClientModel batchEmailClientModel) {
        List<Client> clientList = new ArrayList<>();

        BatchEmailMetaInfo batchEmailMetaInfo = new BatchEmailMetaInfo();
        batchEmailMetaInfo.setId(new ObjectId());
        batchEmailMetaInfo.setTotalToBeSentTo(batchEmailClientModel.getClientIds().length);

        batchEmailMetaInfoRepository.save(batchEmailMetaInfo);

        for(int i = 0; i < batchEmailClientModel.getClientIds().length; i++) {
            Optional<Client> toSendTo = clientRepository.findById(new ObjectId(batchEmailClientModel.getClientIds()[i]));
            if(toSendTo.isPresent()) {
                clientList.add(toSendTo.get());
            } else {
                log.log(Level.WARNING, "Error sending user promotional email, there account was not found.");
            }
        }

        BatchEmail batchEmail;
        if(batchEmailClientModel.getBatchEmailId() != null) {
            Optional<BatchEmail> batchEmailOptional = batchEmailRepository.findById(new ObjectId(batchEmailClientModel.getBatchEmailId()));

            if(!batchEmailOptional.isPresent()) {
                throw new ResourceNotFoundException("The promotional campaign was not found.");
            }
            batchEmail = batchEmailOptional.get();

        } else {
            batchEmail = new BatchEmail();
        }
        batchEmail.setBatchEmailStatus(BatchEmailStatus.SETUP);
        batchEmail.setToEmail(clientList);

        Optional<BatchEmailMetaInfo> batchEmailMetaInfoOptional = batchEmailMetaInfoRepository.findById(new ObjectId(batchEmailMetaInfo.getId().toString()));

        batchEmailMetaInfoOptional.ifPresent(batchEmail::setBatchEmailMetaInfo);
        BatchEmail created = batchEmailRepository.save(batchEmail);

        if(!batchEmailClientModel.getBatchEmailId().equals("")) {
            return new BatchEmailClientSetupModel(
                    created.getId().toString(),
                    (batchEmail.getBatchEmailTitle()!=null && !batchEmail.getBatchEmailTitle().equals(""))?batchEmail.getBatchEmailTitle():"No Content",
                    (batchEmail.getBatchEmailMessage()!=null && !batchEmail.getBatchEmailMessage().equals(""))?batchEmail.getBatchEmailMessage():"No Content",
                    true,
                    true
            );
        } else {
            return new BatchEmailClientSetupModel(
                    created.getId().toString(),
                    "",
                    "",
                    false,
                    true
            );
        }
    }

    @Override
    public BatchEmailContentSetupModel setupBatchEmailContent(BatchEmailContentModel batchEmailContentModel) {
        Optional<BatchEmail> batchEmailOptional = batchEmailRepository.findById(new ObjectId(batchEmailContentModel.getBatchEmailId()));

        if(!batchEmailOptional.isPresent()) {
            throw new ResourceNotFoundException("The promotional campaign was not found.");
        }

        BatchEmail batchEmail = batchEmailOptional.get();
        batchEmail.setBatchEmailTitle(batchEmailContentModel.getBatchEmailTitle());
        batchEmail.setBatchEmailMessage(batchEmailContentModel.getBatchEmailMessage());

        BatchEmail updated = batchEmailRepository.save(batchEmail);

        BatchEmailContentSetupModel batchEmailContentSetupModel = new BatchEmailContentSetupModel();
        batchEmailContentSetupModel.setBatchEmailId(updated.getId().toString());
        batchEmailContentSetupModel.setSuccess(true);

        return batchEmailContentSetupModel;
    }

    @Override
    public void sendBatchEmail(final String batchEmailId) {
        Optional<BatchEmail> batchEmailOptional = batchEmailRepository.findById(new ObjectId(batchEmailId));

        if(!batchEmailOptional.isPresent()) {
            throw new ResourceNotFoundException("The promotional campaign was not found.");
        }

        BatchEmail batchEmail = batchEmailOptional.get();

        batchEmail.setBatchEmailStatus(BatchEmailStatus.STARTED);
        batchEmail.setStartDate(new Date());

        BatchEmail updated = batchEmailRepository.save(batchEmail);
        ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

        try {
            Future future = threadPoolExecutor.submit(() -> {
                updated.setBatchEmailStatus(BatchEmailStatus.PROCESSING);
                batchEmailRepository.save(updated);

                String body = updated.getBatchEmailMessage().replace("\n","<br/>");

                StringBuilder emailList = new StringBuilder();
                for(Client toSendTo : updated.getToEmail()) {
                    emailList.append(toSendTo.getUsername()).append(",");
                }

                try {
                    log.log(Level.INFO, "Email to send to " + emailList.substring(0, emailList.length() - 1));
                    InternetAddress[] recipients = InternetAddress.parse(emailList.substring(0, emailList.length() - 1));

                    emailService.sendEmailPromotionalEmail(updated.getBatchEmailTitle(), body, recipients);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Sending batch email failed for " + updated.getId().toString() + " failed", e);
                }
            });
            if(future.get() == null) {
                log.log(Level.INFO, "Sending batch email completed for batch job " +batchEmail.getId().toString());
                // change to completed
                BatchEmailMetaInfo batchEmailMetaInfo = batchEmail.getBatchEmailMetaInfo();
                batchEmailMetaInfo.setTotalActualSentTo(batchEmail.getToEmail().size());
                Date completedDate = new Date();
                Date startedDate = batchEmail.getStartDate();
                long difference = completedDate.getTime() - startedDate.getTime();
                long differenceInMinutes = difference / (60 * 1000) % 60;
                batchEmailMetaInfo.setDurationInMinutes(differenceInMinutes);

                batchEmailMetaInfoRepository.save(batchEmailMetaInfo);
                batchEmail.setEndDate(completedDate);
                batchEmail.setBatchEmailStatus(BatchEmailStatus.COMPLETED);
                batchEmailRepository.save(batchEmail);
            }

            threadPoolExecutor.shutdown();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Sending batch email failed for " + batchEmail.getId().toString() + " failed", e);
            BatchEmailMetaInfo batchEmailMetaInfo = batchEmail.getBatchEmailMetaInfo();
            batchEmailMetaInfo.setTotalActualSentTo(batchEmail.getToEmail().size());
            Date completedDate = new Date();
            Date startedDate = batchEmail.getStartDate();
            long difference = completedDate.getTime() - startedDate.getTime();
            long differenceInMinutes = difference / (60 * 1000) % 60;
            batchEmailMetaInfo.setDurationInMinutes(differenceInMinutes);
            batchEmailMetaInfoRepository.save(batchEmailMetaInfo);

            batchEmail.setBatchEmailStatus(BatchEmailStatus.FAILED);
            batchEmailRepository.save(batchEmail);
        }
    }

    @Override
    public List<BatchEmailModel> viewBatchEmails() {
        return batchEmailRepository
                .findAll()
                .stream()
                .map(batchEmail -> {
                    try {
                        BatchEmailModel batchEmailModel = new BatchEmailModel();
                        batchEmailModel.setBatchEmailId(batchEmail.getId().toString());
                        batchEmailModel.setBatchEmailStatus(batchEmail.getBatchEmailStatus().toString());
                        batchEmailModel.setSendDate((batchEmail.getBatchEmailStatus().equals(BatchEmailStatus.COMPLETED))?applicationProperties.getLongDateTimeFormatter().print(batchEmail.getStartDate().getTime()):"Not Completed");
                        batchEmailModel.setBatchEmailTitle((batchEmail.getBatchEmailTitle()!=null && !batchEmail.getBatchEmailTitle().equals(""))?batchEmail.getBatchEmailTitle():"Not Completed");
                        batchEmailModel.setBatchEmailToBeSentTo((batchEmail.getBatchEmailMetaInfo()!=null)?batchEmail.getBatchEmailMetaInfo().getTotalToBeSentTo():0);
                        batchEmailModel.setBatchEmailActualSentTo((batchEmail.getBatchEmailStatus().equals(BatchEmailStatus.COMPLETED))?""+batchEmail.getBatchEmailMetaInfo().getTotalActualSentTo():"Not Completed");
                        batchEmailModel.setCompletedDate((batchEmail.getBatchEmailStatus().equals(BatchEmailStatus.COMPLETED))?applicationProperties.getLongDateTimeFormatter().print(batchEmail.getEndDate().getTime()):"Not Completed");

                        return batchEmailModel;
                    } catch (Exception e) {
                        log.severe("Unable to get batch email. " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public BatchEmailClientsModel viewSpecificBatchEmail(String batchEmailId) {
        if(!ObjectId.isValid(batchEmailId))
            throw new ResourceNotFoundException("The promotional campaign was not found.");

        Optional<BatchEmail> batchEmailOptional = batchEmailRepository.findById(new ObjectId(batchEmailId));

        if(!batchEmailOptional.isPresent()) {
            throw new ResourceNotFoundException("The promotional campaign was not found.");
        }

        BatchEmail batchEmail = batchEmailOptional.get();

        List<Client> alreadyInList = batchEmail.getToEmail();

        List<com.qbook.app.application.models.marketingModels.Client> clients = clientRepository.findAllByIsActiveOrderByFirstName(true)
                .stream()
                .map(client -> {
                    com.qbook.app.application.models.marketingModels.Client client1 = new com.qbook.app.application.models.marketingModels.Client();
                    if (isAlreadyInList(alreadyInList, client)) {
                        client1.setEmailAddress(client.getContactDetails().getEmailAddress());
                        client1.setExistsInList(true);
                        client1.setFullName(client.getFirstName() + " " + client.getLastName());
                        client1.setUserId(client.getId().toString());
                    } else {
                        client1.setEmailAddress(client.getContactDetails().getEmailAddress());
                        client1.setExistsInList(false);
                        client1.setFullName(client.getFirstName() + " " + client.getLastName());
                        client1.setUserId(client.getId().toString());
                    }

                    return client1;
                })
                .collect(Collectors.toList());


        BatchEmailClientsModel batchEmailClientsModel = new BatchEmailClientsModel();
        batchEmailClientsModel.setResending(true);
        batchEmailClientsModel.setClients(clients);
        batchEmailClientsModel.setBatchEmailId(batchEmail.getId().toString());

        return batchEmailClientsModel;
    }

    private boolean isAlreadyInList(List<Client> existingList, Client client) {
        for (Client toCheck: existingList) {
            if(toCheck.getId().toString().equals(client.getId().toString()))
                return true;
        }
        return false;
    }
}
