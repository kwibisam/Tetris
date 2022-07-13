package com.km.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

public class Tetris extends JPanel {

    private final Point[][][] points = {
            /*
                #
                #
                #
                #
             */
            {
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(3,1)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3)},
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(3,1)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(1,3)}
            },

            /*
                J
             */
            {
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(2,0)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(2,2)},
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(0,2)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(0,0)}
            },

            /*
                L
             */
            {
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(2,0)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(2,2)},
                    {new Point(0,1),new Point(1,1),new Point(2,1),new Point(0,0)},
                    {new Point(1,0),new Point(1,1),new Point(1,2),new Point(2,0)}
            },
            /*
                0
             */
            {
                    {new Point(0,0),new Point(0,1),new Point(1,0),new Point(1,1)},
                    {new Point(0,0),new Point(0,1),new Point(1,0),new Point(1,1)},
                    {new Point(0,0),new Point(0,1),new Point(1,0),new Point(1,1)},
                    {new Point(0,0),new Point(0,1),new Point(1,0),new Point(1,1)}
            },
    };

    private final Color[] colors = {
            Color.CYAN,
            Color.MAGENTA,
            Color.ORANGE,
            Color.YELLOW,
            Color.BLACK,
            Color.PINK,
            Color.RED
    };
    private Point point;
    private int currentPiece;
    private int rotation;
    private ArrayList<Integer> nextPiece = new ArrayList<>();
    private long score;
    private Color[][] well;

    private void init(){
        well = new Color[12][24];

        for(int i = 0; i < 12; i++){
            for(int j =0; j < 24; j++){
                if(i == 0 || i == 11 || i == 22){
                    well[i][j] = Color.pink;
                }
                else{
                    well[i][j] = Color.BLACK;
                }
            }
            newPiece();
        }
    }

    public void newPiece(){
        point = new Point(5,2);
        rotation = 0;

        if(nextPiece.isEmpty()){
            Collections.addAll(nextPiece, 0,1,2,3);
            Collections.shuffle(nextPiece);
        }
    }

    private boolean collideAt(int x, int y, int rot){
        for(Point p: points[currentPiece][rot]){

            if(well[p.x+x][p.y+y] != Color.black){
                return true;
            }
        }

        return false;
    }

    private void rotate(int i){
        int newRot = (rotation + i)%4;
        if(newRot < 0){
            newRot = 3;
        }

        if(!collideAt(point.x,point.y,newRot))
            rotation = newRot;
        repaint();
    }

    public void move(int i){
        if(!collideAt(point.x, point.y, rotation))
            point.x += i;
        repaint();
    }

    public void drop(){
        if(!collideAt(point.x, point.y, rotation))
            point.y += 1;
        else{
            fixToWell();
        }
        repaint();
    }

    public void fixToWell(){
        for(Point p: points[currentPiece][rotation]){
            well[point.x+p.x][point.y+p.y] = colors[currentPiece];
        }
        clearRows();
        newPiece();
    }

    public void clearRows(){
        boolean gap;
        int clearCnt = 0;
        for(int j = 21; j > 0; j--){
            gap = false;
            for(int i = 1; i < 11; i++){
                if(well[i][j] == Color.BLACK){
                    gap = true;
                    break;
                }
            }

            if(!gap){
                deleteRow(clearCnt);
                j += 1;
                clearCnt += 1;
            }
            switch(clearCnt){
                case 1:
                    score += 100;
                    break;
                case 2:
                    score += 300;
                    break;
                case 3:
                    score += 500;
                    break;
                case 4:
                    score += 800;
                    break;
            }
        }
    }


    public void deleteRow(int row){
        for(int j = row-1; j > 0; j--){
            for(int i = 1; i < 11; i++){
                well[i][j+1] = well[i][j];
            }
        }
    }

    private void drawPiece(Graphics g){
        g.setColor(colors[currentPiece]);
        for(Point p: points[currentPiece][rotation]){
            g.fillRect((p.x+point.x) * 26, (p.y+point.y) * 26, 25, 25);
        }
    }

    @Override
    public void paintComponent(Graphics g){
        g.fillRect(0,0, 26*12, 26*23);

        for(int i = 0; i <  12; i++){
            for(int j = 0; j < 24; j++){
                g.setColor(well[i][j]);
                g.fillRect(26 * i, 26 * j, 25,25);
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("Score:", 19*12,25);
        drawPiece(g);

    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Tetris Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(12 * 26 +10, 26 * 23 +25);
        frame.setVisible(true);

        final Tetris game = new Tetris();
        game.init();
        frame.add(game);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        game.rotate(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        game.rotate(+1);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.move(-1);
                        break;
                    case KeyEvent.VK_SPACE:
                        game.drop();
                        game.score += 1;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        new Thread(){
            public void run(){
                while(true){

                    try{
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    game.drop();

                }
            }
        }.start();
    }
}
