package net.ideaconsult.opentox.compound;


import java.lang.reflect.Method;

import com.vaadin.data.Item;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.Icon;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;

/**
 * Compound image
 * @author nina
 *
 */
public class ImagePanel extends Panel {
	private static final ThemeResource ICON_ZOOM = new ThemeResource("../opentox/images/zoom-32.png");
	public enum border_style {
		selected {
			@Override
			public String getStyleName() {
				return "bubble";
			}
		},
		unselected {
			@Override
			public String getStyleName() {
				return "borderless";
			}
		}
		;
		public abstract String getStyleName();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2271024777247764202L;
	protected Embedded embedded ;
	
	public Embedded getEmbedded() {
		return embedded;
	}
	public void setEmbedded(Embedded embedded) {
		this.embedded = embedded;
	}
	
	public ImagePanel() {
		this(250,250);
	}
	public ImagePanel(int w,int h) {
		this(w,h,false);
	}
	public ImagePanel(int w,int h, boolean zoom) {
		this(w,h,zoom,null);
	}
	public ImagePanel(int w,int h, boolean zoom, Icon zoomIcon) {
		super();
		VerticalLayout hl = new VerticalLayout();
		hl.setMargin(false);
		hl.setSpacing(false);
		setContent(hl);
		
		setSizeFull();
		embedded = new Embedded(null,new ExternalResource(""));
		
		embedded.setType(Embedded.TYPE_IMAGE);
		setStyleName(border_style.unselected.getStyleName());
		embedded.setWidth(String.format("%dpx",w));
		embedded.setHeight(String.format("%dpx",h));

		if (zoom) {
			HorizontalLayout l = new HorizontalLayout();
			//final PopupView pv = new PopupView("", new Label("Popup content"));
	

			PopupView.Content content = new PopupView.Content() {
				public String getMinimizedValueAsHTML() {
					return "";
				}

				public Component getPopupComponent() {

					ImagePanel img =  new ImagePanel(400,400,false);
					img.setItemDataSource(getData().toString());
					img.setWidth("400px");
					img.setHeight("400px");
					
					return img.embedded;
				}
				};
				final PopupView pv = new PopupView(content);

				Embedded icon = new Embedded();
				icon.setDescription("Structure diagram zoom");
				icon.setSource(ICON_ZOOM);
				icon.addListener(new ClickListener() {
				    @Override
				    public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
				        pv.setPopupVisible(true);
				    }
				});
				l.addComponent(icon);
				l.addComponent(pv);				

				hl.addComponent(l);
		}		
		hl.addComponent(embedded);
		hl.setComponentAlignment(embedded,Alignment.MIDDLE_CENTER);
		

	}
	protected String getImageURL(String url, int width, int height) {
	    return  String.format("%s?media=image/png&w=%d&h=%d",url,width,height);
		
		//return "http://apps.ideaconsult.net:8080/ambit2/images/logo.png";
	}
	public void setItemDataSource(String url) {
		try {
			if (url!= null) {
				ExternalResource resource = new ExternalResource(getImageURL(url,(int)embedded.getWidth(),(int)embedded.getHeight()));
				embedded.setSource(resource);
				embedded.setDescription(url.toString());
				embedded.setData(url.toString());
				setDescription(url.toString());
				setData(url.toString());
			} else {
				embedded.setSource(null);
				embedded.setDescription("NA");
				embedded.setData(null);
				setDescription("NA");
				setData(null);
				setVisible(false);
			}
				
		} catch (Exception x) {
			x.printStackTrace();
		}	
	}
	public void setItemDataSource(Object itemId, Item newDataSource) {
		if (itemId!=null) {
			setItemDataSource(itemId.toString());
			return;
		}
		try {
			Method m = newDataSource.getClass().getMethod(
	                "getUrl",
	                new Class[] {});
			Object url = m.invoke(newDataSource, new Object[] { });		
			setItemDataSource(url==null?null:url.toString());
				
		} catch (Exception x) {
			//x.printStackTrace();
		}	

	}
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		embedded.setVisible(visible);
	}
}
