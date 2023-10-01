package com.flightcomputer.mecc.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.flightcomputer.mecc.model.InputsModel;
import lombok.Getter;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class InputsDTO implements Serializable {
    private final double horizontalSpeed;
    private final double surfaceAltitude;
    private final float dynamicPressure;
    private final float staticPressure;
    private final float staticAirTemperature;
    private final float pitch;
    private final float roll;
    private final float heading;
    private final double latitude;
    private final double longitude;
    private final float liquidFuel;
    private final float thrust;
    private final float mass;
    private final float twr;

    public InputsDTO(InputsModel inputsModel, double initialMass, double initialFuel) {
        this.horizontalSpeed = inputsModel.getNormalHorizontalSpeed();
        this.surfaceAltitude = inputsModel.getNormalSurfaceAltitude();
        this.dynamicPressure = inputsModel.getNormalDynamicPressure();
        this.staticPressure = inputsModel.getNormalStaticPressure();
        this.staticAirTemperature = inputsModel.getNormalStaticAirTemperature();
        this.pitch = inputsModel.getNormalPitch();
        this.roll = inputsModel.getNormalRoll();
        this.heading = inputsModel.getNormalHeading();
        this.latitude = inputsModel.getNormalLatitude();
        this.longitude = inputsModel.getNormalLongitude();
        this.liquidFuel = inputsModel.getNormalLiquidFuel(initialFuel);
        this.thrust = inputsModel.getNormalThrust();
        this.mass = inputsModel.getNormalMass(initialMass);
        this.twr = inputsModel.getNormalTwr();
    }
}
