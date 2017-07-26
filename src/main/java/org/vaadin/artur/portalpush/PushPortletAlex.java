package org.vaadin.artur.portalpush;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import com.vaadin.osgi.liferay.OsgiUIProvider;
import com.vaadin.osgi.liferay.OsgiVaadinPortlet;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinPortletService;

public class PushPortletAlex extends OsgiVaadinPortlet {
	private static final long serialVersionUID = 1182087325994301534L;

	private OsgiUIProvider _provider = null;

	public PushPortletAlex(OsgiUIProvider provider) {
		super(provider);
		_provider = provider;
		System.out.println("PushPortletAlex.PushPortletAlex()");

	}

	@Override
	protected VaadinPortletService createPortletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
		System.out.println("PushPortlet.createPortletService()");
		PushPortletService service = new PushPortletService(this, deploymentConfiguration, _provider);
		service.init();
		return service;
	}

	@Override
	public void init(PortletConfig config) throws PortletException {
		System.out.println("PushPortlet.init()");
		super.init(config);

	}

	// @Override
	// public void init() throws PortletException {
	// // TODO Auto-generated method stub
	// System.out.println("PushPortlet.init()");
	// super.init();
	// }

	// @Override
	// protected void portletInitialized() throws PortletException {
	// // TODO Auto-generated method stub
	// System.out.println("PushPortletAlex.portletInitialized()");
	// super.portletInitialized();
	// }
	//
	// @Override
	// public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
	//
	// // TODO Auto-generated method stub
	// System.out.println("PushPortletAlex.render()");
	// super.render(request, response);
	// }
}
