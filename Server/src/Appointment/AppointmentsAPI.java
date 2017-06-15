package Appointment;

import Business.*;
import com.sun.org.apache.regexp.internal.RE;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/AppointmentsAPI")
public class AppointmentsAPI {

    @Context
    private UriInfo context;
    private Business bLayer;
    /**
     * Creates a new instance of Appointment.AppointmentsAPI
     */
    public AppointmentsAPI() {

        bLayer = new Business();
    }

    @Path("/")
    @GET
    @Produces("application/xml")
    public String helloWorld(){
        return "<result>API is up and runnning</result>";
    }


    @Path("/Services")
    @GET
    @Produces("application/xml")
    public Response wadlLink(){
        String url =this.context.getBaseUri()+"application.wadl";
        String doc = bLayer.wadlDoc(url);
        return this.responseGenerator(doc);
    }

    @Path("Appointments")
    @GET
    @Produces("application/xml")
    public Response getAllAppointments(){
        String url =this.context.getBaseUri().toString();
        String doc = bLayer.getAllAppointments(url);
        return this.responseGenerator(doc);
    }

    @Path("Appointments/{appointment}")
    @GET
    @Produces("application/xml")
    public Response getOneAppointment(@PathParam("appointment") String id){
        String url = this.context.getBaseUri().toString();
        String doc = bLayer.getOneAppointment(url,id);
        return this.responseGenerator(doc);
    }

    @Path("Appointments")
    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    public Response addAppointment(String xml){
        String url = this.context.getBaseUri().toString();
        System.out.println(xml);
        String doc = bLayer.addAppointment(xml,url);
        return this.responseGenerator(doc);
    }

    @Path("Appointments/{appointment}")
    @PUT
    @Produces("application/xml")
    @Consumes("application/xml")
    public Response getOneAppointment(@PathParam("appointment") String id,String xml){
        String url = this.context.getBaseUri().toString();
        String doc = bLayer.updateAppointment(xml,url,id);
        return this.responseGenerator(doc);
    }


    @Path("Patients")
    @GET
    @Produces("application/xml")
    public Response getAllPatients(){
        String url =this.context.getBaseUri().toString();
        String doc = bLayer.getAllPatients(url);
        return this.responseGenerator(doc);
    }

    @Path("Patients/{patient}")
    @GET
    @Produces("application/xml")
    public Response getOnePatient(@PathParam("patient") String id){
        String url = this.context.getBaseUri().toString();
        String doc = bLayer.getOnePatient(url,id);
        return this.responseGenerator(doc);
    }

    @Path("LabTests")
    @GET
    @Produces("application/xml")
    public Response getAllLabTests(){
        String url =this.context.getBaseUri().toString();
        String doc = bLayer.getAllLabTests(url);
        return this.responseGenerator(doc);
    }

    @Path("LabTests/{labtest}")
    @GET
    @Produces("application/xml")
    public Response getOneLabTest(@PathParam("labtest") String id){
        String url = this.context.getBaseUri().toString();
        String doc = bLayer.getOneLabTest(url,id);
        return this.responseGenerator(doc);
    }

    private Response responseGenerator(String doc){
        if(bLayer.isErrorFlag()) {
            switch (bLayer.getErrorNumber()) {
                case 1:
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(doc).build();
                case 2:
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(doc).build();
                case 3:
                    return Response.status(Response.Status.PRECONDITION_FAILED).entity(doc).build();
                case 4:
                    return Response.status(Response.Status.NOT_FOUND).entity(doc).build();
                case 5:
                    return Response.status(Response.Status.BAD_REQUEST).entity(doc).build();
            }
        }
        return Response.ok(doc, MediaType.APPLICATION_XML).build();
    }


}
