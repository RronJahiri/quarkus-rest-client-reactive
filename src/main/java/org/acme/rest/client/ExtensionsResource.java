package org.acme.rest.client;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Set;

@Path("/extension")
public class ExtensionsResource {

    @RestClient
    ExtensionsService extensionsService;

    @GET
    @Path("/id/{id}")
    @Blocking
    public Set<Extension> id(String id) {
        return extensionsService.getById(id);

    }


    @GET
    @Path("/string/{id}")
    @Blocking
    public String getString(String id) {
        return id;
    }

    @GET
    @Path("/uni-string/{id}")
    public Uni<String> getUniString(String id) {
        return Uni.createFrom().item(id);
    }

    @GET
    @Path("/uni-error-stirng/{id}")
    public Uni<String> getError(String id) {
        return Uni.createFrom().failure(new BaseException(422, "error ON " + id));
    }


    @GET
    @Path("/id-uni/{id}")
    public Uni<Set<Extension>> getByIdAsUni(String id) {
        return extensionsService.getByIdAsUni(id);
//        return Uni.createFrom().failure(new BaseException(422, "error ON " + id));
    }
}
