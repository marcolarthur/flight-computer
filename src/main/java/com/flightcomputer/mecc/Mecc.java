package com.flightcomputer.mecc;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;

import java.io.IOException;

public class Mecc {
	public static void main(String[] args) throws IOException, RPCException {
		try {
			Connection connection = Connection.newInstance();
			KRPC krpc = KRPC.newInstance(connection);
			System.out.println("Connected to kRPC version " + krpc.getStatus().getVersion());
		}catch (java.net.ConnectException e){
			System.out.println(e.getMessage());
		}
	}
}