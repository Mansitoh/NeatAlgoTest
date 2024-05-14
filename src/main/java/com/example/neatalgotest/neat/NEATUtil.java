package com.example.neatalgotest.neat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mansitoh
 * Project: TradingView Date: 11/5/2024 @ 00:30
 * Twitter: @Mansitoh_PY Github: https://github.com/Mansitoh
 * Discord: discord.skilled-dev.club
 * Website: skilled-dev.club
 * CEO: Skilled | Development
 */
public class NEATUtil {

    public static List<ConnectionHistory> connectionHistory = new ArrayList<>();
    public static List<NodeHistory> nodeHistory = new ArrayList<>();

    public static double sigmoid(double sum) {
        return 1 / (1 + Math.exp(-sum));
    }

    public static int getCurrentInnovationForConnection(NodeGene inputNode, NodeGene outputNode) {
        for (ConnectionHistory history : connectionHistory) {
            if (history.inNode.equals(inputNode.getId()) && history.outNode.equals(outputNode.getId())) {
                return history.innovationNumber;
            }
        }
        connectionHistory.add(new ConnectionHistory(inputNode.getId(), outputNode.getId(), connectionHistory.size()));
        return connectionHistory.size()-1;
    }

    public static int getCurrentInnovationForNode(NodeGene inputNode, NodeGene outputNode) {
        for (NodeHistory history : nodeHistory) {
            if(history.firstConnectionIn.equalsIgnoreCase(inputNode.getId()) && history.firstConnectionOut.equalsIgnoreCase(outputNode.getId())){
                return history.innovationNumber;
            }
        }
        nodeHistory.add(new NodeHistory(inputNode.getId(), outputNode.getId(), nodeHistory.size()));
        return nodeHistory.size()-1;
    }


    public static class ConnectionHistory {
        public String inNode;
        public String outNode;
        public int innovationNumber;

        public ConnectionHistory(String inNode, String outNode, int innovationNumber) {
            this.inNode = inNode;
            this.outNode = outNode;
            this.innovationNumber = innovationNumber;
        }
    }

    public static class NodeHistory {
        public String firstConnectionIn;
        public String firstConnectionOut;
        public int innovationNumber;

        public NodeHistory(String firstConnectionIn, String firstConnectionOut, int innovationNumber) {
            this.firstConnectionIn = firstConnectionIn;
            this.firstConnectionOut = firstConnectionOut;
            this.innovationNumber = innovationNumber;
        }
    }
}

