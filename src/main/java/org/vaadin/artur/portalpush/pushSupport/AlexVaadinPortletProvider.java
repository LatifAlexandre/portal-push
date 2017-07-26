package org.vaadin.artur.portalpush.pushSupport;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import com.vaadin.osgi.resources.OsgiVaadinResources;
import com.vaadin.osgi.resources.VaadinResourceService;
import com.vaadin.ui.UI;

/**
 * Initializes a service tracker with {@link PortletUIServiceTrackerCustomizer} to track {@link UI} service registrations.
 * <p>
 * This only applies to Liferay Portal 7+ with OSGi support.
 *
 * @author Sampsa Sohlman
 *
 * @since 8.1
 */
@Component(immediate = true)
public class AlexVaadinPortletProvider {

	private ServiceTracker<UI, ServiceObjects<UI>> serviceTracker;
	private AlexPortletUIServiceTrackerCustomizer portletUIServiceTrackerCustomizer;

	@Activate
	void activate(ComponentContext componentContext) throws Exception {
		System.out.println("AlexVaadinPortletProvider.activate()");
		BundleContext bundleContext = componentContext.getBundleContext();
		VaadinResourceService service = OsgiVaadinResources.getService();

		portletUIServiceTrackerCustomizer = new AlexPortletUIServiceTrackerCustomizer(service);
		serviceTracker = new ServiceTracker<UI, ServiceObjects<UI>>(bundleContext, UI.class, portletUIServiceTrackerCustomizer);
		serviceTracker.open();
	}

	@Deactivate
	void deactivate() {
		if (serviceTracker != null) {
			System.out.println("AlexVaadinPortletProvider.deactivate()");
			serviceTracker.close();
			portletUIServiceTrackerCustomizer.cleanPortletRegistrations();
			portletUIServiceTrackerCustomizer = null;
		}

	}
}