package com.example.neatalgotest.neat;

import java.util.List;

/**
 * Created by Mansitoh
 * Project: TradingView Date: 7/5/2024 @ 14:49
 * Twitter: @Mansitoh_PY Github: https://github.com/Mansitoh
 * Discord: discord.skilled-dev.club
 * Website: skilled-dev.club
 * CEO: Skilled | Development
 */
public abstract class NodeGene {
    public String innovation;
    private final NeuronType neuronType;
    private double bias;

    public NodeGene(String  innovation,NeuronType type) {
        this.innovation = innovation;
        this.neuronType = type;
        this.bias = Math.random()*2-1;
    }



    public NeuronType getNeuronType() {
        return neuronType;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double getOutput(List<ConnectionGene> connections) {
        double sum = 0;
        if(neuronType == NeuronType.INPUT){
            InputNodeGene inputNodeGene = (InputNodeGene) this;
            return NEATUtil.sigmoid(inputNodeGene.getInputValue() );
        }
        List<ConnectionGene> connectionsWithThisAsOutput = connections.stream().filter(c -> c.getOutNode() == this && c.isEnabled()).toList();
        for (ConnectionGene connection : connectionsWithThisAsOutput) {
            NodeGene inNode = connection.getInNode();
            if (inNode instanceof InputNodeGene) {
                sum += ((InputNodeGene) inNode).getInputValue() * connection.getWeight();
            } else {
                sum += inNode.getOutput(connections) * connection.getWeight();
            }
        }
        return NEATUtil.sigmoid(sum + bias);
    }


    public abstract NodeGene copy();

    public String getId() {
        return innovation;
    }

    public int getInnovationAsInteger() {
        return Integer.parseInt(innovation.replace("Hidden-", "").replace("Output-", "").replace("Input-", ""));
    }
}
