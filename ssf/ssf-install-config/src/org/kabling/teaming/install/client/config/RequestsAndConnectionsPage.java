package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.widgets.GwValueSpinner;
import org.kabling.teaming.install.shared.RequestsAndConnections;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;

public class RequestsAndConnectionsPage extends ConfigPageDlgBox
{
	private GwValueSpinner maxThreadsSpinner;
	private GwValueSpinner maxIdleSpinner;
	private GwValueSpinner maxActiveSpinner;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		HTML titleDescLabel = new HTML(RBUNDLE.reqConnectionsPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		HTML descLabel = new HTML(RBUNDLE.reqConnectionsPageDesc());
		descLabel.addStyleName("configPageDescLabel");
		fPanel.add(descLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		contentPanel.addStyleName("reqConnectionsPageContent");

		FlexTable table = new FlexTable();
		contentPanel.add(table);

		int row = 0;
		{

			// Max Threads
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxThreadsColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			maxThreadsSpinner = new GwValueSpinner(200, 100, 500, RBUNDLE.default200());
			maxThreadsSpinner.getValSpinnerLabel().addStyleName("infoLabel");
			table.setWidget(row, 1, maxThreadsSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Max Active
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxActiveColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			maxActiveSpinner = new GwValueSpinner(50, 50, 100, RBUNDLE.default50());
			maxActiveSpinner.getValSpinnerLabel().addStyleName("infoLabel");
			table.setWidget(row, 1, maxActiveSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Max Active
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.maxIdleColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			maxIdleSpinner = new GwValueSpinner(20, 20, 100, RBUNDLE.default20());
			maxIdleSpinner.getValSpinnerLabel().addStyleName("infoLabel");
			table.setWidget(row, 1, maxIdleSpinner);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		//Nothing to validate here..Fill up the object and return
		RequestsAndConnections req = config.getRequestsAndConnections();
		req.setMaxActive(maxActiveSpinner.getValueAsInt());
		req.setMaxIdle(maxIdleSpinner.getValueAsInt());
		req.setMaxThreads(maxThreadsSpinner.getValueAsInt());
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
		RequestsAndConnections req = config.getRequestsAndConnections();
		if (req != null)
		{
			maxActiveSpinner.setValue(req.getMaxActive());
			maxIdleSpinner.setValue(req.getMaxIdle());
			maxThreadsSpinner.setValue(req.getMaxThreads());
		}
	}

}