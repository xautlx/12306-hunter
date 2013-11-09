/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.service;

import java.util.List;

import javax.swing.JOptionPane;

import lab.ticket.TicketMainFrame;
import lab.ticket.model.SingleTrainOrderVO;
import lab.ticket.model.TicketData;
import lab.ticket.model.TrainData;
import lab.ticket.model.TrainQueryInfo;
import lab.ticket.model.UserData;
import lab.ticket.view.RandomCodeDialog;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每个登录用户一个独立刷票线程
 */
public class TicketUserThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(TicketUserThread.class);

	//各请求间隔，目前已知测试0.1秒肯定失败
	private static final float SLEEP_SECONDS = 0.5f;

	private HttpClientService httpClientService;

	private TicketMainFrame ticketMainFrame;

	private TicketData ticketData;

	private UserData userData;

	private boolean success = false;

	private boolean terminalSign = false;

	private String submitCode;

	private int count = 0;

	public TicketUserThread(TicketMainFrame ticketMainFrame, HttpClientService httpClientService,
			TicketData ticketData, UserData userData) {
		this.ticketMainFrame = ticketMainFrame;
		this.httpClientService = httpClientService;
		this.ticketData = ticketData;
		this.userData = userData;
	}

	@Override
	public void run() {
		String title = "[线程： " + this.getId() + ", 登录用户：" + userData.getLoginUser() + "] ";
		TicketMainFrame.appendMessage(title + "刷票日期顺序列表：");
		List<String> trainDates = ticketData.getTrainDateList();
		for (String trainDate : trainDates) {
			TicketMainFrame.appendMessage(title + " - " + trainDate);
		}
		while (!success) {
			try {
				if (terminalSign) {
					TicketMainFrame.appendMessage(title + "终止退出");
					return;
				}

				TicketMainFrame.appendMessage(title + "第 " + (++count) + " 次刷票");

				SingleTrainOrderVO singleTrainOrderVO = null;

				for (String trainDate : trainDates) {
					TicketMainFrame.appendMessage(title + "查询【" + trainDate + "】车票信息响应:");
					List<TrainQueryInfo> trainQueryInfos = httpClientService
							.queryTrain(ticketData, userData, trainDate);
					for (TrainQueryInfo trainQueryInfo : trainQueryInfos) {
						if (!trainQueryInfo.isValidForPurchase()) {
							TicketMainFrame.appendMessage(title + " - :( 当前不可购买或无票车次：" + trainDate + trainQueryInfo);
						} else {

							TicketMainFrame.appendMessage(title + " - :) 可购买车次：" + trainDate + trainQueryInfo);
						}
					}

					// 从查询到车次数据按照优先级找到最匹配的车次和座位类型
					// --：没有该席别；*：未到开始时间；有：有并且数量充足；数字：有但数量有限：无：已售完
					List<TrainData> trainDatas = userData.getTrainDatas();
					for (TrainData trainData : trainDatas) {
						for (TrainQueryInfo trainQueryInfo : trainQueryInfos) {
							if (!trainQueryInfo.isValidForPurchase()) {
								continue;
							}
							if (trainQueryInfo.getTrainNo().equals(trainData.getTrainNo())) {
								String seatTypeValue = trainQueryInfo.getSeatDatas().get(trainData.getSeatType());
								if (seatTypeValue.equals("无")) {
									continue;
								}
								TicketMainFrame.appendMessage(title + ":) 预定车次有票：" + trainData.getTrainNo() + ",席别："
										+ trainData.getSeatType() + ",日期：" + trainDate + ",车票：" + seatTypeValue);

								singleTrainOrderVO = new SingleTrainOrderVO();
								singleTrainOrderVO.setTrainQueryInfo(trainQueryInfo);
								singleTrainOrderVO.setLoginUser(userData.getLoginUser());
								singleTrainOrderVO.setCookieData(userData.getCookieData());
								singleTrainOrderVO.setSeatType(trainData.getSeatType());
								singleTrainOrderVO.setTrainDate(trainDate);
								singleTrainOrderVO.setTrainNo(trainData.getTrainNo());
								break;
							}
						}
						if (singleTrainOrderVO != null) {
							break;
						}
						TicketMainFrame.appendMessage(title + ":( 预定车次无票：" + trainData.getTrainNo() + ",席别："
								+ trainData.getSeatType() + ",日期：" + trainDate);
					}

					if (singleTrainOrderVO != null) {
						break;
					}
				}

				if (singleTrainOrderVO == null) {
					TicketMainFrame.appendMessage(title + "郁闷：所有指定日期车次目前均无法购票或无票。暂停 " + SLEEP_SECONDS + " 秒继续尝试刷票...");
					Thread.sleep(Float.valueOf(SLEEP_SECONDS * 1000).intValue());
				} else {

					TicketMainFrame.appendMessage(title + "暂停 " + SLEEP_SECONDS + " 秒后继续...");
					Thread.sleep(Float.valueOf(SLEEP_SECONDS * 1000).intValue());
					httpClientService.submitOrderRequest(singleTrainOrderVO);

					String responseBody = null;
					do {
						synchronized (this) {
							new RandomCodeDialog(singleTrainOrderVO, this);
							this.wait();
						}
						if (StringUtils.isBlank(this.submitCode)) {
							TicketMainFrame.appendMessage(title + "已取消本次下单验证码输入");
							break;
						}
						responseBody = httpClientService.confirmSingleForQueueOrder(ticketData, singleTrainOrderVO,
								this.submitCode, true);
						if (responseBody.contains("验证码")) {
							TicketMainFrame.appendMessage(title + "下单验证码错误，请重新输入");
						}
					} while (responseBody.contains("验证码"));

					//如果errMsg不为Y则表示访问错误，继续下一次循环
					if (responseBody == null || responseBody.indexOf("\"errMsg\":\"Y\"") == -1) {
						continue;
					}

					//下单确认前的余票数量检查请求
					TicketMainFrame.appendMessage(title + "暂停 " + SLEEP_SECONDS + " 秒后继续...");
					Thread.sleep(Float.valueOf(SLEEP_SECONDS * 1000).intValue());
					responseBody = httpClientService.getQueueCount(ticketData, singleTrainOrderVO);
					//此处可考虑添加检查判断逻辑：只有最新返回有余票才继续后续提交订单

					// 提交订单
					TicketMainFrame.appendMessage(title + "暂停 " + SLEEP_SECONDS + " 秒后继续...");
					Thread.sleep(Float.valueOf(SLEEP_SECONDS * 1000).intValue());
					responseBody = httpClientService.confirmSingleForQueueOrder(ticketData, singleTrainOrderVO,
							this.submitCode, false);

					if (responseBody.indexOf("\"errMsg\":\"Y\"") > -1) {
						String msg = title + "貌似已成功下单，赶快用浏览器登录此账号访问“未完成订单”中查看确认及进行后续付款操作";
						JOptionPane.showMessageDialog(ticketMainFrame, msg);
						TicketMainFrame.appendMessage(msg);
						TicketMainFrame.appendMessage(title + "已终止当前用户刷票线程，其他用户刷票线程继续运行");
						success = true;
						break;
					}
				}

			} catch (InterruptedException e) {
				TicketMainFrame.appendMessage(title + "异常：" + e.getMessage());
				logger.error("Error at thread " + this.getId() + " for user " + userData.getLoginUser(), e);
			}
		}
	}

	public void sendTerminalSign() {
		String title = "[线程： " + this.getId() + ", 登录用户：" + userData.getLoginUser() + "] ";
		TicketMainFrame.appendMessage(title + "收到终止信号");
		terminalSign = true;
	}

	public void setSubmitCode(String submitCode) {
		this.submitCode = submitCode;
	}

	public int getCount() {
		return count;
	}
}
