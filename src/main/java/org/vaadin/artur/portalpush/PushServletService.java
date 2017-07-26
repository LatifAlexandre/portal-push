package org.vaadin.artur.portalpush;

import java.util.List;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletService;

public class PushServletService extends VaadinServletService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6374868224495456560L;
	private static String serviceName = null;

	public PushServletService(PushServletAlex servlet, DeploymentConfiguration deploymentConfiguration) throws ServiceException {
		super(servlet, deploymentConfiguration);
		System.out.println("PushServletService.enclosing_method()");
	}

	@Override
	public String getServiceName() {
		// Must match portlet to use same VaadinSession
		System.out.println("PushServletService.getServiceName() " + serviceName);
		return serviceName;
	}

	@Override
	protected List<RequestHandler> createRequestHandlers() throws ServiceException {
		System.out.println("PushServletService.createRequestHandlers()");
		List<RequestHandler> requestHandlers = super.createRequestHandlers();
		requestHandlers.add(new PushRequestHandler(this));
		return requestHandlers;
	}

	@Override
	protected boolean requestCanCreateSession(VaadinRequest request) {
		// Ensure a portlet session is created, not a servlet session.
		// This is just a precaution.
		System.out.println("PushServletService.requestCanCreateSession()");
		return false;
	}

	public static void setServiceName(String serviceName) {
		System.out.println("PushServletService.setServiceName() " + serviceName);
		PushServletService.serviceName = serviceName;
	}
}
