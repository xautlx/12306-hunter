/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.model;

import java.util.HashMap;
import java.util.Map;

import lab.ticket.model.UserData.SeatType;
import lab.ticket.util.TicketUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * 列车信息实体类
 */
public class TrainQueryInfo {

	private String trainCode;// 序号
	private String trainNo; // 车次
	private String fromStation;// 发站
	private String fromStationName; // 发站中心火车站
	private String fromStationCode; // 发站code
	private String startTime;// 发时
	private String toStation;// 到站
	private String toStationName;// 到站中心火车站
	private String toStationCode;// 到站code
	private String endTime; // 到时
	private String locationCode; // 位置code
	private String takeTime;// 历时
	private String formStationNo; // 发站编号
	private String toStationNo; // 到站编号

	// --：没有该席别；*：未到开始时间；有：有并且数量充足；数字：有但数量有限：无：已售完
	private Map<SeatType, String> seatDatas = new HashMap<SeatType, String>();

	private String mmStr;// mmString
	private String trainno4;// trainno4
	private String ypInfoDetail;// ypInfoDetail
	private String single_round_type = "1"; // single_round_type

	private boolean validForPurchase;

	public String getTrainCode() {
		return trainCode;
	}

	public void setTrainCode(String trainCode) {
		this.trainCode = trainCode;
	}

	public String getTrainNo() {
		return trainNo;
	}

	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	public String getFromStation() {
		return fromStation;
	}

	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}

	public String getFromStationCode() {
		return fromStationCode;
	}

	public void setFromStationCode(String fromStationCode) {
		this.fromStationCode = fromStationCode;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getToStation() {
		return toStation;
	}

	public void setToStation(String toStation) {
		this.toStation = toStation;
	}

	public String getToStationCode() {
		return toStationCode;
	}

	public void setToStationCode(String toStationCode) {
		this.toStationCode = toStationCode;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getTakeTime() {
		return takeTime;
	}

	public void setTakeTime(String takeTime) {
		this.takeTime = takeTime;
	}

	public String getFormStationNo() {
		return formStationNo;
	}

	public void setFormStationNo(String formStationNo) {
		this.formStationNo = formStationNo;
	}

	public String getToStationNo() {
		return toStationNo;
	}

	public void setToStationNo(String toStationNo) {
		this.toStationNo = toStationNo;
	}

	public String getMmStr() {
		return mmStr;
	}

	public void setMmStr(String mmStr) {
		this.mmStr = mmStr;
	}

	public String getTrainno4() {
		return trainno4;
	}

	public void setTrainno4(String trainno4) {
		this.trainno4 = trainno4;
	}

	public String getYpInfoDetail() {
		return ypInfoDetail;
	}

	public void setYpInfoDetail(String ypInfoDetail) {
		this.ypInfoDetail = ypInfoDetail;
	}

	public String getSingle_round_type() {
		return single_round_type;
	}

	public void setSingle_round_type(String single_round_type) {
		this.single_round_type = single_round_type;
	}

	public String getFromStationName() {
		return fromStationName;
	}

	public void setFromStationName(String fromStationName) {
		this.fromStationName = fromStationName;
	}

	public String getToStationName() {
		return toStationName;
	}

	public void setToStationName(String toStationName) {
		this.toStationName = toStationName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append("车次=");
		builder.append(trainNo);
		builder.append(", 开车时间=");
		builder.append(startTime);
		builder.append(", 到站时间=");
		builder.append(endTime);
		builder.append(", 乘车时长=");
		builder.append(takeTime);
		builder.append(", 车票信息=");
		Map<SeatType, String> displaysSeatDatas = new HashMap<UserData.SeatType, String>();
		for (Map.Entry<SeatType, String> me : seatDatas.entrySet()) {
			if (!me.getValue().equals("--")) {
				displaysSeatDatas.put(me.getKey(), me.getValue());
			}
		}
		builder.append(displaysSeatDatas.toString());
		builder.append("]");
		return builder.toString();
	}

	public Map<SeatType, String> getSeatDatas() {
		return seatDatas;
	}

	public void setSeatDatas(Map<SeatType, String> seatDatas) {
		this.seatDatas = seatDatas;
	}

	public boolean isValidForPurchase() {
		return validForPurchase;
	}

	public void setValidForPurchase(boolean validForPurchase) {
		this.validForPurchase = validForPurchase;
	}

	/**
	 * 检查给定的车次座位类型是否有效
	 * @param trainData
	 * @return
	 */
	public boolean validateTrainData(TrainData trainData) {
		if (!trainData.getTrainNo().equalsIgnoreCase(this.trainNo)) {
			return false;
		}
		String st = seatDatas.get(trainData.getSeatType());
		if (StringUtils.isBlank(st) || st.equals(TicketUtil.INVALID_SEAT_TYPE)) {
			return false;
		}
		return true;
	}

}
