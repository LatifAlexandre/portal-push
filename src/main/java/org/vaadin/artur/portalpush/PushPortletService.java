package org.vaadin.artur.portalpush;

import java.util.List;
import com.vaadin.osgi.liferay.OsgiUIProvider;
import com.vaadin.osgi.liferay.OsgiVaadinPortletService;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.communication.PortletUIInitHandler;

public class PushPortletService extends OsgiVaadinPortletService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 363173247320787986L;

	public PushPortletService(PushPortletAlex portlet, DeploymentConfiguration deploymentConfiguration, OsgiUIProvider provider)
			throws ServiceException {
		super(portlet, deploymentConfiguration, provider);
		System.out.println("PushPortletService.PushPortletService() " + getServiceName());
		PushServletService.setServiceName(getServiceName());
	}

	@Override
	protected List<RequestHandler> createRequestHandlers() throws ServiceException {
		System.out.println("PushPortletService.createRequestHandlers()");
		List<RequestHandler> list = super.createRequestHandlers();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof PortletUIInitHandler) {
				list.set(i, new PortalPushUIInitHandler());
			}
		}
		return list;
	}

}
