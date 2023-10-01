package com.flightcomputer.mecc.service;

import com.flightcomputer.mecc.dto.InputsDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NeuralNetworkService {
    private MultiLayerNetwork neuralNetwork;

    public NeuralNetworkService() {
        // Define the configuration of your neural network
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(123)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(14)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.SIGMOID)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(5)
                        .nOut(5)
                        .activation(Activation.SIGMOID)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
                        .build())
                .backpropType(BackpropType.Standard)
                .build();

        neuralNetwork = new MultiLayerNetwork(configuration);
        neuralNetwork.init();
        this.trainNeuralNetworkFromCsv();
    }

    public List<String> predict(InputsDTO inputsDTO) {
        INDArray inputArray = convertInputsToINDArray(inputsDTO);

        INDArray output = neuralNetwork.output(inputArray);

        return convertOutputToList(output);
    }

    private INDArray convertInputsToINDArray(InputsDTO inputsDTO) {
        double[] inputData = new double[]{
                inputsDTO.getHorizontalSpeed(),
                inputsDTO.getSurfaceAltitude(),
                inputsDTO.getDynamicPressure(),
                inputsDTO.getStaticPressure(),
                inputsDTO.getStaticAirTemperature(),
                inputsDTO.getPitch(),
                inputsDTO.getRoll(),
                inputsDTO.getHeading(),
                inputsDTO.getLatitude(),
                inputsDTO.getLongitude(),
                inputsDTO.getLiquidFuel(),
                inputsDTO.getThrust(),
                inputsDTO.getMass(),
                inputsDTO.getTwr(),
        };

        return Nd4j.create(inputData, new int[]{1, inputData.length}, 'c');
    }

    private List<String> convertOutputToList(INDArray output) {
        return Arrays.asList(output.data().toString().split(","));
    }

    public void trainNeuralNetworkFromCsv() {
        try {
            File csvFile = ResourceUtils.getFile("training_data.csv");
            FileReader fileReader = new FileReader(csvFile);
            CSVParser csvParser = CSVFormat.DEFAULT.parse(fileReader);

            // Iterate through each record in the CSV file
            for (CSVRecord record : csvParser) {
                // Extract input and output data from the record
                List<String> inputDataStrings = new ArrayList<>();
                List<String> outputDataStrings = new ArrayList<>();

                // Assuming the input data is in columns 0 to 13, and output data is in columns 14 to 27
                for (int i = 0; i < 14; i++) {
                    inputDataStrings.add(record.get(i));
                }
                for (int i = 14; i < 19; i++) {
                    outputDataStrings.add(record.get(i));
                }

                // Convert the data to INDArray format
                INDArray inputArray = Nd4j.create(inputDataStrings.stream().mapToDouble(Double::parseDouble).toArray(), new int[]{1, 14}, 'c');
                INDArray outputArray = Nd4j.create(outputDataStrings.stream().mapToDouble(Double::parseDouble).toArray(), new int[]{1, 5}, 'c');

                // Train the neural network with this data
                neuralNetwork.fit(inputArray, outputArray);
            }

            // Close the CSV file reader
            fileReader.close();
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
    }
}
