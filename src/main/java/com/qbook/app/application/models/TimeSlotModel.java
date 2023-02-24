package com.qbook.app.application.models;


import lombok.Data;

import java.util.Objects;

@Data
public class TimeSlotModel {
	private String time;
	private boolean available;

//	public String getTime() {
//		return time;
//	}
//
	public TimeSlotModel(String time, boolean available) {
		this.time = time;
		this.available = available;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TimeSlotModel)) return false;
		TimeSlotModel that = (TimeSlotModel) o;
		return Objects.equals(time, that.time);
	}

	@Override
	public int hashCode() {
		return Objects.hash(time);
	}
}
