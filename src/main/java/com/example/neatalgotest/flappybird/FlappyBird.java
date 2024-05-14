package com.example.neatalgotest.flappybird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBird extends JFrame implements KeyListener, ActionListener {
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;
    private final int BIRD_SIZE = 30;
    private final int COLUMN_WIDTH = 100;
    private final int COLUMN_GAP = 200;
    private final int COLUMN_SPEED = 5;
    private final int BIRD_SPEED = 2;

    private Timer timer;
    private boolean isGameOver = false;
    private boolean isGameStarted = false;
    private List<Rectangle> columns;
    private List<Bird> birds; // Lista de pájaros controlados por la red neuronal
    private Random random;
    private List<Rectangle> goals;
    private int generationCount =1 ;
    public int birdsAmount= 1000;

    public FlappyBird() {
        setTitle("Flappy Bird");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(this);

        timer = new Timer(20, this);
        columns = new ArrayList<>();
        birds = new ArrayList<>(); // Inicializa la lista de pájaros
        goals = new ArrayList<>();
        random = new Random();

        // Crea cuatro pájaros controlados por la red neuronal
        for (int i = 0; i < birdsAmount; i++) {
            // Aquí debes crear tu red neuronal y pasarla como argumento al constructor del pájaro
            Bird bird = new Bird(100, WINDOW_HEIGHT / 4);
            birds.add(bird);
        }

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
        setVisible(true);
    }

    public void addColumn(boolean start) {
        int space = 300;
        int width = 100;
        int height = 50 + random.nextInt(300);

        Rectangle lowerColumn;
        Rectangle upperColumn;
        if (start) {
            lowerColumn = new Rectangle(WINDOW_WIDTH/4 + width + columns.size() * COLUMN_GAP,
                    WINDOW_HEIGHT - height - 150, width, height+500);
            columns.add(lowerColumn);
            upperColumn = new Rectangle(WINDOW_WIDTH/4+ width + (columns.size() - 1) * COLUMN_GAP,
                    0, width, WINDOW_HEIGHT - height - space);
            columns.add(upperColumn);

             Rectangle goal = new Rectangle((WINDOW_WIDTH/4 + width + (columns.size() - 1) * COLUMN_GAP)-width,
                    upperColumn.y+upperColumn.height, 2, lowerColumn.y-(upperColumn.y+upperColumn.height));
            goals.add(goal);
        } else {
            lowerColumn = new Rectangle(columns.get(columns.size() - 1).x + 300, WINDOW_HEIGHT - height - 150, width, height+500);
            columns.add(lowerColumn);
            upperColumn = new Rectangle(columns.get(columns.size() - 1).x, 0, width, WINDOW_HEIGHT - height - space);
            columns.add(upperColumn);
                Rectangle goal = new Rectangle((columns.get(columns.size() - 1).x)-width,
                    upperColumn.y+upperColumn.height, 2, lowerColumn.y-(upperColumn.y+upperColumn.height));
            goals.add(goal);
        }
    }

    public void paint(Graphics g) {
        int birdsAlive = 0;
        for (Bird bird : birds) {
            if (bird.isAlive()) {
                birdsAlive++;
            }
        }


        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Generation: " + generationCount, 20, 50);
        g.drawString("Alive Birds: " + birdsAlive, 20, 100);
        if(birdsAlive == 0){
            isGameOver = true;
        }

        g.setColor(Color.darkGray);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);


        for (Rectangle column : columns) {
            g.setColor(Color.green);
            g.fillRect(column.x, column.y, column.width, column.height);
        }

        for(Rectangle goal : goals){
            g.setColor(Color.blue);
            g.fillRect(goal.x, goal.y, goal.width, goal.height);
        }

        for (Bird bird : birds) {
            if (bird.isAlive()) {
                g.setColor(Color.red);
                g.fillRect(bird.getX(), bird.getY(), BIRD_SIZE, BIRD_SIZE);
            }
        }



        birdsAlive = 0;
        for (Bird bird : birds) {
            if (bird.isAlive()) {
                birdsAlive++;
            }
        }


        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Generation: " + generationCount, 20, 50);
        g.drawString("Alive Birds: " + birdsAlive, 20, 100);
        if(birdsAlive == 0){
            isGameOver = true;
        }

        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over!", WINDOW_WIDTH / 2 - 150, WINDOW_HEIGHT / 2 - 50);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = COLUMN_SPEED;
        if (!isGameOver) {
            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                column.x -= speed;
            }

            for (int i = 0; i < goals.size(); i++) {
                Rectangle goal = goals.get(i);
                goal.x -= speed;
            }

            int birdNumber = 0;
            for (Bird bird : birds) {
                if (bird.isAlive()) {

                    Rectangle closestColumn = null;
                    Rectangle secondClosestColumn = null;
                    int minDistance = Integer.MAX_VALUE;

                    for (Rectangle column : columns) {
                        int distance = column.x - (bird.getX() - BIRD_SIZE);
                        if (distance > 0 && distance < minDistance) {
                            minDistance = distance;
                            closestColumn = column;
                        }
                    }

                    for(Rectangle column : columns){
                        if(column.equals(closestColumn)){
                            continue;
                        }
                        if(column.x == closestColumn.x){
                            secondClosestColumn = column;
                        }
                    }

                    if (closestColumn == null || secondClosestColumn == null) {
                        System.out.println("No closest column found");
                        continue;
                    }

                    if(closestColumn != null && secondClosestColumn != null){
                        int firstY = closestColumn.y;
                        int secondY = secondClosestColumn.y;
                        Rectangle copyFirst = closestColumn;
                        if(firstY < secondY){
                            closestColumn = secondClosestColumn;
                            secondClosestColumn = copyFirst;
                        }

                    }


                    double xDistance = closestColumn.x - (bird.getX()-BIRD_SIZE);
                    double yDistanceLower = closestColumn.y-(bird.getY() +BIRD_SIZE);
                    double yDistanceUpper = (secondClosestColumn.y + secondClosestColumn.height) - bird.getY();

                    double[] inputs = {xDistance, yDistanceLower, yDistanceUpper,0};

                    System.out.println("===============================");
                    System.out.println("Bird number: " + birdNumber);
                    bird.update(inputs);
                    if (bird.getY() <= 0 || bird.getY() >= WINDOW_HEIGHT - BIRD_SIZE) {
                        bird.setAlive(false);
                    }
                    for (Rectangle column : columns) {
                        if (column.intersects(new Rectangle(bird.getX(), bird.getY(), BIRD_SIZE, BIRD_SIZE))) {
                            bird.setAlive(false);
                        }
                    }

                    for(Rectangle goal : goals){
                        if(goal.intersects(new Rectangle(bird.getX(), bird.getY(), BIRD_SIZE, BIRD_SIZE))){
                            bird.reachGoal(goal);
                        }
                    }
                }
                birdNumber++;
            }

            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                if (column.x + column.width < 0) {
                    columns.remove(column);
                    if (column.y == 0) {
                        addColumn(false);
                    }
                }
            }
            repaint();
        }else{
            System.out.println("Game Over");
            //Choose the best bird
            Bird bestBird = null;
            int maxFitness = 0;
            for(Bird bird : birds){
                if(bird.getFitness() > maxFitness){
                    maxFitness = bird.getFitness();
                    bestBird = bird;
                }
            }
            System.out.println("Best bird fitness: " + bestBird.getFitness());

            Bird secondBestBird = null;
            int secondMaxFitness = 0;
            for(Bird bird : birds){
                if(bird.getFitness() > secondMaxFitness && bird != bestBird){
                    secondMaxFitness = bird.getFitness();
                    secondBestBird = bird;
                }
            }

            //Here you should implement the genetic algorithm to create the next generation of birds
            Bird crossoverBird = bestBird.crossover(secondBestBird);


            //Reinicia el juego
            timer.stop();
            columns.clear();
            goals.clear();
            birds.clear();
            isGameOver = false;
            isGameStarted = false;


            timer = new Timer(20, this);
            columns = new ArrayList<>();
            birds = new ArrayList<>(); // Inicializa la lista de pájaros
            goals = new ArrayList<>();
            random = new Random();

            bestBird.x = 100;
            bestBird.y = WINDOW_HEIGHT / 4;
            bestBird.flaps = 0;
            bestBird.reset();
            birds.add(bestBird);

            Bird mutatedBestBird = bestBird.mutableCopy();
            birds.add(mutatedBestBird);

            secondBestBird.x = 100;
            secondBestBird.y = WINDOW_HEIGHT / 4;
            secondBestBird.flaps = 0;
            secondBestBird.reset();
            birds.add(secondBestBird);
            crossoverBird.x = 100;
            crossoverBird.y = WINDOW_HEIGHT / 4;
            crossoverBird.flaps = 0;
            crossoverBird.reset();
            birds.add(crossoverBird);


            for (int i = 0; i < birdsAmount -4; i++) {
                // Aquí debes crear tu red neuronal y pasarla como argumento al constructor del pájaro
                Bird bird = bestBird.mutableCopy();
                birds.add(bird);
            }

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            timer.start();
            setVisible(true);
            generationCount++;

        }

    }

    public static void main(String[] args) {
        new FlappyBird();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

    }


    @Override
    public void keyReleased(KeyEvent e) {}
}
