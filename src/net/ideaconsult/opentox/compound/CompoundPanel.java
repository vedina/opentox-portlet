package net.ideaconsult.opentox.compound;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import net.ideaconsult.opentox.tools.QuotedTokenizer;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class CompoundPanel extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3254886122165924698L;
	protected ComponentContainer dataHolder;
	protected Table identifiers;
	protected ImagePanel image;
	protected ComponentContainer form;
	protected String compoundURL;
	protected String modelURL = "http://apps.ideaconsult.net:8080/ambit2/model/2";
	
	public CompoundPanel() {
		super();
		setSizeFull();
		HorizontalSplitPanel hs = new HorizontalSplitPanel();
	    hs.setWidth("100%");
		hs.setHeight("48em");
		hs.setSplitPosition(50);
		
		VerticalLayout vs = new VerticalLayout();
		vs.setSizeFull();
		image = new ImagePanel(250,250,false);
		dataHolder = new VerticalLayout();
		dataHolder.setHeight("16em");
		dataHolder.setVisible(false);
		image.setVisible(false);
		vs.addComponent(dataHolder);
		vs.addComponent(image);
		
		hs.addComponent(vs);
		
		form = new Panel();
		//form.setStyleName("light");
		((Panel)form).setContent(new VerticalLayout());
		//form.setWidth("100%");
		//form.setHeight("48em");
		hs.addComponent(form);
		
		addComponent(hs);
		
	}
	
	protected void predictions(String compoundURL,String modelURL) throws Exception  {
		form.removeAllComponents();
		URL url = new URL(String.format("%s?feature_uris[]=%s/predicted",
				compoundURL, URLEncoder.encode(modelURL,"UTF-8")));
		Hashtable<String, String> values = read(url);
		Enumeration<String> keys = values.keys();
		String explanation = null;
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (_titles.Compound.toString().equals(key)) continue;
			if (key.indexOf("#explanation")>0) {
				explanation = values.get(key);
				continue;
			}
			Label label = new Label();
			label.setContentMode(Label.CONTENT_XHTML);
			label.setCaption(key);
			label.setValue(values.get(key));
			form.addComponent(label);
		}
		if (explanation != null) {
			Label label = new Label();
			label.setContentMode(Label.CONTENT_XHTML);
			label.setCaption("Explanation");
			label.setValue(explanation);
			form.addComponent(label);
		}
		form.setVisible(true);
	}
	
	protected Table createTable() {
		Table table = new Table();
		table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
		table.setSizeFull();
		table.addContainerProperty("Attribute", String.class,  null);
		table.addContainerProperty("Value",  Label.class,  null);

		table.setColumnExpandRatio("Attribute",1);
		table.setColumnExpandRatio("Value",4);
		return table;
	}
	
	public void setValue(String searchValue) throws Exception  {
		try {
			form.removeAllComponents();
			Hashtable<String, String> values = find(searchValue);
			image.setVisible(false);
			dataHolder.removeAllComponents();
			identifiers = createTable();
			
			dataHolder.addComponent(identifiers);
			
			for (_titles t : _titles.values()) {
				String value = values.get(t.name());
				if (value == null) continue;
				String[] v = null;
				switch (t) {
				case Compound: {
					compoundURL = value;
					image.setItemDataSource(value);
					image.setVisible(true);
					value = null;
					break;
				}
				case IUPACName: {
					v = value.split(";");
					break;
				}
				case ChemicalName: {
					v = value.split(";");
					break;
				
				}	        				
				case SMILES: {
					v = value.split(",");
					if (v.length>1) v = new String[] {v[0]};
					break;
				}
  				case InChI_std: {
  					v = value.split(",");
					if (v.length>1) v = new String[] {v[0]};
					break;
				}	 
  				case InChIKey_std: {
  					v = value.split(",");
					if (v.length>1) v = new String[] {v[0]};
					break;
				}		          				
				default : {
					v = value.split(";");
					break;
				}
				}
				
				if (value != null) {
					Label label = null;
					if ((v.length>1) || (v[0].length()>80)) {
						label = new Label();
						label.setValue(v[0].length()>80?v[0].substring(0,80):v[0]);
						label.setDescription(value);
					} 
					identifiers.addItem(new Object[] {t.getTitle(),label==null?v[0]:label}, t.ordinal());
				}
			}
			
			predictions(compoundURL, modelURL);
			dataHolder.setVisible(true);
		} catch (Exception x) {
			
			image.setItemDataSource(null);
			image.setVisible(false);
			dataHolder.setVisible(false);
			
			throw x;
		}
	}
	protected Hashtable<String, String> find(String value) throws Exception {
		URL url = new URL(String.format("http://apps.ideaconsult.net:8080/ambit2/query/compound/%s/all?max=1",
				URLEncoder.encode(value,"UTF-8")));
		return read(url);
	}
	protected Hashtable<String, String> read(URL url) throws Exception {
		InputStream in = null;
		String line = null;
		try {

			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			//uc.addRequestProperty("Accept","text/uri-list"); 
			uc.addRequestProperty("Accept","text/csv");
			uc.setDoOutput(true);
			uc.setRequestMethod("GET");
			int code = uc.getResponseCode();
			
			if (code==200) {
				in= uc.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				Hashtable<String, String> values = new Hashtable<String, String>();
				List<String> header = null;
				while ((line = reader.readLine())!=null) {
					List<String> v = new ArrayList<String>();
					
					QuotedTokenizer st = new QuotedTokenizer(line,',');
					while (st.hasMoreTokens()) v.add(st.nextToken().trim());
					
					if (header == null) header = v;
					else {
						for (int i=0; i < header.size();i++) 
							values.put(header.get(i).replace("http://www.opentox.org/api/1.1#", ""),v.get(i));
						return values;
					}
				}
			} else throw new Exception(uc.getResponseMessage());	
			
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (in != null) in.close(); } catch (Exception x) {}
		}
		throw new Exception("Not found");

	}
}
