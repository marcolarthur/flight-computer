package com.flightcomputer.mecc.model;

import krpc.client.services.SpaceCenter;
import lombok.Getter;
import lombok.Setter;

import static com.flightcomputer.mecc.utils.Constants.*;
import static com.flightcomputer.mecc.utils.Normalizer.normalize;

@Getter
@Setter
public class OutputsModel {
    private double pitch;
    private double roll;
    private double yaw;
    private float thrust;
    private float stage;

    public OutputsModel(SpaceCenter.Vessel activeVessel) {
        try {
            this.pitch = activeVessel.getControl().getPitch();
            this.roll = activeVessel.getControl().getRoll();
            this.yaw = activeVessel.getControl().getYaw();
            this.thrust = activeVessel.getControl().getThrottle();
            this.stage = activeVessel.getControl().getCurrentStage();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(SpaceCenter.Vessel activeVessel) {
        try {
            this.pitch = activeVessel.getControl().getPitch();
            this.roll = activeVessel.getControl().getRoll();
            this.yaw = activeVessel.getControl().getYaw();
            this.thrust = activeVessel.getControl().getThrottle();
            this.stage = activeVessel.getControl().getCurrentStage();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
