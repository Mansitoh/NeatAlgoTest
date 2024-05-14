package com.example.neatalgotest.flappybird;


import com.example.neatalgotest.neat.Brain;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Bird {
    public List<Rectangle> goals = new ArrayList<>();
    public int timeAlive = 0;
    int x;
    int y;
    private boolean isAlive;
    private Brain neuralNetwork; // Aquí debes tener tu implementación de la red neuronal
    public int flaps = 0;
    private int vy; // Velocidad vertical del pájaro
    private final int GRAVITY = 1; // Gravedad
    private final int FLAP_FORCE = -12; // Fuerza de aleteo de alas
    public Bird(int x, int y) {
        this.x = x;
        this.y = y;
        this.neuralNetwork = new Brain(4, 1); // Inicializa la red neuronal con 3 entradas y 1 salida (por ejemplo
        this.isAlive = true;
    }

    public int getFitness() {
        return (int) (timeAlive + goals.size() * 100 + flaps);
    }

    public void update(double[] inputs) {
        if (isAlive) {
            timeAlive++;
            vy += GRAVITY;
            inputs[0] = inputs[0];
            inputs[1] = inputs[1] ;
            inputs[2] = inputs[2];
            inputs[3] = (double) y ;

            System.out.println("Inputs: " + inputs[0] + " " + inputs[1] + " " + inputs[2] + " " + inputs[3]);
            // Llama al método de la red neuronal para tomar una decisión
            double[] output = neuralNetwork.feedForward(inputs);
            System.out.println("Output: " + output[0]);
            boolean shouldJump = output[0] > 0.73;
            // Si la red neuronal decide que el pájaro debe saltar, hazlo saltar
            if (shouldJump) {
                vy = FLAP_FORCE;
                flaps++;
            }

            y += vy;
        }
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void setAlive(boolean b) {
        this.isAlive = b;
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public void reachGoal(Rectangle goal) {
        if (!goals.contains(goal)) {
            goals.add(goal);
        }
    }

    public Bird crossover(Bird secondBestBird) {
        Bird child = new Bird(this.x, this.y);
        child.neuralNetwork = this.neuralNetwork.crossover(secondBestBird.neuralNetwork);
        return child;
    }

    public Bird mutableCopy() {
        Bird child = new Bird(this.x, this.y);
        child.neuralNetwork = this.neuralNetwork.copy();
        child.neuralNetwork.mutate();
        return child;
    }

    public void reset(){
        this.timeAlive = 0;
        this.goals.clear();
        this.isAlive = true;
        this.flaps = 0;


    }
}
