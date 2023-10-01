package com.flightcomputer.mecc.service;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MainComputerService {

    @Autowired
    private UIService uiService;


    private KRPC krpc;

    private Connection connection;

    private SpaceCenter spaceCenter;

    private SpaceCenter.Vessel activeVessel;

    private SpaceCenter.Flight flight;

    private double initialFuel, initialMass;

    private volatile boolean writeSpeedsActive = true; // Flag to control the loop

    public void initiateFlightComputer(Connection connection) {
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
//            this.activeVessel.getAutoPilot().targetPitchAndHeading(90, 90);
//            this.activeVessel.getAutoPilot().engage();
//            this.activeVessel.getControl().setThrottle(1);
            this.initialFuel = this.activeVessel.getResources().amount("LiquidFuel");
            this.initialMass = this.activeVessel.getMass();
            this.flight = this.activeVessel.flight(this.activeVessel.getOrbit().getBody().getNonRotatingReferenceFrame());
            startWriting();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void startWriting() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            try {
//                if (this.flight.getVerticalSpeed() < 0) {
//                    executorService.shutdown();
//                    return;
//                }
                this.addTexts();
            } catch (RPCException e) {
                System.out.println(e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void addTexts() throws RPCException {
        Double verticalSpeed = this.flight.getVerticalSpeed();
        Double horizontalSpeed = this.activeVessel.flight(this.activeVessel.getOrbit().getBody().getReferenceFrame()).getSpeed();
        Double surfaceAltitude = this.flight.getSurfaceAltitude();
        Float dynamicPressure = this.flight.getDynamicPressure();
        Float staticPressure = this.flight.getStaticPressure();
        Float staticAirTemperature = this.flight.getStaticAirTemperature();
        Float pitch = this.flight.getPitch();
        Float roll = this.flight.getRoll();
        Float heading = this.flight.getHeading();
        Double latitude = this.flight.getLatitude();
        Double longitude = this.flight.getLongitude();
        Float liquidFuel = this.activeVessel.getResources().amount("LiquidFuel");
        Float thrust = this.activeVessel.getThrust();
        Float maxThrust = this.activeVessel.getMaxThrust();
        Float mass = this.activeVessel.getMass();
        Float twr = thrust / mass;
        uiService.addText(String.format("V Speed: %.2f m/s", verticalSpeed), 1);
        uiService.addText(String.format("H Speed: %.2f m/s | %.1f", horizontalSpeed, normalize(horizontalSpeed, 2280.0)), 2);
        uiService.addText(String.format("Alt: %.2f km | %.1f", surfaceAltitude / 1000, normalize(surfaceAltitude, 70000)), 3);
        uiService.addText(String.format("D Press: %.2f kQ | %.1f", dynamicPressure / 1000, normalize(dynamicPressure, 30397.5)), 4);
        uiService.addText(String.format("S Press: %.2f kQ | %.1f", staticPressure / 1000, normalize(staticPressure, 101325.0)), 5);
        uiService.addText(String.format("Temp: %.2f K | %.1f", staticAirTemperature, normalize(staticAirTemperature, 319.0)), 6);
        uiService.addText(String.format("Pitch: %.1f ° | %.1f", pitch, normalizeNegative(pitch, 90, -90)), 7);
        uiService.addText(String.format("Roll: %.1f ° | %.1f", roll, normalizeNegative(roll, 180, -180)), 8);
        uiService.addText(String.format("Heading: %.1f ° | %.1f", heading, normalize(heading, 360.0)), 9);
        uiService.addText(String.format("Lat: %.3f ° | %.1f", latitude, normalizeNegative(latitude, 90, -90)), 10);
        uiService.addText(String.format("Long: %.3f ° | %.1f", longitude, normalizeNegative(longitude, 180, -180)), 11);
        uiService.addText(String.format("Fuel: %.1f L | %.1f", liquidFuel, normalize(liquidFuel, this.initialFuel)), 12);
        uiService.addText(String.format("Thrust: %.1f kN | %.1f", thrust / 1000, normalize(thrust, maxThrust)), 13);
        uiService.addText(String.format("Weight: %.1f Tons | %.1f", mass / 1000, normalizeNegative(mass, this.initialMass, this.activeVessel.getDryMass())), 14);
        uiService.addText(String.format("TWR: %.1f | %.1f", twr, normalize(twr, (maxThrust / this.activeVessel.getDryMass()))), 15);
        uiService.addText("Target Altitude: 70 km", 16);
    }

    private Float normalize(Number number, Number max) {
        Float n = new BigDecimal(number.toString()).floatValue();
        Float ma = new BigDecimal(max.toString()).floatValue();
        if (ma == 0) return 0.0F;
        return n / ma;
    }

    private Float normalizeNegative(Number number, Number max, Number min) {
        Float n = new BigDecimal(number.toString()).floatValue();
        Float mi = new BigDecimal(min.toString()).floatValue();
        Float ma = new BigDecimal(max.toString()).floatValue();

        return (n - mi) / (ma - mi) * 2 - 1;

    }
}
