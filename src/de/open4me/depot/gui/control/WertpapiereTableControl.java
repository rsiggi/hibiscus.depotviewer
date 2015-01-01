package de.open4me.depot.gui.control;

import java.rmi.RemoteException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.open4me.depot.Settings;
import de.open4me.depot.gui.action.AddWertpapierAction;
import de.open4me.depot.gui.action.OrderList;
import de.open4me.depot.gui.action.WertpapiereAktualisierenAction;
import de.open4me.depot.gui.menu.WertpapierMenu;
import de.open4me.depot.sql.GenericObjectSQL;
import de.open4me.depot.sql.SQLQueries;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.TablePart;

public class WertpapiereTableControl 
{

	private TablePart orderList;
	private WertpapiereControl controller;

	public WertpapiereTableControl() {
	}

	private TablePart getTable() {
		if (orderList != null) {
			return orderList;
		}

		List<GenericObjectSQL> list = SQLQueries.getWertpapiereMitKursdatum();

		orderList = new TablePart(list,new OrderList());
		orderList.addColumn(Settings.i18n().tr("wkn"),"wkn");
		orderList.addColumn(Settings.i18n().tr("ISIN"),"isin");
		orderList.addColumn(Settings.i18n().tr("Name"),"wertpapiername");
		orderList.addColumn(Settings.i18n().tr("Letzter Kurs"),"kursdatum");
		orderList.addSelectionListener(new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (orderList.getSelection() == null) {
					return; 
				}
				if (orderList.getSelection() instanceof Object[]) {
					controller.aktualisieren((GenericObjectSQL[]) orderList.getSelection());
				} else {
					GenericObjectSQL d = (GenericObjectSQL) event.data;
					controller.aktualisieren(d);
				}

			}

		});
		orderList.setMulti(true);
		orderList.setContextMenu(new WertpapierMenu(controller));
		return orderList;
	}

	public Composite getWepierControl(Composite comp) throws RemoteException
	{

		Composite rest = new Composite(comp ,SWT.BORDER);
		GridLayout grid1 = new GridLayout();
		grid1.numColumns = 1;
		rest.setLayout(grid1);


		getTable().paint(rest);

		ButtonArea buttons = new ButtonArea();

		buttons.addButton(new Button("Hinzufügen", new AddWertpapierAction()));
		buttons.addButton(new Button("Aktualisieren",new WertpapiereAktualisierenAction(controller, getTable())));
		
		buttons.paint(rest);
		return rest;
	}

	public void setController(WertpapiereControl controller) {
		this.controller = controller;
		
	}

	public void aktualisiere() throws RemoteException {
		getTable().removeAll();
		for (GenericObjectSQL x : SQLQueries.getWertpapiereMitKursdatum()) {
			getTable().addItem(x);
		}
	}
}