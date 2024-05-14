package com.example.neatalgotest.neat;

/**
 * Created by Mansitoh
 * Project: TradingView Date: 7/5/2024 @ 22:20
 * Twitter: @Mansitoh_PY Github: https://github.com/Mansitoh
 * Discord: discord.skilled-dev.club
 * Website: skilled-dev.club
 * CEO: Skilled | Development
 */
public class ConnectionGene {

    public NodeGene inNode;
    public NodeGene outNode;
    public double weight;
    public int innovation;
    public boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ConnectionGene(NodeGene inNode, NodeGene outNode, int innovation) {
        this.inNode = inNode;
        this.outNode = outNode;
        //random weight between -1 and 1
        this.weight = Math.random() * 2 - 1;
        this.innovation = innovation;
    }

    public ConnectionGene(NodeGene inNode, NodeGene outNode, double weight, int innovation) {
        this.inNode = inNode;
        this.outNode = outNode;
        this.weight = weight;
        this.innovation = innovation;
    }

    public int getInnovation() {
        return innovation;
    }

    public void setInnovation(int innovation) {
        this.innovation = innovation;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public NodeGene getInNode() {
        return inNode;
    }

    public void setInNode(NodeGene inNode) {
        this.inNode = inNode;
    }

    public NodeGene getOutNode() {
        return outNode;
    }

    public void setOutNode(NodeGene outNode) {
        this.outNode = outNode;
    }

    public ConnectionGene copy() {
        return new ConnectionGene(getInNode().copy(), getOutNode().copy(), weight,innovation);
    }
}
