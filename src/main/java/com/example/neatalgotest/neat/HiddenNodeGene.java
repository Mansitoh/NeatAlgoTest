package com.example.neatalgotest.neat;

/**
 * Created by Mansitoh
 * Project: TradingView Date: 11/5/2024 @ 22:52
 * Twitter: @Mansitoh_PY Github: https://github.com/Mansitoh
 * Discord: discord.skilled-dev.club
 * Website: skilled-dev.club
 * CEO: Skilled | Development
 */
public class HiddenNodeGene extends NodeGene {

    public HiddenNodeGene(int innovation) {
        super("Hidden-"+innovation, NeuronType.HIDDEN);
    }

    public HiddenNodeGene copy() {
        HiddenNodeGene newNodeGene = new HiddenNodeGene(Integer.parseInt(this.innovation.replace("Hidden-", "")));
        newNodeGene.setBias(getBias());
        return newNodeGene;
    }
}
