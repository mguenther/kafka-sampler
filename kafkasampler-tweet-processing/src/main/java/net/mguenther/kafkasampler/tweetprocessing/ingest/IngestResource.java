package net.mguenther.kafkasampler.tweetprocessing.ingest;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Path("/ingests")
public class IngestResource {

    private final IngestManager ingestManager;
    private final LinkScheme linkScheme;

    @Inject
    public IngestResource(final IngestManager ingestManager, final LinkScheme linkScheme) {
        this.ingestManager = ingestManager;
        this.linkScheme = linkScheme;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listOfActiveFeeds() {
        final IngestOverview activeIngests = new IngestOverview(ingestManager.activeIngests());
        return Response.ok(activeIngests).build();
    }

    @POST
    public Response activate(@QueryParam("keywords") final List<String> keywords) {
        final Ingest ingest = ingestManager.feed(keywords);
        final URI locationOfIngest = linkScheme.toIngest(ingest.getIngestId());
        return Response.created(locationOfIngest).build();
    }

    @DELETE
    @Path("/{ingestId}")
    public Response deactivate(@PathParam("ingestId") final String ingestId) {
        ingestManager.cancel(ingestId);
        return Response.noContent().build();
    }
}
