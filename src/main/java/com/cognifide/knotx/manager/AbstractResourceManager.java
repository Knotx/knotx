package com.cognifide.knotx.manager;

import com.cognifide.knotx.repository.Repository;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.List;

public abstract class AbstractResourceManager<R, M> implements InitializingBean, ResourceLoaderAware {

	@Autowired
	private Unmarshaller unmarshaller;

	@Autowired
	protected ApplicationContext applicationContext;

	protected ResourceLoader resourceLoader;

	private List<R> resources;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		resources = initializeResourcesFromConfig();
		if (resources.isEmpty()) {
			throw new IllegalArgumentException("A list of AuthenticationProviders is required");
		}
	}

	public List<R> getManagedResource() {
		if (resources == null) {
			initializeResourcesFromConfig();
		}
		return resources;
	}

	@SuppressWarnings("unchecked")
	private M getMetadataFromResource(Resource authConfigResource) throws IOException {
		return (M) unmarshaller.unmarshal(new StreamSource(authConfigResource.getInputStream()));
	}

	private List<R> initializeResourcesFromConfig() {
		try {
			Resource authConfigResource = resourceLoader.getResource(getConfigurationPath());
			if (authConfigResource.isReadable()) {
				return extract(getMetadataFromResource(authConfigResource));
			} else {
				throw new BeanInstantiationException(Repository.class, "Error while creating repository chain");
			}
		} catch (IOException e) {
			throw new BeanInstantiationException(Repository.class, "Error while creating repository chain", e);
		}
	}

	protected abstract List<R> extract(M configurationMetadata);

	protected abstract String getConfigurationPath();

}
