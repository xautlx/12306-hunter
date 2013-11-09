/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import lab.ticket.model.SingleTrainOrderVO;
import lab.ticket.service.HttpClientService;
import lab.ticket.service.TicketUserThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 下单过程弹出的验证码输入对话框UI对象，由Eclipse WindowBuilder插件设计UI原型
 */
public class RandomCodeDialog extends JDialog {

	private static final long serialVersionUID = 7847244107255650518L;

	private static final Logger logger = LoggerFactory.getLogger(RandomCodeDialog.class);

	private HttpClientService httpClientService = new HttpClientService();

	private SingleTrainOrderVO singleTrainOrderVO;
	private TicketUserThread ticketUserThread;

	private final JPanel contentPanel = new JPanel();
	private JTextField textSubmitCode;
	private JLabel lblTrainNo;
	private JLabel lblSeatType;
	private JLabel lblSubmitCodeImg;
	private JLabel lblCount;
	private JLabel lblTrainDate;
	private JLabel lblLoginUser;
	private JButton btnSubmit;

	private JDialog dialog = this;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RandomCodeDialog dialog = new RandomCodeDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RandomCodeDialog() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				textSubmitCode.setText("");
				doSubmitCode();
			}
		});
		setAlwaysOnTop(true);
		setTitle("有票了，赶快输入下单验证码");
		setBounds(100, 100, 473, 349);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 81, 20, 157, 0, 0 };
			gbl_panel.rowHeights = new int[] { 50, 30, 30, 30, 30, 50, 32, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);
			{
				JLabel label = new JLabel("刷票次数：");
				label.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.anchor = GridBagConstraints.EAST;
				gbc_label.insets = new Insets(0, 0, 5, 5);
				gbc_label.gridx = 0;
				gbc_label.gridy = 0;
				panel.add(label, gbc_label);
			}
			{
				lblCount = new JLabel("");
				lblCount.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblCount = new GridBagConstraints();
				gbc_lblCount.fill = GridBagConstraints.HORIZONTAL;
				gbc_lblCount.insets = new Insets(0, 0, 5, 5);
				gbc_lblCount.gridx = 2;
				gbc_lblCount.gridy = 0;
				panel.add(lblCount, gbc_lblCount);
			}
			{
				JLabel label = new JLabel("登录账号：");
				label.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.insets = new Insets(0, 0, 5, 5);
				gbc_label.gridx = 0;
				gbc_label.gridy = 1;
				panel.add(label, gbc_label);
			}
			{
				lblLoginUser = new JLabel("");
				lblLoginUser.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblLoginUser = new GridBagConstraints();
				gbc_lblLoginUser.anchor = GridBagConstraints.WEST;
				gbc_lblLoginUser.insets = new Insets(0, 0, 5, 5);
				gbc_lblLoginUser.gridx = 2;
				gbc_lblLoginUser.gridy = 1;
				panel.add(lblLoginUser, gbc_lblLoginUser);
			}
			{
				JLabel label = new JLabel("乘车日期：");
				label.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.anchor = GridBagConstraints.EAST;
				gbc_label.insets = new Insets(0, 0, 5, 5);
				gbc_label.gridx = 0;
				gbc_label.gridy = 2;
				panel.add(label, gbc_label);
			}
			{
				lblTrainDate = new JLabel("");
				lblTrainDate.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblTrainDate = new GridBagConstraints();
				gbc_lblTrainDate.anchor = GridBagConstraints.WEST;
				gbc_lblTrainDate.insets = new Insets(0, 0, 5, 5);
				gbc_lblTrainDate.gridx = 2;
				gbc_lblTrainDate.gridy = 2;
				panel.add(lblTrainDate, gbc_lblTrainDate);
			}
			{
				JLabel lblNewLabel_1 = new JLabel("乘车车次：");
				lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
				gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
				gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel_1.gridx = 0;
				gbc_lblNewLabel_1.gridy = 3;
				panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
			}
			{
				lblTrainNo = new JLabel("");
				lblTrainNo.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblTrainNo = new GridBagConstraints();
				gbc_lblTrainNo.fill = GridBagConstraints.HORIZONTAL;
				gbc_lblTrainNo.insets = new Insets(0, 0, 5, 5);
				gbc_lblTrainNo.gridx = 2;
				gbc_lblTrainNo.gridy = 3;
				panel.add(lblTrainNo, gbc_lblTrainNo);
			}
			{
				JLabel lblNewLabel_3 = new JLabel("座位席别：");
				lblNewLabel_3.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
				gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
				gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel_3.gridx = 0;
				gbc_lblNewLabel_3.gridy = 4;
				panel.add(lblNewLabel_3, gbc_lblNewLabel_3);
			}
			{
				lblSeatType = new JLabel("");
				lblSeatType.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblSeatType = new GridBagConstraints();
				gbc_lblSeatType.fill = GridBagConstraints.HORIZONTAL;
				gbc_lblSeatType.insets = new Insets(0, 0, 5, 5);
				gbc_lblSeatType.gridx = 2;
				gbc_lblSeatType.gridy = 4;
				panel.add(lblSeatType, gbc_lblSeatType);
			}
			{
				JLabel lblNewLabel_5 = new JLabel("验证码：");
				lblNewLabel_5.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
				gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
				gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel_5.gridx = 0;
				gbc_lblNewLabel_5.gridy = 5;
				panel.add(lblNewLabel_5, gbc_lblNewLabel_5);
			}
			{
				textSubmitCode = new JTextField();
				GridBagConstraints gbc_textSubmitCode = new GridBagConstraints();
				gbc_textSubmitCode.fill = GridBagConstraints.HORIZONTAL;
				gbc_textSubmitCode.insets = new Insets(0, 0, 5, 5);
				gbc_textSubmitCode.gridx = 2;
				gbc_textSubmitCode.gridy = 5;
				panel.add(textSubmitCode, gbc_textSubmitCode);
				textSubmitCode.setColumns(10);
				textSubmitCode.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
						textSubmitCode.setText(textSubmitCode.getText().toUpperCase());
						String submitCode = textSubmitCode.getText();
						if (submitCode.length() == 4) {
							btnSubmit.doClick();
						}
					}
				});
			}
			{
				lblSubmitCodeImg = new JLabel("");
				lblSubmitCodeImg.setFont(new Font("宋体", Font.PLAIN, 14));
				lblSubmitCodeImg.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						refreshSubmitCodeImage();
					}
				});
				GridBagConstraints gbc_lblSubmitCodeImg = new GridBagConstraints();
				gbc_lblSubmitCodeImg.insets = new Insets(0, 0, 5, 0);
				gbc_lblSubmitCodeImg.anchor = GridBagConstraints.WEST;
				gbc_lblSubmitCodeImg.gridx = 3;
				gbc_lblSubmitCodeImg.gridy = 5;
				panel.add(lblSubmitCodeImg, gbc_lblSubmitCodeImg);
			}
			{
				JLabel lblNewLabel_2 = new JLabel("输入四位验证码后自动提交");
				lblNewLabel_2.setFont(new Font("宋体", Font.PLAIN, 14));
				GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
				gbc_lblNewLabel_2.fill = GridBagConstraints.HORIZONTAL;
				gbc_lblNewLabel_2.insets = new Insets(0, 0, 0, 5);
				gbc_lblNewLabel_2.gridx = 2;
				gbc_lblNewLabel_2.gridy = 6;
				panel.add(lblNewLabel_2, gbc_lblNewLabel_2);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnSubmit = new JButton("提交");
				btnSubmit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						doSubmitCode();
						dialog.setVisible(false);
						dialog.dispose();
					}
				});
				buttonPane.add(btnSubmit);
				getRootPane().setDefaultButton(btnSubmit);
			}
			{
				JButton cancelButton = new JButton("取消");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						textSubmitCode.setText("");
						doSubmitCode();
						dialog.setVisible(false);
						dialog.dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.NORTH);
			panel.setLayout(new GridLayout(0, 1, 0, 0));
			{
				JLabel lblNewLabel = new JLabel("刷到票了，赶快输入下单验证码，否则机会转瞬即逝");
				lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 18));
				lblNewLabel.setForeground(Color.RED);
				panel.add(lblNewLabel);
			}
		}

		textSubmitCode.grabFocus();

		setLocationByPlatform(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public RandomCodeDialog(SingleTrainOrderVO singleTrainOrderVO, TicketUserThread ticketUserThread) {
		this();
		this.singleTrainOrderVO = singleTrainOrderVO;
		this.ticketUserThread = ticketUserThread;

		lblTrainNo.setText(singleTrainOrderVO.getTrainNo());
		lblSeatType.setText(singleTrainOrderVO.getSeatType().toString());
		lblCount.setText(String.valueOf(ticketUserThread.getCount()));
		lblTrainDate.setText(singleTrainOrderVO.getTrainDate());
		lblLoginUser.setText(singleTrainOrderVO.getLoginUser());

		refreshSubmitCodeImage();
	}

	private void refreshSubmitCodeImage() {
		textSubmitCode.setText("");
		textSubmitCode.setEnabled(true);
		ImageIcon icon = new ImageIcon(httpClientService.buildSubmitCodeImage(singleTrainOrderVO.getCookieData())
				.getAbsolutePath());
		icon.setImage(icon.getImage().getScaledInstance(icon.getIconWidth(), icon.getIconHeight(), Image.SCALE_DEFAULT));
		lblSubmitCodeImg.setIcon(icon);
		lblSubmitCodeImg.setText("");
		lblSubmitCodeImg.setToolTipText("点击刷新");
	}

	private void doSubmitCode() {
		logger.debug("Notify parent thread...");
		if (ticketUserThread != null) {
			synchronized (ticketUserThread) {
				ticketUserThread.setSubmitCode(textSubmitCode.getText());
				ticketUserThread.notify();
			}
		}
	}
}
