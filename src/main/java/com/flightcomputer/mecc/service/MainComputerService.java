package com.flightcomputer.mecc.service;

import com.flightcomputer.mecc.dto.InputsDTO;
import com.flightcomputer.mecc.dto.OutputsDTO;
import com.flightcomputer.mecc.model.InputsModel;
import com.flightcomputer.mecc.model.OutputsModel;
import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MainComputerService {

    @Autowired
    private UIService uiService;

    @Autowired
    private NeuralNetworkService neuralNetworkService;

    @Autowired
    private FlightRecorderService flightRecorderService;

    private KRPC krpc;

    private Connection connection;

    private SpaceCenter spaceCenter;

    private SpaceCenter.Vessel activeVessel;

    private SpaceCenter.Flight flight;

    private InputsModel inputs;

    private OutputsModel outputs;

    private double initialFuel, initialMass;

    private volatile boolean control = true;

    public void initiateFlightComputer(Connection connection) throws RPCException {
        this.connection = connection;
        this.krpc = KRPC.newInstance(connection);
        this.spaceCenter = SpaceCenter.newInstance(connection);
        uiService.createMainWindow(this.connection);
        try {
            this.activeVessel = spaceCenter.getActiveVessel();
            this.flight = this.activeVessel.flight(this.activeVessel.getReferenceFrame());
        } catch (RPCException e) {
            System.out.println(e.getMessage());
            this.activeVessel = null;
        }
        if (this.activeVessel != null) {
            takeControl();
        }
    }

    private void takeControl() {
        try {
            this.control = true;
            this.initialFuel = this.activeVessel.getResources().amount("LiquidFuel");
            this.initialMass = this.activeVessel.getMass();
            this.flight = this.activeVessel.flight(this.activeVessel.getOrbit().getBody().getNonRotatingReferenceFrame());
            this.inputs = new InputsModel(this.flight, this.activeVessel);
            this.outputs = new OutputsModel(this.activeVessel);
            this.activeVessel.getControl().activateNextStage();
            start();
            Thread.sleep(60000);
            releaseControl();
            reset();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            releaseControl();
            reset();
        }
    }

    private void reset() {
        try {
            this.spaceCenter.revertToLaunch();
            Thread.sleep(15000);
            this.takeControl();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void releaseControl() {
        this.control = false;
    }

    private void start() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            try {
                if (!control) {
                    executorService.shutdown();
                    return;
                }
                //recordFlight();
                fly();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void recordFlight() {
        this.inputs.update(this.flight, this.activeVessel);
        InputsDTO in = new InputsDTO(inputs, this.initialMass, this.initialFuel);
        this.outputs.update(activeVessel);
        OutputsDTO out = new OutputsDTO(new OutputsModel(this.activeVessel));
        System.out.println(out.getThrust());
        flightRecorderService.recordData(in, out);
    }

    private void fly() throws RPCException {
        this.inputs.update(this.flight, this.activeVessel);
        List<String> actions = this.predict();
        float pitch = Float.parseFloat(actions.get(0).replace("[", ""));
        float roll = Float.parseFloat(actions.get(1));
        float yaw = Float.parseFloat(actions.get(2));
        float throttle = Float.parseFloat(actions.get(3));
        float stage = Float.parseFloat(actions.get(4).replace("]", ""));
        this.activeVessel.getControl().setPitch(pitch);
        this.activeVessel.getControl().setRoll(roll);
        this.activeVessel.getControl().setYaw(yaw);
        this.activeVessel.getControl().setThrottle(throttle);
        System.out.println("\n\n");
        System.out.println("pitch: " + pitch);
        System.out.println("roll: " + roll);
        System.out.println("yaw: " + yaw);
        System.out.println("throttle: " + throttle);
    }

    private void addTexts() throws RPCException {
        this.inputs.update(this.flight, this.activeVessel);
        uiService.addText(String.format("V Speed: %.2f m/s", this.inputs.getVerticalSpeed()), 1);
        uiService.addText(String.format("H Speed: %.2f m/s | %.1f", this.inputs.getHorizontalSpeed(), this.inputs.getNormalHorizontalSpeed()), 2);
        uiService.addText(String.format("Alt: %.2f km | %.1f", this.inputs.getSurfaceAltitude() / 1000, this.inputs.getNormalSurfaceAltitude()), 3);
        uiService.addText(String.format("D Press: %.2f kQ | %.1f", this.inputs.getDynamicPressure() / 1000, this.inputs.getNormalDynamicPressure()), 4);
        uiService.addText(String.format("S Press: %.2f kQ | %.1f", this.inputs.getStaticPressure() / 1000, this.inputs.getNormalStaticPressure()), 5);
        uiService.addText(String.format("Temp: %.2f K | %.1f", this.inputs.getStaticAirTemperature(), this.inputs.getNormalStaticAirTemperature()), 6);
        uiService.addText(String.format("Pitch: %.1f ° | %.1f", this.inputs.getPitch(), this.inputs.getNormalPitch()), 7);
        uiService.addText(String.format("Roll: %.1f ° | %.1f", this.inputs.getRoll(), this.inputs.getNormalRoll()), 8);
        uiService.addText(String.format("Heading: %.1f ° | %.1f", this.inputs.getHeading(), this.inputs.getNormalHeading()), 9);
        uiService.addText(String.format("Lat: %.3f ° | %.1f", this.inputs.getLatitude(), this.inputs.getNormalLatitude()), 10);
        uiService.addText(String.format("Long: %.3f ° | %.1f", this.inputs.getLongitude(), this.inputs.getNormalLongitude()), 11);
        uiService.addText(String.format("Fuel: %.1f L | %.1f", this.inputs.getLiquidFuel(), this.inputs.getNormalLiquidFuel(this.initialFuel)), 12);
        uiService.addText(String.format("Thrust: %.1f kN | %.1f", this.inputs.getThrust() / 1000, this.inputs.getNormalThrust()), 13);
        uiService.addText(String.format("Weight: %.1f Tons | %.1f", this.inputs.getMass() / 1000, this.inputs.getNormalMass(this.initialMass)), 14);
        uiService.addText(String.format("TWR: %.1f | %.1f", this.inputs.getTwr(), this.inputs.getNormalTwr()), 15);
        uiService.addText("Target H Speed: 2280 m/s", 17);
        uiService.addText("Target Altitude: 70 km", 18);
    }

    private List<String> predict() {
        return neuralNetworkService.predict(new InputsDTO(this.inputs, this.initialMass, this.initialFuel));
    }

}
