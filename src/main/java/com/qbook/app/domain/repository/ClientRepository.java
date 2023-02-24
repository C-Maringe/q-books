package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Client;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends MongoRepository<Client, ObjectId> {
    Optional<Client> findByUsername(String username);
    List<Client> findAllByIsActiveOrderByFirstName(boolean active);
    List<Client> findAllByReceiveMarketingEmailsAndIsActive(boolean receiveMarketingEmail, boolean active);
    List<Client> findAllByReceiveMarketingEmails(boolean receiveMarketingEmail);
    List<Client> findAllByDateRegisteredBetween(Long from, Long to);
    List<Client> findAllByDateRegisteredAfter(Long after);
    long countByIsActive(boolean active);
}
