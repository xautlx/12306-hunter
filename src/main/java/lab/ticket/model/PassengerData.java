/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 * 个人可自由免费使用或二次开发，自行承担任何相关责任
 */
package lab.ticket.model;

import java.io.Serializable;

import lab.ticket.model.UserData.SeatType;

/**
 * 乘客数据对象
 */
public class PassengerData implements Serializable {

	private static final long serialVersionUID = -4047393628292653857L;

	private boolean selected = true;
	private String cardNo;
	private CardType cardType = CardType.T1;
	private String name;
	private String mobile;
	private TicketType ticketType = TicketType.T1;

	public enum TicketType {
		T1("成人票", "1"),
		T2("儿童票", "2"),
		T3("学生票", "3"),
		T4("残军票", "4");

		private String label;
		private String value;

		private TicketType(String label, String value) {
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

	public enum CardType {
		T1("二代身份证", "1"),
		T2("一代身份证", "2"),
		T3("港澳通行证", "C"),
		T4("台湾通行证", "G"),
		T5("护照", "B");

		private String label;
		private String value;

		private CardType(String label, String value) {
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

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public TicketType getTicketType() {
		return ticketType;
	}

	public void setTicketType(TicketType ticketType) {
		this.ticketType = ticketType;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getShortText() {
		StringBuilder builder = new StringBuilder();
		builder.append(name).append(",").append(cardType.getValue()).append(",").append(cardNo);
		return builder.toString();
	}

	public String getLongText(SeatType seatType) {
		StringBuilder builder = new StringBuilder();
		builder.append(seatType.getValue()).append(",").append("0,").append(ticketType.getValue()).append(",")
				.append(getShortText()).append(",").append(mobile).append(",").append("Y");
		return builder.toString();
	}
}
