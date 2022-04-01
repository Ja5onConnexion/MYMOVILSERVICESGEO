package com.grupomovil.controller;

import com.grupomovil.entity.EBGm_List_all_vehicles;
import com.grupomovil.entity.EBGm_Position_vehicle;
import com.grupomovil.entity.EBGm_Position_yard;
import com.grupomovil.entity.EBGm_Stopped_vehicle;
import com.grupomovil.entity.EBGm_Vehicles_yard;
import com.grupomovil.entity.EBPeriodic;
import com.grupomovil.persistence.AccesDaoMongo;
import com.grupomovil.tools.MBTools;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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
@Path("vehicle")
public class GetDataVehicle {

    private AccesDaoMongo objAccesDao;
    private final MBTools Tools;

    private GetDataVehicle() {
        //objAccesDao = AccesDao.getSingletonInstance();
        objAccesDao = new AccesDaoMongo();
        Tools = new MBTools();
    }

    /**
     * /service/vehicle/getPositionVehicle
     *
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getPositionVehicle")
    public Response positionVehicle(@QueryParam("vehicle") String vehicle) {

        return Response
                .status(Response.Status.OK)
                .entity(findVehicle(vehicle.replace("-", "")))
                .build();
    }

    public List<EBGm_Position_vehicle> findVehicle(String vehicle) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Date now = new Date();
        List<EBGm_Position_vehicle> data = new ArrayList<>();

        for (EBPeriodic p : objAccesDao.displayDocuments(objAccesDao.getCollection("CURRENT_DATA", "telemetry_green"))) {
            Object _nivelRestanteEnergia = (Objects.isNull(p.getNivelRestanteEnergia())) ? 0.0 : p.getNivelRestanteEnergia();
            if (Objects.nonNull(p.getIdVehiculo()) && p.getIdVehiculo().equals(vehicle)) {
                EBGm_Position_vehicle pos = new EBGm_Position_vehicle();
                pos.set_BatteryCharge(_nivelRestanteEnergia.toString());
                pos.set_Latitude(p.getLocalizacionVehiculo().getLatitud());
                pos.set_Longitude(p.getLocalizacionVehiculo().getLongitud());
                pos.set_VehicleTimestamp(p.getFechaHoraLecturaDato());
                data.add(pos);
            }
        }
        return data;
    }

    /**
     * /service/vehicle/getStoppedVehicle
     *
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getStoppedVehicle")
    public Response stoppedVehicle(@QueryParam("unit") Integer unit, @QueryParam("minutes") Integer minutes) {

        return Response
                .status(Response.Status.OK)
                .entity(findStoppedVehicle(unit, minutes))
                .build();
    }

    //Listar todas las solicitudes
    public List<EBGm_Stopped_vehicle> findStoppedVehicle(Integer unit, Integer minutes) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Date now = new Date();
        List<EBGm_Stopped_vehicle> data = new ArrayList<>();

        for (EBPeriodic p : objAccesDao.displayDocuments(objAccesDao.getCollection("CURRENT_DATA", "telemetry_green"))) {
            if (p.getFechaHoraLecturaDato() != null) {
                long diffTime = now.getTime() - Tools.formDateString(p.getFechaHoraLecturaDato(), 8).getTime();
                long diffMinutes = TimeUnit.MINUTES.convert(diffTime, TimeUnit.MILLISECONDS);
                Boolean _patio = (Objects.isNull(p.getPatio())) ? false : p.getPatio();
                if (unit < 1) {
                    if (diffMinutes >= minutes && _patio == false) {
                        EBGm_Stopped_vehicle stop = new EBGm_Stopped_vehicle();
                        stop.set_Status("Detenido");
                        stop.set_VehicleTimestamp(p.getFechaHoraLecturaDato());
                        stop.set_Vehicle(p.getIdVehiculo().substring(0, 3) + '-' + p.getIdVehiculo().substring(3));
                        data.add(stop);
                    }
                } else {
                    if (diffMinutes >= minutes && _patio == false && p.getUnidadFuncional() == unit) {
                        EBGm_Stopped_vehicle stop = new EBGm_Stopped_vehicle();
                        stop.set_Status("Detenido");
                        stop.set_VehicleTimestamp(p.getFechaHoraLecturaDato());
                        stop.set_Vehicle(p.getIdVehiculo().substring(0, 3) + '-' + p.getIdVehiculo().substring(3));
                        data.add(stop);
                    }
                }
            }
        }
        return data;
    }

    /**
     * /service/vehicle/getPositionYard
     *
     *
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getPositionYard")
    public Response listVehiclesInYard(@QueryParam("vehicle") String vehicle) {

        return Response
                .status(Response.Status.OK)
                .entity(findVehiclesInYard(vehicle.replace("-", "")))
                .build();

    }

    //Listar todas las solicitudes
    public List<EBGm_Position_yard> findVehiclesInYard(String vehicle) {

        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Date now = new Date();
        List<EBGm_Position_yard> data = new ArrayList<>();

        for (EBPeriodic p : objAccesDao.displayDocuments(objAccesDao.getCollection("CURRENT_DATA", "telemetry_green"))) {
            Boolean _patio = (Objects.isNull(p.getPatio())) ? false : p.getPatio();
            Object _nivelRestanteEnergia = (Objects.isNull(p.getNivelRestanteEnergia())) ? 0.0 : p.getNivelRestanteEnergia();
            EBGm_Position_yard posy = new EBGm_Position_yard();
            if (p.getIdVehiculo().equals(vehicle)) {
                if (_patio == false) {
                    posy.set_BatteryCharge(_nivelRestanteEnergia.toString());
                    posy.set_location("En vía");
                    posy.set_response(false);
                    posy.set_vehicle(p.getIdVehiculo().substring(0, 3) + '-' + p.getIdVehiculo().substring(3));
                    data.add(posy);
                } else {
                    posy.set_BatteryCharge(_nivelRestanteEnergia.toString());
                    posy.set_location("Patio Green Móvil");
                    posy.set_response(true);
                    posy.set_vehicle(p.getIdVehiculo().substring(0, 3) + '-' + p.getIdVehiculo().substring(3));
                    data.add(posy);
                }
            }
        }
        return data;
    }

    /**
     * /service/vehicle/getVehiclesInYard
     *
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getVehiclesInYard")
    public Response getVehiclesInYard(@QueryParam("yard") Integer yard) {

        return Response
                .status(Response.Status.OK)
                .entity(findVehiclesInYard(yard))
                .build();
    }

    //Listar todas las solicitudes
    public List<EBGm_Vehicles_yard> findVehiclesInYard(Integer yard) {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Date now = new Date();
        List<EBGm_Vehicles_yard> data = new ArrayList<>();

        for (EBPeriodic p : objAccesDao.displayDocuments(objAccesDao.getCollection("CURRENT_DATA", "telemetry_green"))) {
            Boolean _patio = (Objects.isNull(p.getPatio())) ? false : p.getPatio();
            if (yard.equals(10)) {
                if (_patio == true) {
                    EBGm_Vehicles_yard yar = new EBGm_Vehicles_yard();
                    yar.set_Latitude(p.getLocalizacionVehiculo().getLatitud());
                    yar.set_Longitude(p.getLocalizacionVehiculo().getLongitud());
                    yar.set_Vehicle(p.getIdVehiculo().substring(0, 3) + '-' + p.getIdVehiculo().substring(3));
                    yar.set_VehicleTimestamp(p.getFechaHoraLecturaDato());
                    data.add(yar);
                }
            }

        }
        return data;
    }

    /**
     * /service/vehicle/listAllVehiclesLocation
     *
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/listAllVehiclesLocation")
    public Response listAllVehiclesLocation() {

        return Response
                .status(Response.Status.OK)
                .entity(findAllVehiclesLocation())
                .build();
    }

    //Listar todas las solicitudes
    public List<EBGm_List_all_vehicles> findAllVehiclesLocation() {
        String name = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Date now = new Date();
        List<EBGm_List_all_vehicles> data = new ArrayList<>();

        for (EBPeriodic p : objAccesDao.displayDocuments(objAccesDao.getCollection("CURRENT_DATA", "telemetry_green"))) {
            Boolean _patio = (Objects.isNull(p.getPatio())) ? false : p.getPatio();
            Object _nivelRestanteEnergia = (Objects.isNull(p.getNivelRestanteEnergia())) ? 0.0 : p.getNivelRestanteEnergia();
            EBGm_List_all_vehicles lis = new EBGm_List_all_vehicles();
            if (_patio == false) {
                lis.set_BatteryCharge(_nivelRestanteEnergia.toString());
                lis.set_location("En vía");
                lis.set_response(false);
                lis.set_vehicle(p.getIdVehiculo().substring(0, 3) + '-' + p.getIdVehiculo().substring(3));
                data.add(lis);
            } else {
                lis.set_BatteryCharge(_nivelRestanteEnergia.toString());
                lis.set_location("Patio Green Móvil");
                lis.set_response(true);
                lis.set_vehicle(p.getIdVehiculo().substring(0, 3) + '-' + p.getIdVehiculo().substring(3));
                data.add(lis);
            }

        }
        return data;
    }
}
