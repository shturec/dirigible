package org.eclipse.dirigible.runtime.registry.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.registry.processor.RegistryProcessor;

/**
 * Front facing REST service serving the raw repository content
 */
@Singleton
public class RegistryRestService implements IRestService {
	
	@Inject
	private RegistryProcessor processor;
	
	@GET
	@Path("/core/registry/{path:.*}")
	public Response getResource(@PathParam("path") String path) {
		IResource resource = processor.getResource(path);
		if (!resource.exists()) {
			ICollection collection = processor.getCollection(path);
			if (!collection.exists()) {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok().entity(processor.renderTree(collection)).type(ContentTypeHelper.APPLICATION_JSON).build();
		}
		if (resource.isBinary()) {
			return Response.ok().entity(resource.getContent()).type(resource.getContentType()).build();
		}
		return Response.ok(new String(resource.getContent())).type(resource.getContentType()).build();
	}
	
	@Override
	public Class<? extends IRestService> getType() {
		return RegistryRestService.class;
	}
}
