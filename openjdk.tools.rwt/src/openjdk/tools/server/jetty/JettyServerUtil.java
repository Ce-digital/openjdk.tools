package openjdk.tools.server.jetty;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.engine.RWTServletContextListener;

public class JettyServerUtil {
	
	public static Server newServletServer() throws IOException, URISyntaxException {
		return newServletServer(null, false, false, new String[] {});
	}
	
	public static Server newServletServer(String resources_path, boolean is_package_resources, boolean directories_listed) throws IOException, URISyntaxException {
		return newServletServer(resources_path, is_package_resources, directories_listed, new String[] {});
	}
	
	public static Server newRwtServer(Class<?> rwt_application_class) throws IOException, URISyntaxException {
		return newRwtServer(rwt_application_class, null, null);
	}
	
	public static Server newRwtServer(Class<?> rwt_application_class, String public_files_source_dir, String public_files_path) throws IOException, URISyntaxException {	
		Server server = new Server();
		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setContextPath("/");
        contextHandler.setBaseResource(new PathResource(Paths.get(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + UUID.randomUUID().toString())));
        contextHandler.setInitParameter("org.eclipse.rap.applicationConfiguration", rwt_application_class.getCanonicalName());
        contextHandler.setEventListeners(new RWTServletContextListener[]{ new RWTServletContextListener() });
        contextHandler.addServlet(RWTServlet.class, "/");
        contextHandler.addServlet(DefaultServlet.class, "/rwt-resources/*");        
        if(public_files_source_dir != null) {
        	ServletHolder holderHome = new ServletHolder("public", DefaultServlet.class);
	        holderHome.setInitParameter("resourceBase",public_files_source_dir);
	        holderHome.setInitParameter("dirAllowed","true");
	        holderHome.setInitParameter("pathInfoOnly","true");
	        contextHandler.addServlet(holderHome, public_files_path + "/*");
        }        
        HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { contextHandler });
		server.setHandler(handlers);
		return server;	
	}
	
	public static Server newServletServer(String resources_path, boolean is_package_resources, boolean directories_listed, String... welcome_files) throws IOException, URISyntaxException {	
		if(resources_path != null) {
			ResourceHandler resourceHandler = new ResourceHandler();
			resourceHandler.setDirectoriesListed(directories_listed);
			if (is_package_resources) {
				resourceHandler.setBaseResource(new PathResource(JettyServerUtil.class.getResource(resources_path)));
			} else {
				resourceHandler.setBaseResource(new PathResource(Paths.get(resources_path)));
			}
			if (welcome_files.length > 0) {
				resourceHandler.setWelcomeFiles(welcome_files);
			}
			Server server = new Server();
			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { resourceHandler, new ServletHandler() });
			server.setHandler(handlers);
			return server;
		}else {
			Server server = new Server();
			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { new ServletHandler() });
			server.setHandler(handlers);
			return server;
		}		
	}
	
	public static void startAndJoin(Server server) {
		Runnable runnable = new Runnable() {			
			@Override
			public void run() {
				try {
					server.start();
					server.join();
				} catch (Exception ex) {
					ex.printStackTrace();
				}		
			}
		};
		new Thread(runnable).start();
	}
	
	public static void attachServlet(Server server, Class<?> servlet_class, String path) {
		((ServletHandler) ((HandlerList) server.getHandler()).getHandlers()[((HandlerList) server.getHandler()).getHandlers().length-1]).addServletWithMapping(servlet_class.getName(), path);
	}
	
	public static Connector attachHttpsConnector(Server server, int port, String keystoreFilePath, String keystoreManagerPassword, String keystorePassword) {
		HttpConfiguration configuration = new HttpConfiguration();
		configuration.setSecureScheme("https");
		configuration.setSecurePort(port);
		configuration.setOutputBufferSize(32768);
		SslContextFactory factory = new SslContextFactory.Server();
        factory.setKeyStorePath(keystoreFilePath);
        factory.setKeyManagerPassword(keystoreManagerPassword);
        factory.setKeyStorePassword(keystorePassword);
        ServerConnector connector = new ServerConnector(server, new SslConnectionFactory(factory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(configuration));
        connector.setPort(port);
        connector.setIdleTimeout(500000);
        server.addConnector(connector);
        return connector;
	}
	
	public static Connector attachHttpConnector(Server server, int port) {
		HttpConfiguration configuration = new HttpConfiguration();
		configuration.setSecureScheme("http");
		configuration.setSecurePort(port);
		configuration.setOutputBufferSize(32768);
		ServerConnector connector = new ServerConnector(server,new HttpConnectionFactory(configuration));
        connector.setPort(port);
        connector.setIdleTimeout(500000);
        server.addConnector(connector);
        return connector;
	}
	
	public static class HttpServletIO{
		
		public static void write(HttpServletResponse response, String data) throws IOException {
			response.getWriter().println(data);
		}
		
		public static String read(HttpServletRequest request) throws IOException {
			String output = "";
	        String line = null;
	        while ((line = request.getReader().readLine()) != null) {
				output = output + line;
			}
			return output;
		}
	}
}
