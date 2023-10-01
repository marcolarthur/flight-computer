package com.flightcomputer.mecc;

import com.flightcomputer.mecc.service.MainComputerService;
import krpc.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static com.flightcomputer.mecc.service.ConnectionService.getConnection;

@SpringBootApplication
public class Mecc {
    @Autowired
    private MainComputerService mainComputerService;

    public static void main(String[] args) {
        SpringApplication.run(Mecc.class, args);
    }

    @PostConstruct
    private void initialize() {
        startConnection();
    }

    private void startConnection() {
        try {
            Connection connection = getConnection();
            mainComputerService.initiateFlightComputer(connection);
        } catch (java.net.ConnectException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



