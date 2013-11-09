/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import lab.ticket.model.PassengerData;
import lab.ticket.model.TicketData;
import lab.ticket.model.TrainData;
import lab.ticket.model.TrainQueryInfo;
import lab.ticket.model.UserData;
import lab.ticket.service.HttpClientService;
import lab.ticket.service.TicketUserThread;
import lab.ticket.view.PassengerPanel;
import lab.ticket.view.UserPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 12306刷票程序主应用。由Eclipse WindowBuilder插件设计UI原型。
 */
public class TicketMainFrame extends JFrame {

	private static final long serialVersionUID = -6541864654653129335L;

	private static final Logger logger = LoggerFactory.getLogger(UserPanel.class);

	private static final DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
	private HttpClientService httpClientService = new HttpClientService();
	private List<TicketUserThread> ticketThreads = new ArrayList<TicketUserThread>();

	private static TicketMainFrame frame;
	private JPanel m_contentPane;
	private JPanel panel_4;
	private JLabel lblNewLabel_4;
	private JTextField textTrainFrom;
	private JLabel lblNewLabel_5;
	private JTextField textTrainTo;
	private JLabel lblNewLabel_6;
	private JTextField textPrimaryTrainDate;
	private JPanel userPanelContainer;
	private JPanel panelLogger;
	private JPanel passengerPanelContainer;
	private UserPanel defaultUserPanel;
	private PassengerPanel defaultPassengerPanel;
	private JLabel label_2;
	private static JScrollPane scrollPaneLogger;
	private static JTextArea consoleArea;
	private JPanel panelOperation;
	private JButton btnStart;
	private JButton btnStop;
	private JLabel label;
	private JTextField textExtraTrainDates;
	private JLabel lblNewLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new TicketMainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TicketMainFrame() {

		final String filePath = System.getProperty("user.dir") + File.separator + "12306.dat";

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath));
					out.writeObject(frame.bindUItoModel());
					out.close();
					logger.debug("Saved UI data to file: {}", filePath);
				} catch (Exception e) {
					logger.error("Save UI data to file error", e);
				}
			}

			@Override
			public void windowOpened(WindowEvent event) {
				try {
					// 初始化购票日期为20天预售期日期
					Calendar trainDate = Calendar.getInstance();
					trainDate.add(Calendar.DAY_OF_MONTH, 19);
					textPrimaryTrainDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(trainDate.getTime()));

					// 基于上次保存的dat数据文件恢复UI组件初始值
					File inFile = new File(filePath);
					if (!inFile.exists()) {
						return;
					}
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(inFile));
					TicketData ticketData = (TicketData) in.readObject();
					in.close();
					logger.debug("Loaded UI data from file: {}", filePath);
					textTrainFrom.setText(ticketData.getTrainFrom());
					textTrainTo.setText(ticketData.getTrainTo());
					textExtraTrainDates.setText(ticketData.getExtraTrainDates());
					// 初始化登录用户信息
					List<UserData> userDatas = ticketData.getUserDatas();
					if (userDatas != null && userDatas.size() > 0) {
						for (int i = 0; i < userDatas.size(); i++) {
							UserData userData = userDatas.get(i);
							if (i == 0) {
								defaultUserPanel.bindModeltoUI(userData, true);
							} else {
								UserPanel userPanel = addUserPanel();
								userPanel.bindModeltoUI(userData);
							}
						}
					}
					// 初始化乘客信息
					List<PassengerData> passengerDatas = ticketData.getPassengerDatas();
					if (passengerDatas != null && passengerDatas.size() > 0) {
						for (int i = 0; i < passengerDatas.size(); i++) {
							PassengerData passengerData = passengerDatas.get(i);
							if (i == 0) {
								defaultPassengerPanel.bindModeltoUI(passengerData);
							} else {
								PassengerPanel passengerPanel = addPassengerPanel();
								passengerPanel.bindModeltoUI(passengerData);
							}
						}
					}

				} catch (Exception e) {
					logger.error("Load UI data from file error", e);
				}
			}
		});

		setTitle("12306-hunter: Java Swing C/S版本12306订票助手. 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途.");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1051, 781);
		m_contentPane = new JPanel();
		setContentPane(m_contentPane);
		GridBagLayout gbl_m_contentPane = new GridBagLayout();
		gbl_m_contentPane.columnWidths = new int[] { 1024, 0 };
		gbl_m_contentPane.rowHeights = new int[] { 50, 150, 126, 39, 148, 0 };
		gbl_m_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_m_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		m_contentPane.setLayout(gbl_m_contentPane);

		panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 0;
		m_contentPane.add(panel_4, gbc_panel_4);
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"\u7B2C\u4E00\u6B65\uFF1A\u8F93\u5165\u4E58\u8F66\u4FE1\u606F", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 68, 100, 54, 94, 0, 80, 77, 200, 0, 0 };
		gbl_panel_4.rowHeights = new int[] { 37, 0 };
		gbl_panel_4.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);

		lblNewLabel_4 = new JLabel("起站");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 0;
		panel_4.add(lblNewLabel_4, gbc_lblNewLabel_4);

		textTrainFrom = new JTextField();
		textTrainFrom.setToolTipText("必须填写精确的站点名称");
		GridBagConstraints gbc_textTrainFrom = new GridBagConstraints();
		gbc_textTrainFrom.insets = new Insets(0, 0, 0, 5);
		gbc_textTrainFrom.fill = GridBagConstraints.HORIZONTAL;
		gbc_textTrainFrom.gridx = 1;
		gbc_textTrainFrom.gridy = 0;
		panel_4.add(textTrainFrom, gbc_textTrainFrom);
		textTrainFrom.setColumns(10);

		lblNewLabel_5 = new JLabel("到站");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.gridx = 2;
		gbc_lblNewLabel_5.gridy = 0;
		panel_4.add(lblNewLabel_5, gbc_lblNewLabel_5);

		textTrainTo = new JTextField();
		textTrainTo.setToolTipText("必须填写精确的站点名称");
		GridBagConstraints gbc_textTrainTo = new GridBagConstraints();
		gbc_textTrainTo.fill = GridBagConstraints.HORIZONTAL;
		gbc_textTrainTo.insets = new Insets(0, 0, 0, 5);
		gbc_textTrainTo.gridx = 3;
		gbc_textTrainTo.gridy = 0;
		panel_4.add(textTrainTo, gbc_textTrainTo);
		textTrainTo.setColumns(10);

		lblNewLabel_6 = new JLabel("乘车日期");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.gridx = 4;
		gbc_lblNewLabel_6.gridy = 0;
		panel_4.add(lblNewLabel_6, gbc_lblNewLabel_6);

		textPrimaryTrainDate = new JTextField();
		textPrimaryTrainDate.setToolTipText("每次启动自动设定为当前日期20天后预售期");
		GridBagConstraints gbc_textPrimaryTrainDate = new GridBagConstraints();
		gbc_textPrimaryTrainDate.insets = new Insets(0, 0, 0, 5);
		gbc_textPrimaryTrainDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPrimaryTrainDate.gridx = 5;
		gbc_textPrimaryTrainDate.gridy = 0;
		panel_4.add(textPrimaryTrainDate, gbc_textPrimaryTrainDate);
		textPrimaryTrainDate.setColumns(10);

		label = new JLabel("备选日期");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 0, 5);
		gbc_label.gridx = 6;
		gbc_label.gridy = 0;
		panel_4.add(label, gbc_label);

		textExtraTrainDates = new JTextField();
		textExtraTrainDates.setToolTipText("主要用在指定多个日期刷“退票”");
		textExtraTrainDates.setColumns(10);
		GridBagConstraints gbc_textExtraTrainDates = new GridBagConstraints();
		gbc_textExtraTrainDates.insets = new Insets(0, 0, 0, 5);
		gbc_textExtraTrainDates.fill = GridBagConstraints.HORIZONTAL;
		gbc_textExtraTrainDates.gridx = 7;
		gbc_textExtraTrainDates.gridy = 0;
		panel_4.add(textExtraTrainDates, gbc_textExtraTrainDates);

		lblNewLabel = new JLabel("格式：按照预计乘车“日”优先级逗号分隔填写，如20,19,18,14");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridx = 8;
		gbc_lblNewLabel.gridy = 0;
		panel_4.add(lblNewLabel, gbc_lblNewLabel);

		userPanelContainer = new JPanel();
		userPanelContainer.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"\u7B2C\u4E8C\u6B65\uFF1A\u7528\u6237\u53CA\u8F66\u6B21\u8BBE\u7F6E", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagConstraints gbc_userPanelContainer = new GridBagConstraints();
		gbc_userPanelContainer.fill = GridBagConstraints.BOTH;
		gbc_userPanelContainer.insets = new Insets(0, 0, 5, 0);
		gbc_userPanelContainer.gridx = 0;
		gbc_userPanelContainer.gridy = 1;
		m_contentPane.add(userPanelContainer, gbc_userPanelContainer);
		userPanelContainer.setLayout(new GridLayout(0, 1, 0, 0));

		passengerPanelContainer = new JPanel();
		passengerPanelContainer.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u7B2C\u4E09\u6B65\uFF1A\u4E58\u8F66\u4EBA\u4FE1\u606F", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_passengerPanelContainer = new GridBagConstraints();
		gbc_passengerPanelContainer.fill = GridBagConstraints.HORIZONTAL;
		gbc_passengerPanelContainer.insets = new Insets(0, 0, 5, 0);
		gbc_passengerPanelContainer.gridx = 0;
		gbc_passengerPanelContainer.gridy = 2;
		m_contentPane.add(passengerPanelContainer, gbc_passengerPanelContainer);

		panelOperation = new JPanel();
		GridBagConstraints gbc_panelOperation = new GridBagConstraints();
		gbc_panelOperation.fill = GridBagConstraints.BOTH;
		gbc_panelOperation.insets = new Insets(0, 0, 5, 0);
		gbc_panelOperation.gridx = 0;
		gbc_panelOperation.gridy = 3;
		m_contentPane.add(panelOperation, gbc_panelOperation);
		panelOperation.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnStart = new JButton("开始自动刷票");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TicketData ticketData = frame.bindUItoModel();

				TicketMainFrame.appendMessage("-------填写信息检查---开始---------");
				TicketMainFrame.appendMessage("基于输入的[起点站:" + textTrainFrom.getText() + ",终点站：" + textTrainTo.getText()
						+ ",乘车日期：" + textPrimaryTrainDate.getText() + "]查询车票信息响应:");
				UserData firstUserData = ticketData.getFirstUserData();
				if (!firstUserData.isLoginSuccess()) {
					TicketMainFrame.appendMessage("错误：第一个用户账号必须成功登录");
					JOptionPane.showMessageDialog(frame, "错误：第一个用户账号必须成功登录");
					return;
				}
				List<TrainQueryInfo> trainQueryInfos = httpClientService.queryTrain(ticketData, firstUserData,
						ticketData.getPrimaryTrainDate());
				for (TrainQueryInfo trainQueryInfo : trainQueryInfos) {
					TicketMainFrame.appendMessage(trainQueryInfo.toString());

				}
				String fmtMessage = "%s车次 > 登录账号:%s, 车次：%s, 席别：%s";
				for (UserData userData : ticketData.getUserDatas()) {
					for (TrainData trainData : userData.getTrainDatas()) {
						boolean valid = false;
						for (TrainQueryInfo trainQueryInfo : trainQueryInfos) {
							if (trainQueryInfo.validateTrainData(trainData)) {
								valid = true;
								break;
							}
						}
						String msg = String.format(fmtMessage, (valid ? "有效" : "无效"), userData.getLoginUser(),
								trainData.getTrainNo(), trainData.getSeatType());
						TicketMainFrame.appendMessage(msg);
						if (!valid) {
							JOptionPane.showMessageDialog(frame, msg);
							return;
						}
					}
				}
				TicketMainFrame.appendMessage("-------填写信息检查---完毕---------");

				btnStart.setEnabled(false);
				//基于登录账号启动刷票线程
				for (UserData userData : ticketData.getUserDatas()) {
					if (userData.isLoginSuccess()) {
						TicketUserThread ticketThread = new TicketUserThread(frame, httpClientService, ticketData,
								userData);
						ticketThread.start();
						ticketThreads.add(ticketThread);
					} else {
						TicketMainFrame.appendMessage("忽略未成功登录用户账号：" + userData.getLoginUser());
					}
				}
				btnStop.setEnabled(true);
			}
		});
		panelOperation.add(btnStart);

		btnStop = new JButton("停止自动刷票");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStop.setEnabled(false);
				if (ticketThreads != null) {
					for (TicketUserThread ticketThread : ticketThreads) {
						ticketThread.sendTerminalSign();
					}
				}
				ticketThreads.clear();
				btnStart.setEnabled(true);
			}
		});
		panelOperation.add(btnStop);

		label_2 = new JLabel("");
		panelOperation.add(label_2);

		panelLogger = new JPanel();
		panelLogger.setBorder(new TitledBorder(null, "\u8FD0\u884C\u8BB0\u5F55", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panelLogger = new GridBagConstraints();
		gbc_panelLogger.fill = GridBagConstraints.BOTH;
		gbc_panelLogger.gridx = 0;
		gbc_panelLogger.gridy = 4;
		m_contentPane.add(panelLogger, gbc_panelLogger);
		panelLogger.setLayout(new GridLayout(0, 1, 0, 0));

		scrollPaneLogger = new JScrollPane();
		panelLogger.add(scrollPaneLogger);

		consoleArea = new JTextArea();
		scrollPaneLogger.setViewportView(consoleArea);
		DefaultCaret caret = (DefaultCaret) consoleArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		defaultUserPanel = new UserPanel(this);
		userPanelContainer.add(defaultUserPanel);
		passengerPanelContainer.setLayout(new GridLayout(0, 2, 0, 0));

		defaultPassengerPanel = new PassengerPanel(this);
		passengerPanelContainer.add(defaultPassengerPanel);
	}

	public UserPanel addUserPanel() {
		UserPanel userPanel = new UserPanel(this, true);
		int rowHeight = ((GridBagLayout) m_contentPane.getLayout()).rowHeights[1];
		((GridBagLayout) m_contentPane.getLayout()).rowHeights[1] = userPanel.getHeight() + rowHeight;
		userPanelContainer.add(userPanel);
		m_contentPane.validate();
		return userPanel;
	}

	public void removeUserPanel(UserPanel userPanel) {
		int rowHeight = ((GridBagLayout) m_contentPane.getLayout()).rowHeights[1];
		((GridBagLayout) m_contentPane.getLayout()).rowHeights[1] = rowHeight - userPanel.getHeight();
		userPanelContainer.remove(userPanel);
		m_contentPane.validate();
	}

	public PassengerPanel addPassengerPanel() {
		PassengerPanel passengerPanel = new PassengerPanel(this, true);
		int rowHeight = ((GridBagLayout) m_contentPane.getLayout()).rowHeights[2];
		((GridBagLayout) m_contentPane.getLayout()).rowHeights[2] = passengerPanel.getHeight() + rowHeight;
		passengerPanelContainer.add(passengerPanel);
		m_contentPane.validate();
		return passengerPanel;
	}

	public void removePassengerPanel(PassengerPanel passengerPanel) {
		int rowHeight = ((GridBagLayout) m_contentPane.getLayout()).rowHeights[2];
		((GridBagLayout) m_contentPane.getLayout()).rowHeights[2] = rowHeight - passengerPanel.getHeight();
		passengerPanelContainer.remove(passengerPanel);
		m_contentPane.validate();
	}

	/**
	 * 追加显示日志消息
	 * @param message
	 */
	public static synchronized void appendMessage(String message) {
		logger.debug(message);
		if (consoleArea != null) {
			//force to scroll to bottom
			consoleArea.setText(consoleArea.getText() + df.format(new Date()) + ": " + message + "\n");
		}
	}

	/**
	 * 绑定UI数据到模型对象
	 * @return
	 */
	private TicketData bindUItoModel() {
		TicketData ticketData = new TicketData();
		ticketData.setTrainFrom(textTrainFrom.getText());
		ticketData.setTrainTo(textTrainTo.getText());
		ticketData.setPrimaryTrainDate(textPrimaryTrainDate.getText());
		ticketData.setExtraTrainDates(textExtraTrainDates.getText());
		for (Component component : userPanelContainer.getComponents()) {
			ticketData.getUserDatas().add(((UserPanel) component).bindUItoModel());
		}
		for (Component component : passengerPanelContainer.getComponents()) {
			ticketData.getPassengerDatas().add(((PassengerPanel) component).bindUItoModel());
		}
		return ticketData;
	}

}
