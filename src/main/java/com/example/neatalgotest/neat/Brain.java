package com.example.neatalgotest.neat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mansitoh
 * Project: TradingView
 * Date: 11/5/2024 @ 00:14
 * Twitter: @Mansitoh_PY
 * Github: https://github.com/Mansitoh
 * Discord: discord.skilled-dev.club
 * Website: skilled-dev.club
 * CEO: Skilled | Development
 */
public class Brain {

    public static double ADD_NODE_CHANCE = 0.002;
    public static double REMOVE_NODE_CHANCE = 0.001;
    public static double ADD_CONNECTION_CHANCE = 0.001;
    public static double REMOVE_CONNECTION_CHANCE = 0.001;
    public static double MUTATE_WEIGHT_CHANCE = 0.8;

    public List<NodeGene> inputNodes;
    public List<NodeGene> outputNodes;
    public List<NodeGene> hiddenNodes;
    public List<ConnectionGene> connections;

    public Brain(int inputSize, int outputSize) {
        inputNodes = new ArrayList<>();
        outputNodes = new ArrayList<>();
        hiddenNodes = new ArrayList<>();
        connections = new ArrayList<>();

        for (int i = 0; i < inputSize; i++) {
            inputNodes.add(new InputNodeGene(i));
        }
        for (int i = 0; i < outputSize; i++) {
            outputNodes.add(new OutputNodeGene(i));
        }

        for (NodeGene inputNode : inputNodes) {
            for (NodeGene outputNode : outputNodes) {
                int innovation = NEATUtil.getCurrentInnovationForConnection(inputNode, outputNode);
                connections.add(new ConnectionGene(inputNode, outputNode, innovation));
            }
        }
    }

    public double[] feedForward(double[] inputs) {
        sortConnections();

        double[] outputs = new double[outputNodes.size()];
        if (inputs.length != inputNodes.size()) {
            throw new IllegalArgumentException("Input size does not match the number of input nodes");
        }

        for (int i = 0; i < inputNodes.size(); i++) {
            ((InputNodeGene) inputNodes.get(i)).setInputValue(inputs[i]);
        }

        for (int i = 0; i < outputNodes.size(); i++) {
            NodeGene outputNode = outputNodes.get(i);
            double output = 0;
            List<ConnectionGene> connectedToOutput = new ArrayList<>();
            for (ConnectionGene connection : connections) {
                if (connection.outNode == outputNode && connection.isEnabled()) {
                    connectedToOutput.add(connection);
                }
            }
            for (ConnectionGene connection : connectedToOutput) {
                output += connection.weight * connection.getInNode().getOutput(connections);
            }
            output = NEATUtil.sigmoid(output + outputNode.getBias());
            outputs[i] = output;
        }


        return outputs;
    }

    private void sortConnections() {
        connections.sort((o1, o2) -> Integer.compare(o1.innovation, o2.innovation));
        hiddenNodes.sort((o1, o2) -> Integer.compare(o1.getInnovationAsInteger(), o2.getInnovationAsInteger()));
        inputNodes.sort((o1, o2) -> Integer.compare(o1.getInnovationAsInteger(), o2.getInnovationAsInteger()));
        outputNodes.sort((o1, o2) -> Integer.compare(o1.getInnovationAsInteger(), o2.getInnovationAsInteger()));
    }

    public Brain crossover(Brain other) {
        Brain child = new Brain(inputNodes.size(), outputNodes.size());
        child.connections.clear();

        List<ConnectionGene> connectionsToAdd = new ArrayList<>();

        for (ConnectionGene connection : connections) {
            connectionsToAdd.add(connection.copy());
        }
        for (ConnectionGene otherConnection : other.connections) {
            ConnectionGene matchingConnection = connectionsToAdd.stream()
                    .filter(c -> c.innovation == otherConnection.innovation).findFirst().orElse(null);
            if (matchingConnection == null) {
                connectionsToAdd.add(otherConnection.copy());
            }
        }

        child.inputNodes.clear();
        child.outputNodes.clear();
        child.hiddenNodes.clear();

        for (ConnectionGene connection : connectionsToAdd) {
            NodeGene inNode = connection.getInNode();
            NodeGene outNode = connection.getOutNode();
            if (inNode.getNeuronType().equals(NeuronType.INPUT)) {
                NodeGene finalInNode1 = inNode;
                NodeGene matchingNode = child.inputNodes.stream().filter(n -> n.innovation.equalsIgnoreCase(finalInNode1.innovation)).findFirst().orElse(null);
                if (matchingNode == null) {
                    inNode = new InputNodeGene(inNode.getInnovationAsInteger());
                    child.inputNodes.add(inNode);
                }
            } else if (inNode.getNeuronType().equals(NeuronType.HIDDEN)) {
                NodeGene finalInNode = inNode;
                NodeGene matchingNode = child.hiddenNodes.stream().filter(n -> n.innovation.equalsIgnoreCase(finalInNode.innovation)).findFirst().orElse(null);
                if (matchingNode == null) {
                    inNode = new HiddenNodeGene(inNode.getInnovationAsInteger());
                    child.hiddenNodes.add(inNode);
                }
            } else if (inNode.getNeuronType().equals(NeuronType.OUTPUT)) {
                NodeGene finalInNode2 = inNode;
                NodeGene matchingNode = child.outputNodes.stream().filter(n -> n.innovation.equalsIgnoreCase(finalInNode2.innovation)).findFirst().orElse(null);
                if (matchingNode == null) {
                    inNode = new OutputNodeGene(inNode.getInnovationAsInteger());
                    child.outputNodes.add(inNode);
                }
            }

            if (outNode.getNeuronType().equals(NeuronType.INPUT)) {
                NodeGene finalOutNode1 = outNode;
                NodeGene matchingNode = child.inputNodes.stream().filter(n -> n.innovation.equalsIgnoreCase(finalOutNode1.innovation)).findFirst().orElse(null);
                if (matchingNode == null) {
                    outNode = new InputNodeGene(outNode.getInnovationAsInteger());
                    child.inputNodes.add(outNode);
                }
            } else if (outNode.getNeuronType().equals(NeuronType.HIDDEN)) {
                NodeGene finalOutNode = outNode;
                NodeGene matchingNode = child.hiddenNodes.stream().filter(n -> n.innovation.equalsIgnoreCase(finalOutNode.innovation)).findFirst().orElse(null);
                if (matchingNode == null) {
                    outNode = new HiddenNodeGene(outNode.getInnovationAsInteger());
                    child.hiddenNodes.add(outNode);
                }
            } else if (outNode.getNeuronType().equals(NeuronType.OUTPUT)) {
                NodeGene finalOutNode2 = outNode;
                NodeGene matchingNode = child.outputNodes.stream().filter(n -> n.innovation.equalsIgnoreCase(finalOutNode2.innovation)).findFirst().orElse(null);
                if (matchingNode == null) {
                    outNode = new OutputNodeGene(outNode.getInnovationAsInteger());
                    child.outputNodes.add(outNode);
                }
            }

            int innovation = NEATUtil.getCurrentInnovationForConnection(connection.getInNode(), connection.getOutNode());
            ConnectionGene newConnection = new ConnectionGene(inNode, outNode, connection.getWeight(), innovation);
            child.connections.add(newConnection);
        }

        child.sortConnections();
        sortConnections();
        return child;
    }

    public void addNode() {
        for (ConnectionGene connection : new ArrayList<>(connections)) {
            if (!connection.isEnabled()) continue;
            if (Math.random() < ADD_NODE_CHANCE) {
                NodeGene inputNode = connection.getInNode();
                NodeGene outputNode = connection.getOutNode();
                int newInnovation = NEATUtil.getCurrentInnovationForNode(inputNode, outputNode);
                HiddenNodeGene newNode = new HiddenNodeGene(newInnovation);
                hiddenNodes.add(newNode);
                int firstConnectionInnovation = NEATUtil.getCurrentInnovationForConnection(inputNode, newNode);
                ConnectionGene newConnection1 = new ConnectionGene(inputNode, newNode, firstConnectionInnovation);
                newConnection1.setWeight(1);

                int secondConnectionInnovation = NEATUtil.getCurrentInnovationForConnection(newNode, outputNode);
                ConnectionGene newConnection2 = new ConnectionGene(newNode, outputNode, secondConnectionInnovation);
                newConnection2.setWeight(connection.getWeight());
                connection.setEnabled(false);
                connections.add(newConnection1);
                connections.add(newConnection2);
            }
        }
    }

    public void removeNode() {
        for (NodeGene node : new ArrayList<>(hiddenNodes)) {
            if (Math.random() < REMOVE_NODE_CHANCE) {
                List<ConnectionGene> connectionsWithThisNodeAsInput = new ArrayList<>();
                List<ConnectionGene> connectionsWithThisNodeAsOutput = new ArrayList<>();
                for (ConnectionGene connection : connections) {
                    if (connection.getInNode() == node || connection.getOutNode() == node) {
                        connectionsWithThisNodeAsInput.add(connection);
                    }
                }
                for (ConnectionGene connection : connectionsWithThisNodeAsInput) {
                    connection.setEnabled(false);
                }
                hiddenNodes.remove(node);
            }
        }
    }

    public void removeConnection() {
        for (ConnectionGene connection : new ArrayList<>(connections)) {
            if (Math.random() < REMOVE_CONNECTION_CHANCE) {
                connection.setEnabled(false);
            }
        }
    }

    public void addConnection() {
        for (NodeGene node : new ArrayList<>(hiddenNodes)) {
            if (Math.random() < ADD_CONNECTION_CHANCE) {
                NodeGene randomNode = hiddenNodes.get((int) (Math.random() * hiddenNodes.size()));
                if (randomNode != node) {
                    ConnectionGene existingConnection = connections.stream()
                            .filter(c -> c.getInNode() == node && c.getOutNode() == randomNode).findFirst().orElse(null);
                    if (existingConnection == null) {
                        int currentInnovation = NEATUtil.getCurrentInnovationForConnection(node, randomNode);
                        ConnectionGene connection = new ConnectionGene(node, randomNode, currentInnovation);
                        connections.add(connection);
                    }
                }
            }
        }
    }

    public void mutateWeights() {
        for (ConnectionGene connection : connections) {
            if (Math.random() < MUTATE_WEIGHT_CHANCE) {
                connection.setWeight(connection.getWeight() + (Math.random() * 2 - 1) * 0.1);
            }
        }
    }

    public void mutateBias() {
        for (NodeGene node : hiddenNodes) {
            if (Math.random() < MUTATE_WEIGHT_CHANCE) {
                node.setBias(node.getBias() + (Math.random() * 2 - 1) * 0.1);
            }
        }
    }

    public void mutate() {
        addNode();
        //removeNode();
        //addConnection();
        //removeConnection();
        mutateWeights();
        mutateBias();
    }

    public Brain copy() {
        Brain brain = new Brain(inputNodes.size(), outputNodes.size());
        brain.connections.clear();
        brain.hiddenNodes.clear();
        brain.inputNodes.clear();
        brain.outputNodes.clear();
        for (ConnectionGene connection : connections) {
            brain.connections.add(connection.copy());
        }
        for (NodeGene node : hiddenNodes) {
            brain.hiddenNodes.add(node.copy());
        }
        for (NodeGene node : inputNodes) {
            brain.inputNodes.add(node.copy());
        }
        for (NodeGene node : outputNodes) {
            brain.outputNodes.add(node.copy());
        }
        brain.sortConnections();
        return brain;
    }

    public BufferedImage visualize() {
        int width = 1280;
        int height = 720;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        int diametersForNeurons = 10;
        int numbersOfInputNeurons = inputNodes.size();
        int numbersOfOutputNeurons = outputNodes.size();

        int inputNeuronSpace = height / (numbersOfInputNeurons + 1);
        HashMap<String, Point> neuronPositions = new HashMap<>();

        for (int i = 0; i < numbersOfInputNeurons; i++) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval(100, inputNeuronSpace * (i + 1), diametersForNeurons, diametersForNeurons);
            neuronPositions.put(inputNodes.get(i).innovation, new Point(100, inputNeuronSpace * (i + 1)));
            g2d.setColor(Color.BLUE);
            g2d.drawString(inputNodes.get(i).innovation, 20, inputNeuronSpace * (i + 1) + 10);
        }

        int outputNeuronSpace = height / (numbersOfOutputNeurons + 1);

        for (int i = 0; i < numbersOfOutputNeurons; i++) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval(width - 100, outputNeuronSpace * (i + 1), diametersForNeurons, diametersForNeurons);
            neuronPositions.put(outputNodes.get(i).innovation, new Point(width - 100, outputNeuronSpace * (i + 1)));
            g2d.setColor(Color.BLUE);
            g2d.drawString(outputNodes.get(i).innovation, width - 80, outputNeuronSpace * (i + 1) + 10);
        }

        int numberOfHiddenNeurons = hiddenNodes.size();
        int hiddenNeuronSpace = height / (numberOfHiddenNeurons + 1);

        for (int i = 0; i < numberOfHiddenNeurons; i++) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval(width / 2, hiddenNeuronSpace * (i + 1), diametersForNeurons, diametersForNeurons);
            neuronPositions.put(hiddenNodes.get(i).innovation, new Point(width / 2, hiddenNeuronSpace * (i + 1)));
            g2d.setColor(Color.BLUE);
            g2d.drawString(hiddenNodes.get(i).innovation, width / 2 + 20, hiddenNeuronSpace * (i + 1) + 10);
        }

        g2d.setColor(Color.RED);
        for (ConnectionGene connection : connections) {
            Point start = neuronPositions.get(connection.inNode.innovation);
            Point end = neuronPositions.get(connection.outNode.innovation);
            g2d.drawLine(start.x + diametersForNeurons / 2, start.y + diametersForNeurons / 2,
                    end.x + diametersForNeurons / 2, end.y + diametersForNeurons / 2);
        }

        g2d.dispose();
        return image;
    }
}
