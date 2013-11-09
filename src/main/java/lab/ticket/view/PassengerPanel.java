/*
 * 12306-hunter: Java Swing C/S版本12306订票助手
 * 本程序完全开放源代码，仅作为技术学习交流之用，不得用于任何商业用途
 */
package lab.ticket.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import lab.ticket.TicketMainFrame;
import lab.ticket.model.PassengerData;
import lab.ticket.model.PassengerData.CardType;
import lab.ticket.model.PassengerData.TicketType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 乘客区域UI对象，由Eclipse WindowBuilder插件设计UI原型
 */
public class PassengerPanel extends JPanel {

	private static final long serialVersionUID = 676933411293955925L;

	private static final Logger logger = LoggerFactory.getLogger(PassengerPanel.class);

	private TicketMainFrame container;
	private PassengerPanel self = this;

	private JPanel panel_2;
	private JLabel label_6;
	private JLabel label_7;
	private JLabel label_8;
	private JLabel label_9;
	private JLabel label_14;
	private JComboBox comboTicketType;
	private JTextField textName;
	private JComboBox comboCardType;
	private JTextField textCardNo;
	private JTextField textMobile;
	private JButton btnLineOP;
	private JCheckBox chckbxSelect;
	private JLabel label;

	/**
	 * Create the panel.
	 */
	public PassengerPanel() {
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u4E58\u5BA2", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 286, 0 };
		gridBagLayout.rowHeights = new int[] { 47, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 71, 0, 89, 150, 80, 100, 72, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		label = new JLabel("选择");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 1;
		gbc_label.gridy = 0;
		panel_2.add(label, gbc_label);

		label_8 = new JLabel("证件类型");
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.insets = new Insets(0, 0, 5, 5);
		gbc_label_8.gridx = 2;
		gbc_label_8.gridy = 0;
		panel_2.add(label_8, gbc_label_8);

		label_9 = new JLabel("证件号码");
		GridBagConstraints gbc_label_9 = new GridBagConstraints();
		gbc_label_9.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_9.insets = new Insets(0, 0, 5, 5);
		gbc_label_9.gridx = 3;
		gbc_label_9.gridy = 0;
		panel_2.add(label_9, gbc_label_9);

		label_7 = new JLabel("姓名");
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_7.insets = new Insets(0, 0, 5, 5);
		gbc_label_7.gridx = 4;
		gbc_label_7.gridy = 0;
		panel_2.add(label_7, gbc_label_7);

		label_14 = new JLabel("手机号");
		GridBagConstraints gbc_label_14 = new GridBagConstraints();
		gbc_label_14.insets = new Insets(0, 0, 5, 5);
		gbc_label_14.gridx = 5;
		gbc_label_14.gridy = 0;
		panel_2.add(label_14, gbc_label_14);

		label_6 = new JLabel("票种");
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.insets = new Insets(0, 0, 5, 0);
		gbc_label_6.gridx = 6;
		gbc_label_6.gridy = 0;
		panel_2.add(label_6, gbc_label_6);

		btnLineOP = new JButton("+");
		btnLineOP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String op = btnLineOP.getText();
				if ("-".equals(op)) {
					container.removePassengerPanel(self);
				} else {
					container.addPassengerPanel();
				}

			}
		});
		GridBagConstraints gbc_btnLineOP = new GridBagConstraints();
		gbc_btnLineOP.insets = new Insets(0, 0, 0, 5);
		gbc_btnLineOP.gridx = 0;
		gbc_btnLineOP.gridy = 1;
		panel_2.add(btnLineOP, gbc_btnLineOP);

		chckbxSelect = new JCheckBox("");
		chckbxSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxSelect.isSelected()) {
					comboCardType.setEnabled(true);
					textCardNo.setEnabled(true);
					textMobile.setEnabled(true);
					textName.setEnabled(true);
					comboTicketType.setEnabled(true);
				} else {
					comboCardType.setEnabled(false);
					textCardNo.setEnabled(false);
					textMobile.setEnabled(false);
					textName.setEnabled(false);
					comboTicketType.setEnabled(false);
				}
			}
		});
		chckbxSelect.setSelected(true);
		GridBagConstraints gbc_chckbxSelect = new GridBagConstraints();
		gbc_chckbxSelect.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxSelect.gridx = 1;
		gbc_chckbxSelect.gridy = 1;
		panel_2.add(chckbxSelect, gbc_chckbxSelect);

		comboCardType = new JComboBox();
		comboCardType.setModel(new DefaultComboBoxModel(CardType.values()));
		GridBagConstraints gbc_comboCardType = new GridBagConstraints();
		gbc_comboCardType.insets = new Insets(0, 0, 0, 5);
		gbc_comboCardType.gridx = 2;
		gbc_comboCardType.gridy = 1;
		panel_2.add(comboCardType, gbc_comboCardType);

		textCardNo = new JTextField();
		GridBagConstraints gbc_textCardNo = new GridBagConstraints();
		gbc_textCardNo.fill = GridBagConstraints.HORIZONTAL;
		gbc_textCardNo.insets = new Insets(0, 0, 0, 5);
		gbc_textCardNo.gridx = 3;
		gbc_textCardNo.gridy = 1;
		panel_2.add(textCardNo, gbc_textCardNo);
		textCardNo.setColumns(10);

		textName = new JTextField();
		GridBagConstraints gbc_textName = new GridBagConstraints();
		gbc_textName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textName.insets = new Insets(0, 0, 0, 5);
		gbc_textName.gridx = 4;
		gbc_textName.gridy = 1;
		panel_2.add(textName, gbc_textName);
		textName.setColumns(10);

		textMobile = new JTextField();
		GridBagConstraints gbc_textMobile = new GridBagConstraints();
		gbc_textMobile.fill = GridBagConstraints.HORIZONTAL;
		gbc_textMobile.insets = new Insets(0, 0, 0, 5);
		gbc_textMobile.gridx = 5;
		gbc_textMobile.gridy = 1;
		panel_2.add(textMobile, gbc_textMobile);
		textMobile.setColumns(10);

		comboTicketType = new JComboBox();
		comboTicketType.setModel(new DefaultComboBoxModel(TicketType.values()));
		GridBagConstraints gbc_comboTicketType = new GridBagConstraints();
		gbc_comboTicketType.gridx = 6;
		gbc_comboTicketType.gridy = 1;
		panel_2.add(comboTicketType, gbc_comboTicketType);
	}

	public PassengerPanel(TicketMainFrame container) {
		this();
		this.container = container;
	}

	/**
	 * 追加模式构造UI对象
	 * @param container
	 * @param append
	 */
	public PassengerPanel(TicketMainFrame container, boolean append) {
		this(container);
		if (append) {
			btnLineOP.setText("-");
		}
	}

	/**
	 * 绑定UI数据到模型对象
	 * @return
	 */
	public PassengerData bindUItoModel() {
		logger.debug("Binding UI data for passenger: {}", textCardNo.getText());
		PassengerData passengerData = new PassengerData();
		if (chckbxSelect.isSelected()) {
			passengerData.setSelected(true);
		} else {
			passengerData.setSelected(false);
		}
		passengerData.setCardNo(textCardNo.getText());
		passengerData.setCardType((CardType) comboCardType.getSelectedItem());
		passengerData.setName(textName.getText());
		passengerData.setMobile(textMobile.getText());
		passengerData.setTicketType((TicketType) comboTicketType.getSelectedItem());
		return passengerData;
	}

	/**
	 * 绑定模型对象数据到UI
	 * @return
	 */
	public void bindModeltoUI(PassengerData passengerData) {
		logger.debug("Binding Model data for passenger: {}", passengerData.getCardNo());
		if (passengerData.isSelected()) {
			chckbxSelect.setSelected(true);
		} else {
			chckbxSelect.setSelected(false);
			comboCardType.setEnabled(false);
			textCardNo.setEnabled(false);
			textMobile.setEnabled(false);
			textName.setEnabled(false);
			comboTicketType.setEnabled(false);
		}
		textCardNo.setText(passengerData.getCardNo());
		comboCardType.setSelectedItem(passengerData.getCardType());
		textName.setText(passengerData.getName());
		textMobile.setText(passengerData.getMobile());
		comboTicketType.setSelectedItem(passengerData.getTicketType());
	}
}
