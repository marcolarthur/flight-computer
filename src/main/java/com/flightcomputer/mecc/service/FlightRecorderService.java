package com.flightcomputer.mecc.service;

import com.flightcomputer.mecc.dto.InputsDTO;
import com.flightcomputer.mecc.dto.OutputsDTO;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightRecorderService {
    private static final String CSV_FILE_PATH = "training_data.csv";

    public void recordData(InputsDTO inputsDTO, OutputsDTO outputsDTO) {
        List<String> inputData = getInputData(inputsDTO);
        List<String> outputData = getOutputData(outputsDTO);

        try {
            FileWriter writer = new FileWriter(CSV_FILE_PATH, true); // Append to the existing file
            CSVWriter csvWriter = new CSVWriter(writer);
            List<String> combinedData = new ArrayList<>(inputData);
            combinedData.addAll(outputData);
            csvWriter.writeNext(combinedData.toArray(new String[0]));
            csvWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<String> getOutputData(OutputsDTO outputsDTO) {
        List<String> outputData = new ArrayList<>();
        outputData.add(String.valueOf(outputsDTO.getPitch()));
        outputData.add(String.valueOf(outputsDTO.getRoll()));
        outputData.add(String.valueOf(outputsDTO.getYaw()));
        outputData.add(String.valueOf(outputsDTO.getThrust()));
        outputData.add(String.valueOf(outputsDTO.getStage()/8));
        return outputData;
    }

    private static List<String> getInputData(InputsDTO inputsDTO) {
        List<String> inputData = new ArrayList<>();
        inputData.add(String.valueOf(inputsDTO.getHorizontalSpeed()));
        inputData.add(String.valueOf(inputsDTO.getSurfaceAltitude()));
        inputData.add(String.valueOf(inputsDTO.getDynamicPressure()));
        inputData.add(String.valueOf(inputsDTO.getStaticPressure()));
        inputData.add(String.valueOf(inputsDTO.getStaticAirTemperature()));
        inputData.add(String.valueOf(inputsDTO.getPitch()));
        inputData.add(String.valueOf(inputsDTO.getRoll()));
        inputData.add(String.valueOf(inputsDTO.getHeading()));
        inputData.add(String.valueOf(inputsDTO.getLatitude()));
        inputData.add(String.valueOf(inputsDTO.getLongitude()));
        inputData.add(String.valueOf(inputsDTO.getLiquidFuel()));
        inputData.add(String.valueOf(inputsDTO.getThrust()));
        inputData.add(String.valueOf(inputsDTO.getMass()));
        inputData.add(String.valueOf(inputsDTO.getTwr()));
        return inputData;
    }
}
