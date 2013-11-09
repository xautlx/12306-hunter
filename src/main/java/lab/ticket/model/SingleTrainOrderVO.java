/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.model;

import java.util.Map;

import lab.ticket.model.UserData.SeatType;

public class SingleTrainOrderVO {

	private String loginUser;

	private Map<String, String> cookieData;
	
	private String trainDate;

	private String trainNo;

	private SeatType seatType;

	private TrainQueryInfo trainQueryInfo;

	private String submitOrderRequestToken;
	private String submitOrderRequestLeftTicketStr;
	
	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public Map<String, String> getCookieData() {
		return cookieData;
	}

	public void setCookieData(Map<String, String> cookieData) {
		this.cookieData = cookieData;
	}

	public String getSubmitOrderRequestToken() {
		return submitOrderRequestToken;
	}

	public void setSubmitOrderRequestToken(String submitOrderRequestToken) {
		this.submitOrderRequestToken = submitOrderRequestToken;
	}

	public String getSubmitOrderRequestLeftTicketStr() {
		return submitOrderRequestLeftTicketStr;
	}

	public void setSubmitOrderRequestLeftTicketStr(String submitOrderRequestLeftTicketStr) {
		this.submitOrderRequestLeftTicketStr = submitOrderRequestLeftTicketStr;
	}

	public String getTrainDate() {
		return trainDate;
	}

	public void setTrainDate(String trainDate) {
		this.trainDate = trainDate;
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

	public TrainQueryInfo getTrainQueryInfo() {
		return trainQueryInfo;
	}

	public void setTrainQueryInfo(TrainQueryInfo trainQueryInfo) {
		this.trainQueryInfo = trainQueryInfo;
	}
}
