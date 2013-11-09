/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 登录账号数据模型
 */
public class UserData implements Serializable {

	private static final long serialVersionUID = -8827235378622351629L;

	private String loginUser;

	private boolean loginSuccess = false;

	//登录用户的Cookie数据对象
	private Map<String, String> cookieData;

	private List<TrainData> trainDatas = new ArrayList<TrainData>();


	public enum SeatType {
		BUSS_SEAT("商务座", "9"),
		BEST_SEAT("特等座", "P"),
		ONE_SEAT("一等座", "M"),
		TWO_SEAT("二等座", "O"),
		VAG_SLEEPER("高级软卧", "6"),
		SOFT_SLEEPER("软卧", "4"),
		HARD_SLEEPER("硬卧", "3"),
		SOFT_SEAT("软座", "2"),
		HARD_SEAT("硬座", "1"),
		NONE_SEAT("无座", "-1"),
		OTH_SEAT("其他", "0");

		private String label;
		private String value;

		private SeatType(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

		public String toString() {
			return label;
		}
	}

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

	public List<TrainData> getTrainDatas() {
		return trainDatas;
	}

	public void setTrainDatas(List<TrainData> trainDatas) {
		this.trainDatas = trainDatas;
	}

	public boolean isLoginSuccess() {
		return loginSuccess;
	}

	public void setLoginSuccess(boolean loginSuccess) {
		this.loginSuccess = loginSuccess;
	}
}
