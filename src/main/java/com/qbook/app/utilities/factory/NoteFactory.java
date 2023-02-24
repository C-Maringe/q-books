package com.qbook.app.utilities.factory;


import com.qbook.app.application.models.ClientNewNoteModel;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Note;
import com.qbook.app.domain.models.Notification;
import com.qbook.app.domain.models.User;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;

public class NoteFactory {

    public static Note buildNote(ClientNewNoteModel clientNewNoteModel, User user) {
        ModelMapper modelMapper = new ModelMapper();

        Note note = modelMapper.map(clientNewNoteModel, Note.class);
        note.setDateCreated(DateTime.now().toDate().getTime());
        note.setDateUpdated(DateTime.now().toDate().getTime());
        note.setUuid(Factory.createRandomCode());
        note.setCreatedById(user.getId().toString());
        note.setCreatedByName(user.getFirstName() + " " + user.getLastName());
        return note;
    }
}
