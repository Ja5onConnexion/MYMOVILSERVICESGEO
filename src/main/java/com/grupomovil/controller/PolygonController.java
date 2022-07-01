package com.grupomovil.controller;

import com.grupomovil.entity.EBGm_polygon;
import com.grupomovil.entity.EBGm_zone;
import com.grupomovil.entity.EBGm_zone_parametrization;
import com.grupomovil.entity.geo.events.EBPeriodic;
import com.grupomovil.entity.ws.request.EBWs_Polygons;
import com.grupomovil.persistence.AccesDao;
import com.grupomovil.persistence.AccesDaoMongo;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;
import com.grupomovil.entity.geo.events.EBBaterias;
import com.grupomovil.tools.MBTools;
import java.util.List;
import javax.ws.rs.GET;

/**
 * @author jeisson.junco
 */
@Path("polygon")
public class PolygonController {

    private final AccesDao objAccesDao;
    public AccesDaoMongo objAccesDaoMongo;
    private final MBTools Tools;

    private PolygonController() {
        objAccesDao = AccesDao.getSingletonInstance();
        objAccesDaoMongo = new AccesDaoMongo();
        Tools = new MBTools();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("save")
    public Response save(String json) {
        return Response
                .status(Response.Status.OK)
                .entity(pruebas(json))
                .build();

    }

    private String pruebas(String json) {
        try {
            Gson g = new Gson();
            EBWs_Polygons p = g.fromJson(json, EBWs_Polygons.class);

            EBGm_zone zone = new EBGm_zone();
            Date now;
            zone.setGm_zon_dat(now = new Date());
            zone.setGm_zon_nam(p.getName());
            zone.setGm_zon_des(p.getDescription());
            zone.setGm_zon_sta("on".equals(p.getStatus_input()) ? 1 : 0);
            zone.setGm_zon_typ_id(p.getZone_type());

            if (objAccesDao.create("createZone", zone, 1).equals("OK")) {
                switch (zone.getGm_zon_typ_id()) {
                    case 1:
                        // Circle
                        EBGm_polygon polygon = new EBGm_polygon();
                        polygon.setGm_pol_lat(String.valueOf(p.getCoords().getCenter().getLat()));
                        polygon.setGm_pol_lon(String.valueOf(p.getCoords().getCenter().getLng()));
                        polygon.setGm_pol_rad(String.valueOf(p.getCoords().getRadius()));
                        polygon.setGm_zon_id(zone.getGm_zon_id());
                        polygon.setGm_pol_dat(now = new Date());
                        objAccesDao.create("createPolygon", polygon, 1);

                        break;

                    case 2:
                        // Triangle
                        for (EBWs_Polygons.Polygon pa : p.getPolygon()) {
                            EBGm_polygon poly1 = new EBGm_polygon();
                            poly1.setGm_pol_lat(String.valueOf(pa.getLat()));
                            poly1.setGm_pol_lon(String.valueOf(pa.getLng()));
                            poly1.setGm_zon_id(zone.getGm_zon_id());
                            poly1.setGm_pol_dat(now = new Date());
                            objAccesDao.create("createPolygon", poly1, 1);
                        }
                        break;
                    case 3:
                        // Polygon
                        for (EBWs_Polygons.Polygon pa : p.getPolygon()) {
                            EBGm_polygon poly2 = new EBGm_polygon();
                            poly2.setGm_pol_lat(String.valueOf(pa.getLat()));
                            poly2.setGm_pol_lon(String.valueOf(pa.getLng()));
                            poly2.setGm_zon_id(zone.getGm_zon_id());
                            poly2.setGm_pol_dat(now = new Date());
                            objAccesDao.create("createPolygon", poly2, 1);
                        }
                        break;

                    default:

                }
                EBGm_zone_parametrization para = new EBGm_zone_parametrization();
                para.setGm_var_id(Integer.parseInt(p.getVariable_input()));
                para.setGm_var_ran_id(Integer.parseInt(p.getRange_input()));
                para.setGm_zon_id(zone.getGm_zon_id());
                para.setGm_zon_par_val(p.getValue());
                objAccesDao.create("createZoneParametrization", para, 1);

            }
        } catch (ParseException | SQLException ex) {
            System.out.println(ex.getMessage());

        }
        return "OK";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("currentdata")
    public Response createcurrentPost(String json) {
        return Response
                .status(Response.Status.OK)
                .entity(createcurrent(json))
                .build();

    }

    private String createcurrent(String json) {
        EBPeriodic dataObj = new EBPeriodic();
        dataObj = new Gson().fromJson(json, EBPeriodic.class);
        return objAccesDaoMongo.updateDocuments(objAccesDaoMongo.getCollection("CURRENT_DATA", "telemetry_green"), dataObj, true);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("currentalarm")
    public Response currentalarmPost(String json) {
        return Response
                .status(Response.Status.OK)
                .entity(currentalarm(json))
                .build();

    }

    private String currentalarm(String json) {
        EBPeriodic dataObj = new EBPeriodic();
        EBPeriodic.Alarma data = dataObj.new Alarma();
        data = new Gson().fromJson(json, EBPeriodic.Alarma.class);
        dataObj.setAlarma(data);
        return objAccesDaoMongo.updateDocuments(objAccesDaoMongo.getCollection("CURRENT_DATA", "telemetry_green"), dataObj, false);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("currentevent")
    public Response currenteventPost(String json) {
        return Response
                .status(Response.Status.OK)
                .entity(currentevent(json))
                .build();

    }

    private String currentevent(String json) {
        EBPeriodic dataObj = new EBPeriodic();
        dataObj = new Gson().fromJson(json, EBPeriodic.class);
        return objAccesDaoMongo.updateDocuments(objAccesDaoMongo.getCollection("CURRENT_DATA", "telemetry_green"), dataObj, true);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("baterias")
    public Response getVehiclesInYard() {
        return Response
                .status(Response.Status.OK)
                .entity(findBateries())
                .build();
    }

    //Listar todas las solicitudes
    public List<EBBaterias> findBateries() {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        return objAccesDaoMongo.displayBaterias(objAccesDaoMongo.getCollection("CURRENT_DATA", "telemetry_green"));
    }

}
