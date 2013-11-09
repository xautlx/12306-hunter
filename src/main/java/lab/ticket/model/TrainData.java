/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.model;

import java.io.Serializable;

import lab.ticket.model.UserData.SeatType;

/**
 * 车次及席别
 */
public class TrainData implements Serializable {

	private static final long serialVersionUID = -8502617475052774379L;

	private String trainNo;

	private SeatType seatType;

	public TrainData(String trainNo, SeatType seatType) {
		super();
		this.trainNo = trainNo;
		this.seatType = seatType;
	}

	public String getTrainNo() {
		return trainNo;
	}

	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	public SeatType getSeatType() {
		return seatType;
	}

	public void setSeatType(SeatType seatType) {
		this.seatType = seatType;
	}
}
