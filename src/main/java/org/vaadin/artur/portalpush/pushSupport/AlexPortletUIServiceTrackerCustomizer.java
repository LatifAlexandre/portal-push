package org.vaadin.artur.portalpush.pushSupport;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.portlet.Portlet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.vaadin.artur.portalpush.PushPortletAlex;
import com.vaadin.osgi.liferay.OsgiUIProvider;
import com.vaadin.osgi.liferay.VaadinLiferayPortletConfiguration;
import com.vaadin.osgi.resources.VaadinResourceService;
import com.vaadin.ui.UI;

/**
 * Tracks {@link UI UIs} registered as OSGi services.
 *
 * <p>
 * If the {@link UI} is annotated with {@link VaadinLiferayPortletConfiguration}, a {@link Portlet} is created for it.
 * <p>
 * This only applies to Liferay Portal 7+ with OSGi support.
 *
 * @author Sampsa Sohlman
 *
 * @since 8.1
 */
class AlexPortletUIServiceTrackerCustomizer implements ServiceTrackerCustomizer<UI, ServiceObjects<UI>> {

	private static final String RESOURCE_PATH_PREFIX = "/o/%s";
	private static final String DISPLAY_CATEGORY = AlexPortletProperties.DISPLAY_CATEGORY;
	private static final String VAADIN_CATEGORY = "category.vaadin";

	private static final String PORTLET_NAME = AlexPortletProperties.PORTLET_NAME;
	private static final String DISPLAY_NAME = AlexPortletProperties.DISPLAY_NAME;
	private static final String PORTLET_SECURITY_ROLE = AlexPortletProperties.PORTLET_SECURITY_ROLE;
	private static final String VAADIN_RESOURCE_PATH = "javax.portlet.init-param.vaadin.resources.path";

	private Map<ServiceReference<UI>, ServiceRegistration<Portlet>> portletRegistrations = new HashMap<ServiceReference<UI>, ServiceRegistration<Portlet>>();
	private VaadinResourceService service;

	AlexPortletUIServiceTrackerCustomizer(VaadinResourceService service) {
		this.service = service;
	}

	@Override
	public ServiceObjects<UI> addingService(ServiceReference<UI> uiServiceReference) {
		System.out.println("AlexPortletUIServiceTrackerCustomizer.addingService()");
		Bundle bundle = uiServiceReference.getBundle();
		BundleContext bundleContext = bundle.getBundleContext();
		UI contributedUI = bundleContext.getService(uiServiceReference);

		try {
			Class<? extends UI> uiClass = contributedUI.getClass();
			VaadinLiferayPortletConfiguration portletConfiguration = uiClass.getAnnotation(VaadinLiferayPortletConfiguration.class);

			boolean isPushPortlet = uiServiceReference.getProperty(AlexPortletProperties.PORTLET_PUSH_PROPERTY) != null;
			if (isPushPortlet) {
				System.out.println("AlexPortletUIServiceTrackerCustomizer.addingService() - isPushPortlet : " + isPushPortlet);
				return registerPortlet(uiServiceReference, portletConfiguration);
			} else {
				System.out.println("AlexPortletUIServiceTrackerCustomizer.addingService() - isPushPortlet " + isPushPortlet);
				return null;
			}
		} finally {
			bundleContext.ungetService(uiServiceReference);
		}
	}

	private ServiceObjects<UI> registerPortlet(ServiceReference<UI> reference, VaadinLiferayPortletConfiguration configuration) {
		System.out.println("AlexPortletUIServiceTrackerCustomizer.registerPortlet()");
		Bundle bundle = reference.getBundle();
		BundleContext bundleContext = bundle.getBundleContext();

		ServiceObjects<UI> serviceObjects = bundleContext.getServiceObjects(reference);

		OsgiUIProvider uiProvider = new OsgiUIProvider(serviceObjects);

		Dictionary<String, Object> properties = null;
		if (configuration != null) {
			properties = createPortletProperties(uiProvider, reference, configuration);
		} else {
			properties = createPortletProperties(reference);
		}

		PushPortletAlex portlet = new PushPortletAlex(uiProvider);

		ServiceRegistration<Portlet> serviceRegistration = bundleContext.registerService(Portlet.class, portlet, properties);

		portletRegistrations.put(reference, serviceRegistration);

		return serviceObjects;
	}

	private Dictionary<String, Object> createPortletProperties(OsgiUIProvider uiProvider, ServiceReference<UI> reference,
			VaadinLiferayPortletConfiguration configuration) {
		System.out.println("AlexPortletUIServiceTrackerCustomizer.createPortletProperties()");
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		String category = configuration.category();
		if (category.trim().isEmpty()) {
			category = VAADIN_CATEGORY;
		}
		copyProperty(reference, properties, DISPLAY_CATEGORY, category);

		String portletName = configuration.name();
		if (portletName.trim().isEmpty()) {
			portletName = uiProvider.getDefaultPortletName();
		}

		String displayName = configuration.displayName();
		if (displayName.trim().isEmpty()) {
			displayName = uiProvider.getDefaultDisplayName();
		}

		copyProperty(reference, properties, PORTLET_NAME, portletName);
		copyProperty(reference, properties, DISPLAY_NAME, displayName);
		copyProperty(reference, properties, PORTLET_SECURITY_ROLE, configuration.securityRole());

		String resourcesPath = String.format(RESOURCE_PATH_PREFIX, service.getResourcePathPrefix());
		copyProperty(reference, properties, VAADIN_RESOURCE_PATH, resourcesPath);

		return properties;
	}

	private void copyProperty(ServiceReference<UI> serviceReference, Dictionary<String, Object> properties, String key, Object defaultValue) {
		System.out.println("AlexPortletUIServiceTrackerCustomizer.copyProperty()");
		Object value = serviceReference.getProperty(key);
		if (value != null) {
			properties.put(key, value);
		} else if (value == null && defaultValue != null) {
			properties.put(key, defaultValue);
		}
	}

	private Dictionary<String, Object> createPortletProperties(ServiceReference<UI> reference) {
		System.out.println("AlexPortletUIServiceTrackerCustomizer.createPortletProperties()");
		Hashtable<String, Object> properties = new Hashtable<>();
		for (String key : reference.getPropertyKeys()) {
			properties.put(key, reference.getProperty(key));
		}
		String resourcesPath = String.format(RESOURCE_PATH_PREFIX, service.getResourcePathPrefix());
		properties.put(VAADIN_RESOURCE_PATH, resourcesPath);

		return properties;
	}

	@Override
	public void modifiedService(ServiceReference<UI> serviceReference, ServiceObjects<UI> ui) {
		/*
		 * This service has been registered as a portlet at some point, otherwise it wouldn't be tracked.
		 *
		 * This handles changes for Portlet related properties that are part of the UI service to be passed to the Portlet service registration.
		 */
		System.out.println("AlexPortletUIServiceTrackerCustomizer.modifiedService()");
		Dictionary<String, Object> newProperties = createPortletProperties(serviceReference);
		ServiceRegistration<Portlet> registration = portletRegistrations.get(serviceReference);
		if (registration != null) {
			registration.setProperties(newProperties);
		}
	}

	@Override
	public void removedService(ServiceReference<UI> reference, ServiceObjects<UI> ui) {
		System.out.println("AlexPortletUIServiceTrackerCustomizer.removedService()");
		ServiceRegistration<Portlet> portletRegistration = portletRegistrations.get(reference);
		portletRegistrations.remove(reference);
		portletRegistration.unregister();
	}

	void cleanPortletRegistrations() {
		System.out.println("AlexPortletUIServiceTrackerCustomizer.cleanPortletRegistrations()");
		for (ServiceRegistration<Portlet> registration : portletRegistrations.values()) {
			registration.unregister();
		}
		portletRegistrations.clear();
		portletRegistrations = null;
	}
}