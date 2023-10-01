package com.flightcomputer.mecc.service;

import com.flightcomputer.mecc.Mecc;
import krpc.client.Connection;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class ConnectionService {
    public static Connection getConnection() throws IOException {
        // Load the application properties
        Properties properties = new Properties();
        try (InputStream input = Mecc.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        }

        // Retrieve the connection details from the properties
        String host = properties.getProperty("krpc.host");
        int rpcPort = Integer.parseInt(properties.getProperty("krpc.rpc.port"));
        int streamPort = Integer.parseInt(properties.getProperty("krpc.stream.port"));

        // Create a connection to the kRPC server using the properties
        return Connection.newInstance("connection", host, rpcPort, streamPort);
    }

}
