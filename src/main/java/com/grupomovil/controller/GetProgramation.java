package com.grupomovil.controller;

import com.grupomovil.entity.EBGm_geo_rigel;
import com.grupomovil.persistence.AccesDao;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Jeisson.Junco
 *
 */
@Path("programation")
public class GetProgramation {

    private final AccesDao objAccesDao;

    private GetProgramation() {
        objAccesDao = AccesDao.getSingletonInstance();
    }

    /**
     * /service/programation/getProgramation
     *
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getProgramation")
    public Response programationVehicle(@QueryParam("date") String date, @QueryParam("hour") String hour, @QueryParam("vehicle") String vehicle) {

        return Response
                .status(Response.Status.OK)
                .entity(findProgramation(date, hour, vehicle))
                .build();
    }

    public List<EBGm_geo_rigel> findProgramation(String date, String hour, String vehicle) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        System.out.println("Inicia: " + name);
        EBGm_geo_rigel obj = new EBGm_geo_rigel();
        obj.setEv_date(date);
        obj.setHour(hour);
        obj.setVehicle(vehicle);
        List<EBGm_geo_rigel> data = null;
        
        try {
            data = (List<EBGm_geo_rigel>) objAccesDao.select("getProgramation", obj, 6);
        } catch (SQLException | ParseException e) {
            System.out.println("Error en " + name + ":" + e.getMessage());
        }
        System.out.println("Finaliza: " + name);
        return data;
    }

}