package com.example.neatalgotest.neat;

/**
 * Created by Mansitoh
 * Project: TradingView Date: 11/5/2024 @ 22:53
 * Twitter: @Mansitoh_PY Github: https://github.com/Mansitoh
 * Discord: discord.skilled-dev.club
 * Website: skilled-dev.club
 * CEO: Skilled | Development
 */
public class OutputNodeGene  extends NodeGene{

        public OutputNodeGene(int innovation) {
            super("Output-"+innovation, NeuronType.OUTPUT);
        }

        public OutputNodeGene copy() {
            OutputNodeGene newNodeGene = new OutputNodeGene(Integer.parseInt(this.innovation.replace("Output-", "")));
            newNodeGene.setBias(getBias());
            return newNodeGene;
        }
}
