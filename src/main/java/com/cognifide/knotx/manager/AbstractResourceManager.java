/*
 * Knot.x - reactive microservice assembler
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import java.io.IOException;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

public abstract class AbstractResourceManager<R, M> implements InitializingBean, ResourceLoaderAware {

    @Autowired
    protected ApplicationContext applicationContext;
    protected ResourceLoader resourceLoader;
    @Autowired
    private Unmarshaller unmarshaller;
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
