package com.flightcomputer.mecc.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.flightcomputer.mecc.model.OutputsModel;
import lombok.Getter;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public class OutputsDTO implements Serializable {
    private final double pitch;
    private final double roll;
    private final double yaw;
    private final float thrust;
    private final float stage;

    public OutputsDTO(OutputsModel outputsModel) {
        this.pitch = outputsModel.getPitch();
        this.roll = outputsModel.getRoll();
        this.yaw = outputsModel.getYaw();
        this.thrust = outputsModel.getThrust();
        this.stage = outputsModel.getStage();
    }
}
