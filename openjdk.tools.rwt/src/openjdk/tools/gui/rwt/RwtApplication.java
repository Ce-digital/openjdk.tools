package openjdk.tools.gui.rwt;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public abstract class RwtApplication extends AbstractEntryPoint implements ApplicationConfiguration{
	private static final long serialVersionUID = 1L;
	
	public abstract String getTitle();
	public abstract String getIcon();
	public abstract String getStyleSheet();
	
	@Override
	public void configure(Application application) {
		application.setOperationMode(OperationMode.SWT_COMPATIBILITY);
		application.addResource("icon", new ResourceLoader() {
			
			@Override
			public InputStream getResourceAsStream(String arg0) throws IOException {
				return this.getClass().getClassLoader().getResourceAsStream(getIcon());
			}
		});
		application.addStyleSheet("application.styles", getStyleSheet());
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(WebClient.THEME_ID, "application.styles");
		properties.put(WebClient.PAGE_TITLE, getTitle());
		properties.put(WebClient.FAVICON, "icon");
		properties.put( WebClient.PAGE_OVERFLOW, "scrollY" );
		application.addEntryPoint("/", getClass(), properties);
	}

	@Override
	protected void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		initalize(parent);
	}
	
	public abstract void initalize(Composite parent);
	
	

	
}
