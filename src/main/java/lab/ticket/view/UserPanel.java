/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import lab.ticket.TicketMainFrame;
import lab.ticket.model.TrainData;
import lab.ticket.model.UserData;
import lab.ticket.model.UserData.SeatType;
import lab.ticket.service.HttpClientService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录账号输入UI对象，由Eclipse WindowBuilder插件设计UI原型
 */
public class UserPanel extends JPanel {

	private static final long serialVersionUID = 6430934135358072444L;

	private static final Logger logger = LoggerFactory.getLogger(UserPanel.class);

	private TicketMainFrame container;
	private Map<String, String> cookieData;
	private UserPanel self=this;

	private HttpClientService httpClientService = new HttpClientService();
	private JLabel label_4;
	private JLabel label_10;
	private JLabel label_11;
	private JLabel label1;
	private JLabel label2;
	private JTextField textLoginUser;
	private JPasswordField textLoginPasswd;
	private JTextField textLoginCode;
	private JLabel lblLoginCodeImg;
	private JButton btnLogin;
	private JTextField textTrainNo1;
	private JTextField textTrainNo2;
	private JTextArea textCookie;
	private JComboBox comboSeatType1;
	private JComboBox comboSeatType2;
	private JPanel panel;
	private JPanel panel_2;
	private JPanel panel_3;
	private JPanel panel_4;
	private JLabel label3;
	private JLabel label4;
	private JLabel label5;
	private JTextField textTrainNo3;
	private JTextField textTrainNo4;
	private JTextField textTrainNo5;
	private JComboBox comboSeatType4;
	private JComboBox comboSeatType3;
	private JComboBox comboSeatType5;
	private JButton btnLineOP;
	private JPanel panel_5;
	private JLabel lblNewLabel;

	/**
	 * Create the panel.
	 */
	public UserPanel() {
		setBorder(new TitledBorder(null, "\u7528\u6237", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 286, 0 };
		gridBagLayout.rowHeights = new int[] { 62, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 106, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 30, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 50, 94, 87, 73, 72, 0, 60, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		label_4 = new JLabel("账号");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_4.insets = new Insets(0, 0, 5, 5);
		gbc_label_4.gridx = 1;
		gbc_label_4.gridy = 0;
		panel_2.add(label_4, gbc_label_4);

		label_10 = new JLabel("密码");
		GridBagConstraints gbc_label_10 = new GridBagConstraints();
		gbc_label_10.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_10.insets = new Insets(0, 0, 5, 5);
		gbc_label_10.gridx = 2;
		gbc_label_10.gridy = 0;
		panel_2.add(label_10, gbc_label_10);

		label_11 = new JLabel("登录验证码");
		GridBagConstraints gbc_label_11 = new GridBagConstraints();
		gbc_label_11.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_11.insets = new Insets(0, 0, 5, 5);
		gbc_label_11.gridx = 3;
		gbc_label_11.gridy = 0;
		panel_2.add(label_11, gbc_label_11);

		btnLineOP = new JButton("+");
		btnLineOP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String op = btnLineOP.getText();
				if ("-".equals(op)) {
					container.removeUserPanel(self);
				} else {
					container.addUserPanel();
				}

			}
		});
		GridBagConstraints gbc_btnLineOP = new GridBagConstraints();
		gbc_btnLineOP.insets = new Insets(0, 0, 0, 5);
		gbc_btnLineOP.gridx = 0;
		gbc_btnLineOP.gridy = 1;
		panel_2.add(btnLineOP, gbc_btnLineOP);

		textLoginUser = new JTextField();
		GridBagConstraints gbc_textLoginUser = new GridBagConstraints();
		gbc_textLoginUser.fill = GridBagConstraints.HORIZONTAL;
		gbc_textLoginUser.insets = new Insets(0, 0, 0, 5);
		gbc_textLoginUser.gridx = 1;
		gbc_textLoginUser.gridy = 1;
		panel_2.add(textLoginUser, gbc_textLoginUser);
		textLoginUser.setColumns(10);

		textLoginPasswd = new JPasswordField();
		GridBagConstraints gbc_textLoginPasswd = new GridBagConstraints();
		gbc_textLoginPasswd.fill = GridBagConstraints.HORIZONTAL;
		gbc_textLoginPasswd.insets = new Insets(0, 0, 0, 5);
		gbc_textLoginPasswd.gridx = 2;
		gbc_textLoginPasswd.gridy = 1;
		panel_2.add(textLoginPasswd, gbc_textLoginPasswd);
		textLoginPasswd.setColumns(10);

		textLoginCode = new JTextField();
		textLoginCode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textLoginCode.setText(textLoginCode.getText().toUpperCase());
				String loginCode = textLoginCode.getText();
				if (loginCode.length() == 4) {
					btnLogin.doClick();
				}
			}
		});
		GridBagConstraints gbc_textLoginCode = new GridBagConstraints();
		gbc_textLoginCode.fill = GridBagConstraints.HORIZONTAL;
		gbc_textLoginCode.insets = new Insets(0, 0, 0, 5);
		gbc_textLoginCode.gridx = 3;
		gbc_textLoginCode.gridy = 1;
		panel_2.add(textLoginCode, gbc_textLoginCode);
		textLoginCode.setColumns(10);

		lblLoginCodeImg = new JLabel("点击刷新");
		lblLoginCodeImg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ("登录成功".equals(lblLoginCodeImg.getText())) {
					return;
				}
				refreshLoginCodeImage();
			}
		});
		GridBagConstraints gbc_lblLoginCodeImg = new GridBagConstraints();
		gbc_lblLoginCodeImg.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblLoginCodeImg.insets = new Insets(0, 0, 0, 5);
		gbc_lblLoginCodeImg.gridx = 4;
		gbc_lblLoginCodeImg.gridy = 1;
		panel_2.add(lblLoginCodeImg, gbc_lblLoginCodeImg);
		lblLoginCodeImg.setToolTipText("点击刷新");

		btnLogin = new JButton("登录");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String loginUser = textLoginUser.getText();
				String loginPasswd = new String(textLoginPasswd.getPassword());
				String loginCode = textLoginCode.getText();
				if (StringUtils.isBlank(loginUser) || StringUtils.isBlank(loginPasswd)
						|| StringUtils.isBlank(loginCode)) {
					JOptionPane.showMessageDialog(container, "登录信息不完整，请重新输入");
					return;
				}
				btnLogin.setText("登录中...");
				if (httpClientService.Login(loginUser, loginPasswd, loginCode, cookieData)) {
					btnLogin.setText("登录");
					btnLogin.setEnabled(false);
					lblLoginCodeImg.setIcon(null);
					lblLoginCodeImg.setEnabled(false);
					lblLoginCodeImg.setText("登录成功");
					lblLoginCodeImg.setToolTipText("");
				} else {
					btnLogin.setText("登录");
					JOptionPane.showMessageDialog(container, "登录失败,请重试");
					refreshLoginCodeImage();
				}
			}
		});
		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.insets = new Insets(0, 0, 0, 5);
		gbc_btnLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLogin.gridx = 5;
		gbc_btnLogin.gridy = 1;
		panel_2.add(btnLogin, gbc_btnLogin);

		panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 0;
		panel.add(panel_3, gbc_panel_3);
		panel_3.setLayout(new GridLayout(0, 5, 0, 0));

		label1 = new JLabel("车次席别1");
		panel_3.add(label1);

		label2 = new JLabel("车次席别2");
		panel_3.add(label2);

		label3 = new JLabel("车次席别3");
		panel_3.add(label3);

		label4 = new JLabel("车次席别4");
		panel_3.add(label4);

		label5 = new JLabel("车次席别5");
		panel_3.add(label5);

		textTrainNo1 = new JTextField();
		textTrainNo1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textTrainNo1.setText(textTrainNo1.getText().toUpperCase());
			}
		});
		panel_3.add(textTrainNo1);
		textTrainNo1.setColumns(10);

		textTrainNo2 = new JTextField();
		textTrainNo2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textTrainNo2.setText(textTrainNo2.getText().toUpperCase());
			}
		});
		panel_3.add(textTrainNo2);
		textTrainNo2.setColumns(10);

		textTrainNo3 = new JTextField();
		textTrainNo3.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textTrainNo3.setText(textTrainNo3.getText().toUpperCase());
			}
		});
		textTrainNo3.setColumns(10);
		panel_3.add(textTrainNo3);

		textTrainNo4 = new JTextField();
		textTrainNo4.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textTrainNo4.setText(textTrainNo4.getText().toUpperCase());
			}
		});
		textTrainNo4.setColumns(10);
		panel_3.add(textTrainNo4);

		textTrainNo5 = new JTextField();
		textTrainNo5.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textTrainNo5.setText(textTrainNo5.getText().toUpperCase());
			}
		});
		textTrainNo5.setColumns(10);
		panel_3.add(textTrainNo5);

		panel_5 = new JPanel();
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.insets = new Insets(0, 0, 0, 5);
		gbc_panel_5.gridx = 0;
		gbc_panel_5.gridy = 1;
		panel.add(panel_5, gbc_panel_5);
		GridBagLayout gbl_panel_5 = new GridBagLayout();
		gbl_panel_5.columnWidths = new int[] { 50, 403, 0, 0 };
		gbl_panel_5.rowHeights = new int[] { 0, 0 };
		gbl_panel_5.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_5.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_5.setLayout(gbl_panel_5);

		lblNewLabel = new JLabel("");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_5.add(lblNewLabel, gbc_lblNewLabel);

		textCookie = new JTextArea();
		textCookie.setEditable(false);
		GridBagConstraints gbc_textCookie = new GridBagConstraints();
		gbc_textCookie.insets = new Insets(0, 0, 0, 5);
		gbc_textCookie.fill = GridBagConstraints.BOTH;
		gbc_textCookie.gridx = 1;
		gbc_textCookie.gridy = 0;
		panel_5.add(textCookie, gbc_textCookie);
		textCookie.setColumns(10);

		panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 5);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 1;
		gbc_panel_4.gridy = 1;
		panel.add(panel_4, gbc_panel_4);
		panel_4.setLayout(new GridLayout(0, 5, 0, 0));

		comboSeatType1 = new JComboBox();
		panel_4.add(comboSeatType1);
		comboSeatType1.setModel(new DefaultComboBoxModel(SeatType.values()));
		comboSeatType1.setSelectedIndex(6);

		comboSeatType2 = new JComboBox();
		panel_4.add(comboSeatType2);
		comboSeatType2.setModel(new DefaultComboBoxModel(SeatType.values()));
		comboSeatType2.setSelectedIndex(6);

		comboSeatType3 = new JComboBox();
		comboSeatType3.setModel(new DefaultComboBoxModel(SeatType.values()));
		comboSeatType3.setSelectedIndex(6);
		panel_4.add(comboSeatType3);

		comboSeatType4 = new JComboBox();
		comboSeatType4.setModel(new DefaultComboBoxModel(SeatType.values()));
		comboSeatType4.setSelectedIndex(6);
		panel_4.add(comboSeatType4);

		comboSeatType5 = new JComboBox();
		comboSeatType5.setModel(new DefaultComboBoxModel(SeatType.values()));
		comboSeatType5.setSelectedIndex(6);
		panel_4.add(comboSeatType5);
	}

	public UserPanel(TicketMainFrame container) {
		this();
		this.container = container;
		refreshLoginCodeImage();
	}

	public UserPanel(TicketMainFrame container, boolean append) {
		this(container);
		if (append) {
			btnLineOP.setText("-");
		}
	}

	private void refreshLoginCodeImage() {
		if (StringUtils.isBlank(textCookie.getText())) {
			// 初始化Cookie
			cookieData = httpClientService.initCookie();
			textCookie.setText(StringUtils.join(cookieData.entrySet(), "\r\n"));
		}

		textLoginCode.setText("");
		ImageIcon icon = new ImageIcon(httpClientService.buildLoginCodeImage(cookieData).getAbsolutePath());
		icon.setImage(icon.getImage().getScaledInstance(icon.getIconWidth(), icon.getIconHeight(), Image.SCALE_DEFAULT));
		lblLoginCodeImg.setIcon(icon);
		lblLoginCodeImg.setText("");
		lblLoginCodeImg.setToolTipText("点击刷新");
	}

	public UserData bindUItoModel() {
		logger.debug("Binding UI data for user: {}", textLoginUser.getText());
		UserData userData = new UserData();
		userData.setLoginUser(textLoginUser.getText());
		if ("登录成功".equals(lblLoginCodeImg.getText())) {
			userData.setLoginSuccess(true);
		} else {
			userData.setLoginSuccess(false);
		}
		userData.setCookieData(cookieData);
		List<TrainData> trainDatas = userData.getTrainDatas();
		if (StringUtils.isNotBlank(textTrainNo1.getText())) {
			trainDatas.add(new TrainData(textTrainNo1.getText(), (SeatType) comboSeatType1.getSelectedItem()));
		}
		if (StringUtils.isNotBlank(textTrainNo2.getText())) {
			trainDatas.add(new TrainData(textTrainNo2.getText(), (SeatType) comboSeatType2.getSelectedItem()));
		}
		if (StringUtils.isNotBlank(textTrainNo3.getText())) {
			trainDatas.add(new TrainData(textTrainNo3.getText(), (SeatType) comboSeatType3.getSelectedItem()));
		}
		if (StringUtils.isNotBlank(textTrainNo4.getText())) {
			trainDatas.add(new TrainData(textTrainNo4.getText(), (SeatType) comboSeatType4.getSelectedItem()));
		}
		if (StringUtils.isNotBlank(textTrainNo5.getText())) {
			trainDatas.add(new TrainData(textTrainNo5.getText(), (SeatType) comboSeatType5.getSelectedItem()));
		}
		return userData;
	}

	public void bindModeltoUI(UserData userData, boolean passwdFocus) {
		logger.debug("Binding Model data for user: {}", userData.getLoginUser());
		textLoginUser.setText(userData.getLoginUser());

		List<TrainData> trainDatas = userData.getTrainDatas();
		if (trainDatas.size() > 0) {
			TrainData trainData = trainDatas.get(0);
			textTrainNo1.setText(trainData.getTrainNo());
			comboSeatType1.setSelectedItem(trainData.getSeatType());
		}
		if (trainDatas.size() > 1) {
			TrainData trainData = trainDatas.get(1);
			textTrainNo2.setText(trainData.getTrainNo());
			comboSeatType2.setSelectedItem(trainData.getSeatType());
		}
		if (trainDatas.size() > 2) {
			TrainData trainData = trainDatas.get(2);
			textTrainNo3.setText(trainData.getTrainNo());
			comboSeatType3.setSelectedItem(trainData.getSeatType());
		}
		if (trainDatas.size() > 3) {
			TrainData trainData = trainDatas.get(3);
			textTrainNo4.setText(trainData.getTrainNo());
			comboSeatType4.setSelectedItem(trainData.getSeatType());
		}
		if (trainDatas.size() > 4) {
			TrainData trainData = trainDatas.get(4);
			textTrainNo5.setText(trainData.getTrainNo());
			comboSeatType5.setSelectedItem(trainData.getSeatType());
		}

		if (passwdFocus) {
			textLoginPasswd.grabFocus();
		}
	}

	public void bindModeltoUI(UserData userData) {
		this.bindModeltoUI(userData, false);
	}
}
