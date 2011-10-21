package net.ideaconsult.portlet.toxpredict;

import net.ideaconsult.opentox.compound.CompoundPanel;

import com.vaadin.Application;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Toxpredict_portletApplication extends Application {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7087684353671148447L;
	protected ObjectProperty<String> searchObject = new ObjectProperty<String>("benzene",String.class);
	protected CompoundPanel compoundPanel;
	

	
	

	@Override
	public void init() {
		Window mainWindow = new Window("Toxpredict_portlet Application");
		
		final TextField searchField = new TextField();
		String hint = "Enter CAS, EINECS, SMILES, Name, InChI";
		searchField.setDescription(hint);
        searchField.setImmediate(true);
        searchField.setRequired(true);
        searchField.setInputPrompt("Search");
        searchField.setWidth("100%");
    	searchField.setPropertyDataSource(searchObject);
    	searchField.setInputPrompt(hint);
   
    	
    	com.vaadin.ui.VerticalLayout all = new com.vaadin.ui.VerticalLayout();
    	all.setSpacing(true);
    	all.setMargin(false);
    	mainWindow.setContent(all);

    	
    	HorizontalLayout top = new HorizontalLayout();
    	
		
		final NativeButton searchButton = new NativeButton("Search");
		searchButton.addListener(new Button.ClickListener() {
	        	@Override
	        	public void buttonClick(ClickEvent event) {
	        		try {
	        			searchField.setComponentError(null);
	        			compoundPanel.setValue(searchObject.getValue().toString().trim());
	        		} catch (Exception x) {
	        			searchField.setComponentError(new UserError(x.getMessage()));
	        		}
	        			
	        	}
	        });
		searchButton.setClickShortcut(KeyCode.ENTER);
		top.addComponent(searchField);
		top.addComponent(searchButton);
    	top.setWidth("100%");
		mainWindow.getContent().addComponent(top);

		compoundPanel = new CompoundPanel();
		compoundPanel.setSizeFull();
		mainWindow.getContent().addComponent(compoundPanel);

		setMainWindow(mainWindow);
	}


}
