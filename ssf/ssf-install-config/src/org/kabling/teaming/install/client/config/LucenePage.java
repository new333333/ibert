package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.ValueRequiredValidator;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.client.widgets.VibeTextBox;
import org.kabling.teaming.install.shared.Lucene;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * UI for setting up Lucene
 * 
 */
public class LucenePage extends ConfigPageDlgBox implements ClickHandler,
		ChangeHandler {
	private VibeTextBox hostAddrTextBox;
	private GwValueSpinner rmiPortSpinner;
	private GwValueSpinner maxBoolsSpinner;
	private GwValueSpinner mergeFactorSpinner;
	private ListBox configTypeListBox;
	private LuceneHighAvailabilityPanel haPanel;

	private static final String LOCAL = "local";
	private static final String SERVER = "server";
	private static final String HIGH_AVAILABILITY = "ha";

	@Override
	public Panel createContent(Object propertiesObj) {
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		// Title
		HTML titleDescLabel = new HTML(RBUNDLE.clusteringPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("lucenePageContent");

		int row = 0;
		FlexTable hostTable = new FlexTable();
		contentPanel.add(hostTable);

		{
			// Max Bools
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxBooleansColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			maxBoolsSpinner = new GwValueSpinner(10000, 10000, 12000,
					RBUNDLE.default10000());
			maxBoolsSpinner.getValSpinnerLabel().addStyleName("infoLabel");
			hostTable.setWidget(row, 1, maxBoolsSpinner);
			hostTable.getFlexCellFormatter()
					.addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Merge Factor
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.mergeFactorColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			mergeFactorSpinner = new GwValueSpinner(10, 10, 50,
					RBUNDLE.default10());
			mergeFactorSpinner.getValSpinnerLabel().addStyleName("infoLabel");
			hostTable.setWidget(row, 1, mergeFactorSpinner);
			hostTable.getFlexCellFormatter()
					.addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Configuration type
			InlineLabel keyLabel = new InlineLabel(
					RBUNDLE.configurationTypeColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			configTypeListBox = new ListBox(false);
			configTypeListBox.addChangeHandler(this);
			configTypeListBox.addItem(RBUNDLE.local(), LOCAL);
			configTypeListBox.addItem(RBUNDLE.server(), SERVER);
			configTypeListBox.addItem(RBUNDLE.highAvailablity(),
					HIGH_AVAILABILITY);
			hostTable.setWidget(row, 1, configTypeListBox);
			hostTable.getFlexCellFormatter()
					.addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Host Name or IP Address
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.hostNameColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			hostAddrTextBox = new VibeTextBox();
			hostAddrTextBox.setValidator(new ValueRequiredValidator(
					hostAddrTextBox));
			hostTable.setWidget(row, 1, hostAddrTextBox);
			hostTable.getFlexCellFormatter()
					.addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// RMI Port
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.rmiPortColon());
			hostTable.setWidget(row, 0, keyLabel);
			hostTable.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			rmiPortSpinner = new GwValueSpinner(1199, 1024, 9999,
					RBUNDLE.defaultIs1199());
			rmiPortSpinner.addStyleName("luceneConfigWizRmiSpinnerLabel");
			hostTable.setWidget(row, 1, rmiPortSpinner);
			hostTable.getFlexCellFormatter()
					.addStyleName(row, 1, "table-value");
		}

		{
			row++;

			// High Availability (hide by default)
			haPanel = new LuceneHighAvailabilityPanel();
			haPanel.setVisible(false);
			hostTable.setWidget(row, 1, haPanel);
			hostTable.getFlexCellFormatter()
					.addStyleName(row, 1, "table-value");
		}

		return fPanel;
	}

	private boolean isValid() {
		// Make sure the host name is not empty
		if (!hostAddrTextBox.isValid()) {
			setErrorMessage(RBUNDLE.allFieldsRequired());
			return false;
		}

		if (configTypeListBox.getSelectedIndex() == 1)
		{
			//Remote lucene server cannot point to the local box
			if (AppUtil.isLocalIpAddr(hostAddrTextBox.getText()))
			{
				setErrorMessage(RBUNDLE.remoteLuceneCannotPointToLocalBox());
				return false;
			}
		}
		
		// If they have selected high availability, let's make sure they have
		// atleast one search node
		if (configTypeListBox.getSelectedIndex() == 2) {
			if (haPanel.getAvailableNodes() == null
					|| haPanel.getAvailableNodes().size() == 0) {
				setErrorMessage(RBUNDLE.noAvailabilityNodesExists());
				return false;
			}
		}
		return true;

	}

	@Override
	public Object getDataFromDlg() {

		if (!isValid())
			return null;

		Lucene lucene = config.getLucene();
		lucene.setMaxBooleans(maxBoolsSpinner.getValueAsInt());
		lucene.setMergeFactor(mergeFactorSpinner.getValueAsInt());

		// Configuration type (local, server or ha)
		if (configTypeListBox.getSelectedIndex() == 0)
			lucene.setLocation(LOCAL);
		else if (configTypeListBox.getSelectedIndex() == 1)
			lucene.setLocation(SERVER);
		else
			lucene.setLocation(HIGH_AVAILABILITY);

		lucene.setIndexHostName(hostAddrTextBox.getText());
		lucene.setRmiPort(rmiPortSpinner.getValueAsInt());

		if (configTypeListBox.getSelectedIndex() == 2) {
			// Save high availability information
			lucene.setSearchNodesList(haPanel.getAvailableNodes());
		}
		
		GWT.log(lucene.toString());
		return config;
	}

	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}

	@Override
	public void initUIWithData() {
		Lucene lucene = config.getLucene();

		if (lucene != null) {
			maxBoolsSpinner.setValue(lucene.getMaxBooleans());
			mergeFactorSpinner.setValue(lucene.getMergeFactor());
			
			if (lucene.getLocation().equals("::luceneMode_Local")
					|| lucene.getLocation().equals(LOCAL)) {
				configTypeListBox.setSelectedIndex(0);
				showUIForLocalMode();
			} else if (lucene.getLocation().equals(SERVER)) {
				configTypeListBox.setSelectedIndex(1);
				hostAddrTextBox.setText(lucene.getIndexHostName());
				rmiPortSpinner.setValue(lucene.getRmiPort());
				showUIForServerMode();
			} else {
				configTypeListBox.setSelectedIndex(2);
				showUIForHighAvailabilityMode();
			}
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == configTypeListBox) {
			int index = configTypeListBox.getSelectedIndex();

			if (index == 0) // Local
			{
				showUIForLocalMode();
			} else if (index == 1) // Server
			{
				showUIForServerMode();
			} else {
				showUIForHighAvailabilityMode();
			}
		}
	}

	private void showUIForLocalMode() {
		hostAddrTextBox.setText("localhost");
		hostAddrTextBox.setEnabled(false);

		rmiPortSpinner.setValue(1199);
		rmiPortSpinner.setEnabled(false);

		haPanel.setVisible(false);
	}

	private void showUIForServerMode() {
		hostAddrTextBox.setText(config.getLucene().getIndexHostName());
		rmiPortSpinner.setValue(config.getLucene().getRmiPort());

		hostAddrTextBox.setEnabled(true);
		rmiPortSpinner.setEnabled(true);

		haPanel.setVisible(false);
	}

	private void showUIForHighAvailabilityMode() {
		hostAddrTextBox.setText(config.getLucene().getIndexHostName());
		rmiPortSpinner.setValue(config.getLucene().getRmiPort());

		haPanel.setVisible(true);
		haPanel.updateUI(config);
	}
}