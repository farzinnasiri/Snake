package snake;

/**
 * @author farzin.nasiri
 */

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    // setting the windows size
    static final int HEIGHT = 500;
    static final int WIDTH = 500;
    private static final int SIZE = 10;
    // render
    private Graphics2D g2;
    private BufferedImage image;

    // the game loop things

    private long targetTime;
    private boolean running;

    // Game stuff
    private Square head, food;
    private ArrayList<Square> snake;
    private ArrayList<Square> wall;
    private Color foodColor;

    //game sounds
    private AudioInputStream eatingSound;
    private AudioInputStream gameOverSound;

    private Clip eatingClip;
    private Clip losingClip;

    // movment
    private int dx, dy;

    // key input
    private boolean up, down, right, left, start;


    //game states
    private boolean won;
    private int score;
    private int level;
    private boolean gameover;
    private boolean wallOn;
    private double fps;
    private int count = 0;
    private ArrayList<Integer> highScore;
    private Random ran;
    private boolean extraPoint;
    private long startTime, endTime;
    private int extraFoodTimeOnScreen;


    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        this.highScore = new ArrayList<Integer>();
        highScore.add(0);
        this.ran = new Random();
        this.extraFoodTimeOnScreen = 4000;
        this.foodColor = Color.RED;
        try {
            this.eatingSound = AudioSystem
                    .getAudioInputStream(getClass().getClassLoader().getResourceAsStream("eat_sound.wav"));
            this.eatingClip = AudioSystem.getClip();
            this.eatingClip.open(this.eatingSound);
            this.gameOverSound = AudioSystem
                    .getAudioInputStream(getClass().getClassLoader().getResourceAsStream("game_over.wav"));
            this.losingClip = AudioSystem.getClip();
            this.losingClip.open(this.gameOverSound);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setFps(double fps) {
        targetTime = (1000 / (int) fps);

    }
    private void init() {
        this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2 = this.image.createGraphics();
        running = true;
        setUpLevel();
        gameover = false;
        level = 1;

    }

    @Override
    public void addNotify() {
        super.addNotify();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        // the controls
        if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
            this.up = true;
        } else if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
            this.down = true;
        } else if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
            this.left = true;
        } else if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
            this.right = true;
        } else if (k == KeyEvent.VK_R) {
            this.start = true;
            this.won = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();

        // the controls
        if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
            this.up = false;
        } else if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
            this.down = false;
        } else if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
            this.left = false;
        } else if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
            this.right = false;
        }

    }

    @Override
    public void run() {
        if (running)
            return;
        init();
        long starTime;
        long elapsedTime;
        long sleep;
        while (running) {
            starTime = System.nanoTime();

            // updating snake and rendering
            updateGame();
            render();
            elapsedTime = System.nanoTime() - starTime;
            sleep = targetTime - elapsedTime / 1000000;
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    System.out.println("Problem in snake loop");
                    e.printStackTrace();
                }
            }

        }

    }

    //setting up the game entities each time it starts
    private void setUpLevel() {
        this.snake = new ArrayList<>();
        this.wall = new ArrayList<>();
        this.head = new Square(SIZE);
        this.head.setPosition(WIDTH / 2, HEIGHT / 2);
        this.snake.add(head);
        //initial length is 3
        for (int i = 1; i < 3; i++) {
            Square e = new Square(SIZE);
            e.setPosition(head.getX() + (i * SIZE), this.head.getY());
            this.snake.add(e);
        }

        this.food = new Square(SIZE);
        setFood();
        this.score = 0;
        this.count = 0;
        this.fps = 10;
        setFps(fps);
        this.gameover = false;
        this.level = 0;
        this.foodColor = Color.RED;

    }

    //foods position is determined randomly
    private void setFood() {
        int x, y;
        x = randomPosition();
        y = randomPosition();
        this.food.setPosition(x, y);
        for (Square e : snake) {
            if(food.isCollision(e) && !food.isCollision(head)){
                setFood();
            }

        }

    }

    private int randomPosition() {
        int p = (int) (ran.nextDouble() * (WIDTH - 2 * SIZE) + SIZE);
        p -= (p % SIZE);
        return p;
    }

    private void setWall() {
        //upper wall
        for (int i = 0; i < 50; i++) {
            Square e = new Square(SIZE);
            e.setPosition(i * SIZE, 0);
            this.wall.add(e);
        }
        //lower wall
        for (int i = 0; i < 50; i++) {
            Square e = new Square(SIZE);
            e.setPosition(i * SIZE, HEIGHT - SIZE);
            this.wall.add(e);
        }

        //left wall
        for (int i = 0; i < 50; i++) {
            Square e = new Square(SIZE);
            e.setPosition(0, i * SIZE);
            this.wall.add(e);
        }

        //right wall
        for (int i = 0; i < 50; i++) {
            Square e = new Square(SIZE);
            e.setPosition(WIDTH - SIZE, i * SIZE);
            this.wall.add(e);
        }

    }

    private void render() {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //cleaning screen
        g2.clearRect(0, 0, WIDTH, HEIGHT);


        //setting the head and body color
        for (int i = 0; i < snake.size(); i++) {
            if (i == 0) {
                g2.setColor(new Color(0, 50, 200));
            } else {
                g2.setColor(new Color(0, 100, 200));
            }
            Square e = snake.get(i);
            e.render(g2);

        }

        if (wallOn) {
            g2.setColor(Color.DARK_GRAY);

            for (Square e : wall) {
                e.renderWall(g2);

            }
        }
        if (!gameover) {
            g2.setColor(foodColor);
            if (extraPoint) {
                endTime = System.currentTimeMillis();
                if ((endTime - startTime) >= extraFoodTimeOnScreen) {
                    extraPoint = false;
                    foodColor = Color.RED;
                }
            }

            food.render(g2);
        }
        renderTextHints();

        Graphics g = getGraphics();
        g.drawImage(this.image, 0, 0, null);
        g.dispose();
    }

    //text hints
    private void renderTextHints() {

        if (gameover && !won) {
            g2.setColor(Color.RED);
            g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 17));
            g2.drawString("GameOver!", 220, 250);
            g2.drawString("Press R with a direction key to restart", 110, 300);
        } else if (won) {
            g2.setColor(Color.GREEN);
            g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 40));
            g2.drawString("Congratulations you WON!!!!", 5, 250);

        }
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 12));
        if (!wallOn) {
            g2.drawString("Score: " + score, 220, 12);
            g2.drawString(" Level: " + level, 220, 490);
            g2.drawString("High Score: " + Collections.max(highScore), 410, 12);
        } else {
            g2.drawString("Score: " + score, 220, 25);
            g2.drawString(" Level: " + level, 220, 480);
            g2.drawString("High Score: " + Collections.max(highScore), 390, 25);

        }

        if (dx == 0 && dy == 0) {

            g2.setColor(Color.GREEN);
            g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 20));
            g2.drawString("Ready!", 210, 200);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(g2.getFont().getFontName(), Font.PLAIN, 14));
            g2.drawString("Use the arrow keys or W,S,A,D to control the snake", 90, 300);
            g2.drawString("Walls are going to appear in level six, if you hit them,you lose!", 55, 330);
            g2.drawString("Yellow foods give you extra points,the last only 3 seconds", 70, 360);
            g2.drawString("Created by Farzin Nasiri", 165, 450);
        }

    }

    private void updateGame() {

        if (gameover) {
            this.wallOn = false;
            highScore.add(score);

            if (start) {

                setUpLevel();
            }
        } else {
            if (this.up && dy == 0) {
                dy = -SIZE;
                dx = 0;

            } else if (this.down && dy == 0) {
                dy = SIZE;
                dx = 0;

            } else if (this.left && dx == 0) {
                dy = 0;
                dx = -SIZE;

            } else if (this.right && dx == 0 && dy != 0) {
                dy = 0;
                dx = SIZE;
            }
            if (dx != 0 || dy != 0) {

                for (int i = snake.size() - 1; i > 0; i--) {
                    this.snake.get(i).setPosition(this.snake.get(i - 1).getX(), this.snake.get(i - 1).getY());

                }
                head.move(dx, dy);
            }
            for (Square e : snake) {
                if (e.isCollision(head)) {
                    losing();
                    break;
                }


            }
            if(wallOn){
                for (Square e : wall) {
                    if(e.isCollision(head)){
                        losing();
                        break;
                    }

                }
            }


            if (food.isCollision((head))) {
                eatingClip.start();
                eatingClip.setFramePosition(0);
                if (extraPoint) {
                    score += 5;
                } else {

                    score++;
                }
                if (ran.nextInt(20) == 0) {
                    foodColor = Color.YELLOW;
                    extraPoint = true;
                    startTime = System.currentTimeMillis();

                } else {
                    foodColor = Color.RED;
                    extraPoint = false;

                }

                setFood();
                Square e = new Square(SIZE);

                e.setPosition(-100, -100);
                this.snake.add(e);
                level = (score / 10) + 1;
                if (level > 10) {
                    level = 10;
                    won = true;
                    gameover = true;
                } else if (level >= 1 && fps <= 20) {
                    fps *= 1.010;
                }

                if (level > 5) {
                    wallOn = true;
                }
                setFps(fps);

            }
            if (wallOn && count == 0) {
                setWall();
                count++;
            }
            if (!wallOn) {
                if (head.getX() < 0)
                    head.setX(WIDTH);
                else if (head.getY() < 0)
                    head.setY(HEIGHT);
                else if (head.getX() > WIDTH)
                    head.setX(0);
                else if (head.getY() > HEIGHT)
                    head.setY(0);
            }

        }
    }

    private void losing() {
        losingClip.start();
        this.gameover = true;
        this.start = false;
        losingClip.setFramePosition(0);
        return;
    }


}
