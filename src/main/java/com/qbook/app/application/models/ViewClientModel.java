package com.qbook.app.application.models;

import com.qbook.app.domain.models.Note;
import com.qbook.app.domain.models.Voucher;
import lombok.Data;

import java.util.List;

@Data
public class ViewClientModel {
	private String id;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String mobileNumber;
	private String dateOfBirth;
	private List<Note> notes;
	private int loyaltyPoints;
	private List<Voucher> vouchers;
}
