package com.example.neatalgotest.neat;

public class InputNodeGene extends NodeGene {

    public double inputValue;

    public InputNodeGene(int innovation) {
        super("Input-"+innovation, NeuronType.INPUT);
    }

    public double getInputValue() {
        return inputValue;
    }

    public void setInputValue(double inputValue) {
        this.inputValue = inputValue;
    }


    public InputNodeGene copy() {
        InputNodeGene newNodeGene = new InputNodeGene(Integer.parseInt(this.innovation.replace("Input-", "")));
        newNodeGene.setBias(getBias());
        return newNodeGene;
    }
}