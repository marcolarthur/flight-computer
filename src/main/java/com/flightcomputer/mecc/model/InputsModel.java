package com.flightcomputer.mecc.model;

import krpc.client.services.SpaceCenter;
import lombok.Getter;
import lombok.Setter;

import static com.flightcomputer.mecc.utils.Constants.*;
import static com.flightcomputer.mecc.utils.Normalizer.normalize;
import static com.flightcomputer.mecc.utils.Normalizer.normalizeNegative;

@Getter
@Setter
public class InputsModel {
    private double verticalSpeed;
    private double horizontalSpeed;
    private double surfaceAltitude;
    private float dynamicPressure;
    private float staticPressure;
    private float staticAirTemperature;
    private float pitch;
    private float roll;
    private float heading;
    private double latitude;
    private double longitude;
    private float liquidFuel;
    private float thrust;
    private float maxThrust;
    private float mass;
    private float dryMass;
    private float twr;

    public InputsModel(SpaceCenter.Flight flight, SpaceCenter.Vessel activeVessel) {
        try {
            this.verticalSpeed = flight.getVerticalSpeed();
            this.horizontalSpeed = activeVessel.flight(activeVessel.getOrbit().getBody().getReferenceFrame()).getSpeed();
            this.surfaceAltitude = flight.getSurfaceAltitude();
            this.dynamicPressure = flight.getDynamicPressure();
            this.staticPressure = flight.getStaticPressure();
            this.staticAirTemperature = flight.getStaticAirTemperature();
            this.pitch = flight.getPitch();
            this.roll = flight.getRoll();
            this.heading = flight.getHeading();
            this.latitude = flight.getLatitude();
            this.longitude = flight.getLongitude();
            this.liquidFuel = activeVessel.getResources().amount("LiquidFuel");
            this.thrust = activeVessel.getThrust();
            this.maxThrust = activeVessel.getMaxThrust();
            this.mass = activeVessel.getMass();
            this.dryMass = activeVessel.getDryMass();
            this.twr = thrust / mass;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(SpaceCenter.Flight flight, SpaceCenter.Vessel activeVessel) {
        try {
            this.verticalSpeed = flight.getVerticalSpeed();
            this.horizontalSpeed = activeVessel.flight(activeVessel.getOrbit().getBody().getReferenceFrame()).getSpeed();
            this.surfaceAltitude = flight.getSurfaceAltitude();
            this.dynamicPressure = flight.getDynamicPressure();
            this.staticPressure = flight.getStaticPressure();
            this.staticAirTemperature = flight.getStaticAirTemperature();
            this.pitch = flight.getPitch();
            this.roll = flight.getRoll();
            this.heading = flight.getHeading();
            this.latitude = flight.getLatitude();
            this.longitude = flight.getLongitude();
            this.liquidFuel = activeVessel.getResources().amount("LiquidFuel");
            this.thrust = activeVessel.getThrust();
            this.maxThrust = activeVessel.getMaxThrust();
            this.mass = activeVessel.getMass();
            this.dryMass = activeVessel.getDryMass();
            this.twr = thrust / mass;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public double getNormalHorizontalSpeed() {
        return normalize(horizontalSpeed, MAX_HORIZONTAL_SPEED);
    }

    public double getNormalSurfaceAltitude() {
        return normalize(surfaceAltitude, MAX_SURFACE_ALTITUDE);
    }

    public float getNormalDynamicPressure() {
        return normalize(dynamicPressure, MAX_DYNAMYC_PRESSURE);
    }

    public float getNormalStaticPressure() {
        return normalize(staticPressure, MAX_STATIC_PRESSURE);
    }

    public float getNormalStaticAirTemperature() {
        return normalize(staticAirTemperature, MAX_TEMPERATURE);
    }

    public float getNormalPitch() {
        return normalizeNegative(pitch, MAX_PITCH, MIN_PITCH);
    }

    public float getNormalRoll() {
        return normalizeNegative(roll, MAX_ROLL, MIN_ROLL);
    }

    public float getNormalHeading() {
        return normalize(heading, MAX_HEADING);
    }

    public double getNormalLatitude() {
        return normalizeNegative(latitude, MAX_LATITUDE, MIN_LATITUDE);
    }

    public double getNormalLongitude() {
        return normalizeNegative(longitude, MAX_LONGITUDE, MIN_LONGITUDE);
    }

    public float getNormalLiquidFuel(double initialFuel) {
        return normalize(liquidFuel, initialFuel);
    }

    public float getNormalThrust() {
        return normalize(thrust, maxThrust);
    }

    public float getNormalMass(double initialMass) {
        return normalizeNegative(mass, initialMass, dryMass);

    }

    public float getNormalTwr() {
        return normalize(twr, (maxThrust / dryMass));
    }
}
