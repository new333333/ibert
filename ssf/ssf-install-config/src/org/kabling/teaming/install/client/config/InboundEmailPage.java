package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.widgets.GwTextBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.shared.EmailSettings;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

public class InboundEmailPage extends ConfigPageDlgBox
{
	private GwTextBox smtpBindAddrTextBox;
	private GwValueSpinner smtpPortSpinner;
	private CheckBox enableSMTPServerCheckBox;
	private CheckBox announceTlsCheckBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		HTML titleDescLabel = new HTML(RBUNDLE.clusteringPageTitleDesc());
		titleDescLabel.addStyleName("inboundEmailTitleDescLabel");
		fPanel.add(titleDescLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("inboundPageContent");

		enableSMTPServerCheckBox = new CheckBox(RBUNDLE.enableInternalSMTPServer());
		contentPanel.add(enableSMTPServerCheckBox);
		
		FlexTable table = new FlexTable();
		table.addStyleName("inboundEmailTable");
		contentPanel.add(table);

		int row = 0;
		{

			// SMTP Bind Address
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.smtpBindAddressColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			smtpBindAddrTextBox = new GwTextBox();
			table.setWidget(row, 1, smtpBindAddrTextBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// SMTP Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.smtpPortColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			smtpPortSpinner = new GwValueSpinner(4446, 1024, 9999, null);
			table.setWidget(row, 1, smtpPortSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}
		
		{
			row++;
			//Announce TLS
			announceTlsCheckBox = new CheckBox(RBUNDLE.announceTLS());
			table.setWidget(row, 1, announceTlsCheckBox);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		EmailSettings emailSettings = config.getEmailSettings();
		
		emailSettings.setInternalInboundSMTPEnabled(enableSMTPServerCheckBox.getValue());
		emailSettings.setInternalInboundSMTPBindAddress(smtpBindAddrTextBox.getText());
		emailSettings.setInternalInboundSMTPPort(smtpPortSpinner.getValueAsInt());
		emailSettings.setInternalInboundSMTPTLSEnabld(announceTlsCheckBox.getValue());
		
		return config;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	@Override
	public void initUIWithData()
	{
		EmailSettings emailSettings = config.getEmailSettings();
		
		if (emailSettings != null)
		{
			enableSMTPServerCheckBox.setValue(emailSettings.isInternalInboundSMTPEnabled());
			announceTlsCheckBox.setValue(emailSettings.isInternalInboundSMTPTLSEnabld());
			smtpBindAddrTextBox.setText(emailSettings.getInternalInboundSMTPBindAddress());
			smtpPortSpinner.setValue(emailSettings.getInternalInboundSMTPPort());
		}
	}

}
