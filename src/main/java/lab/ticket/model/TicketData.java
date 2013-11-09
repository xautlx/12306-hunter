/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lab.ticket.util.TicketUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * 订票主数据对象
 */
public class TicketData implements Serializable {

	private static final long serialVersionUID = 5912310187954784584L;

	private String trainFrom;
	private String trainTo;
	
	private String trainFromCode;
	private String trainToCode;
	
	private String primaryTrainDate;
	//备选日期数据，逗号分隔，只需填写“日”，如当前是20号，有效数据示例：22,23,1,2,3
	private String extraTrainDates;
	//登录用户集合
	private List<UserData> userDatas = new ArrayList<UserData>();
	//乘车人数据集合
	private List<PassengerData> passengerDatas = new ArrayList<PassengerData>();

	public String getTrainFrom() {
		return trainFrom;
	}

	public void setTrainFrom(String trainFrom) {
		this.trainFrom = trainFrom;
		this.trainFromCode=TicketUtil.getCityCode(trainFrom);
	}

	public String getTrainTo() {
		return trainTo;
	}

	public void setTrainTo(String trainTo) {
		this.trainTo = trainTo;
		this.trainToCode=TicketUtil.getCityCode(trainTo);
	}

	public List<UserData> getUserDatas() {
		return userDatas;
	}

	public void setUserDatas(List<UserData> userDatas) {
		this.userDatas = userDatas;
	}

	public List<PassengerData> getPassengerDatas() {
		return passengerDatas;
	}

	public void setPassengerDatas(List<PassengerData> passengerDatas) {
		this.passengerDatas = passengerDatas;
	}

	public String getPrimaryTrainDate() {
		return primaryTrainDate;
	}

	public void setPrimaryTrainDate(String primaryTrainDate) {
		this.primaryTrainDate = primaryTrainDate;
	}

	public String getExtraTrainDates() {
		return extraTrainDates;
	}

	public void setExtraTrainDates(String extraTrainDates) {
		this.extraTrainDates = extraTrainDates;
	}

	public String getTrainFromCode() {
		return trainFromCode;
	}

	public String getTrainToCode() {
		return trainToCode;
	}
	
	public UserData getFirstUserData() {
		return userDatas.get(0);
	}

	/**
	 * 过滤掉未勾选的乘车人记录
	 * @return
	 */
	public List<PassengerData> getValidPassengerDatas() {
		List<PassengerData> validPassengerDatas = new ArrayList<PassengerData>();
		for (PassengerData passengerData : passengerDatas) {
			if (passengerData.isSelected()) {
				validPassengerDatas.add(passengerData);
			}
		}
		return validPassengerDatas;
	}

	/**
	 * 根据订票主日期和备选日期,计算返回优先级排序的乘车日期集合
	 * @return
	 */
	public List<String> getTrainDateList() {
		List<String> trainDates = new ArrayList<String>();
		trainDates.add(primaryTrainDate);
		if (StringUtils.isNotBlank(extraTrainDates)) {
			int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			int curMonth = Calendar.getInstance().get(Calendar.MONTH);
			int curYear = Calendar.getInstance().get(Calendar.YEAR);
			String[] splitExtraTrainDate = StringUtils.split(extraTrainDates, ",");
			for (String dt : splitExtraTrainDate) {
				if (StringUtils.isNotBlank(dt.trim())) {
					int day = Integer.valueOf(dt.trim());
					if (day >= curDay) {//如果大于当前天,则说明在本月
						trainDates.add(curYear + "-" + (curMonth + 1) + "-" + day);
					} else {//如果小于当前天，则说明下下月
						trainDates.add(curYear + "-" + (curMonth + 1 + 1) + "-" + day);
					}
				}
			}
		}
		return trainDates;
	}



}
