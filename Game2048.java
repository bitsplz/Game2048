/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;

/**
 *
 * @author MMM
 */
public class Game2048 extends JPanel{
    private Cell[] myTiles;
    private boolean myWin = false;
    private boolean myLose = false;
    private int myScore = 0;
    private final int tileSize = 64;//size of each tile
    private final int tileDis = 16;//distance between each tile
    
    public Game2048() {
        setPreferredSize(new Dimension(340, 400));
        setFocusable(true);
        Control key=new Control();
        addKeyListener(key);
        startGame();
    }
    //used to start a new game
    public void startGame() {
        myScore = 0;
        myWin = false;
        myLose = false;
        myTiles = new Cell[16];
        for (int i = 0; i < myTiles.length; i++) {
            myTiles[i] = new Cell();//creating new cells
        }
        addTile();//adding the starting to tiles
        addTile();
    }

    private void addTile() {
        ArrayList<Cell> list = new ArrayList<>(16);
            for (Cell t : myTiles) {
                if (t.isEmpty()) {
                    list.add(t);
                }
            }
        if (!list.isEmpty()) {
            int index=(int)(Math.random()*list.size());//generate a random cell location
            Cell newTile = list.get(index);
            if (Math.random()<0.9) {
                newTile.value=2;
            }
            else{
                newTile.value=4;
            }
        }
    }

    public void left() {
        boolean needAddTile = false;
        for (int i = 0; i < 4; i++) {
            Cell[] line = getLine(i);//gets the grid line by line
            Cell[] newLine=moveLine(line);//removes all the empty spaces
            Cell[] merged = mergeLine(newLine);//merges the same adjacent tiles
            System.arraycopy(merged,0,myTiles,i*4,4);//copies the new merged line to the original grid
            if (!needAddTile && !compare(line,merged)) {//if original line and new merged line is not same then add a new tile 
                needAddTile = true;
            }
        }
        if (needAddTile) {//adds a random tile after every move
            addTile();
        }
    }
    
    public void right(){
        myTiles = rotate(180);//rotates the whole grid 180 degree clockwise 
        left();
        myTiles = rotate(180);//rotates the whole grid 180 degree clock wise
    }
    
    public void up() {
        myTiles = rotate(270);//rotates the whole grid 270 degree clock wise
        left();
        myTiles = rotate(90);//rotates the whole grid 90 degree clock wise
    }

    public void down() {
        myTiles = rotate(90);//rotates the whole grid 90 degree clock wise
        left();
        myTiles = rotate(270);//rotates the whole grid 270 degree clock wise
    }
    
    //the rotate method gets an angle to rotate the grid clockwise to make it equivalent to the left operation 
    private Cell[] rotate(int angle) {
    Cell[] newTiles = new Cell[16];
    int offsetX = 3, offsetY = 3;
    if (angle == 90) {
      offsetY = 0;
    } else if (angle == 270) {
      offsetX = 0;
    }
    //the concept of matrix rotation is used here. 
    double rad = Math.toRadians(angle);
    int cos=(int)Math.cos(rad);
    int sin=(int)Math.sin(rad);
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        int newX=(x*cos)-(y*sin)+offsetX;
        int newY=(x*sin)+(y*cos)+offsetY;
        newTiles[newX+newY*4]=myTiles[x+y*4];
      }
    }
    return newTiles;
  }
    //checks whether the grid is full and cannot make any move
    private boolean isFull() {
        ArrayList<Cell> list = new ArrayList<>(16);
        for (Cell t : myTiles) {
            if (t.isEmpty()) {
                list.add(t);
            }
        }
        if (list.isEmpty()) {
            return true;
        }
        return false;
    }
    
    
    
    //tells us whether we can move in the direction asked by the user
    private boolean canMove() {
        
        if (!isFull()) {
            return true;
        }
        //checks the adjacent values if they are same then it can move
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (((myTiles[x+y*4].value == myTiles[(x+1)+y*4].value))||((myTiles[x+y*4].value == myTiles[x+(y+1)*4].value))){
                  return true;
                }
            }
        }
        return false;
    }
    
    //compare two lines and its value
    private boolean compare(Cell[] line1, Cell[] line2) {
        if (line1 == line2) {
            return true;
        } else if (line1.length != line2.length) {
            return false;
        }
        for (int i = 0; i < line1.length; i++) {
            if (line1[i].value != line2[i].value) {
                return false;
            }
        }
        return true;
    }
    
    //eliminates the empty spaces in a line
    private Cell[] moveLine(Cell[] oldLine) {
        LinkedList<Cell> l = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            if (!oldLine[i].isEmpty()){
                l.addLast(oldLine[i]);
            }
        }
        if (l.isEmpty()) {//if line is completely empty
            return oldLine;
        } 
        else {
            Cell[] newLine = new Cell[4];
            while (l.size() != 4) {
                l.add(new Cell());
            }
            for (int i = 0; i < 4; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }
    
    //merges the lines adjacent values if they are same
    private Cell[] mergeLine(Cell[] oldLine) {
        LinkedList<Cell> list = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            int num = oldLine[i].value;
            if (i < 3 && oldLine[i].value == oldLine[i+1].value) {//compares the two value and checks whether its not the last cell 
                num *= 2;
                myScore += num;
                int ourTarget = 2048;
                if (num == ourTarget) {//the winning target is checked
                  myWin = true;
                }
                i++;//if the cell merges skip the next tile
            }
            list.add(new Cell(num));
        }
        if (list.isEmpty()) {
            return oldLine;
        } else {
        while (list.size() != 4) {
            list.add(new Cell());
        }
        return list.toArray(new Cell[4]);
        }
    }
    
    //returns a complete line
    private Cell[] getLine(int index) {
    Cell[] line = new Cell[4];
    for (int i = 0; i < 4; i++) {
        line[i] = myTiles[i + index * 4];
    }
    return line;
    }
    
    

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2=(Graphics2D)g;
        g2.setColor(new Color(0xbbada0));
        g2.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

                int value = myTiles[x + y * 4].value;
                int xCordinate = x * (tileDis + tileSize) + tileDis;//calculates the x coordinate
                int yCordinate = y * (tileDis + tileSize) + tileDis;//calculates the y coordinate
                g2.setColor(myTiles[x + y * 4].getBackground());
                g2.fillRoundRect(xCordinate, yCordinate, tileSize, tileSize, 14, 14);//draws a tile at the x and y coordinate
                
                g2.setColor(Color.DARK_GRAY);
                Font font = new Font("Arial", Font.BOLD, 36);//creating font for the value of tiles
                g2.setFont(font);
                String s = String.valueOf(value);
                FontMetrics fm = getFontMetrics(font);//used to get details about the font
                int w = fm.stringWidth(s);
                int h = -(int) fm.getLineMetrics(s, g2).getBaselineOffsets()[2];
                if (value != 0){
                    g2.drawString(s,xCordinate+(tileSize-w)/2,yCordinate+tileSize-(tileSize-h)/2-2);
                }
                //checks for win or loose and displays appropriate text
                if (myWin || myLose) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.darkGray
                    );
                    g2.setFont(new Font("Arial", Font.BOLD, 48));
                    if (myWin) {
                        g2.drawString("You won!", 68, 150);
                    }
                    if (myLose) {
                        g2.drawString("Game over!", 50, 130);
                        g2.drawString("You lose!", 64, 200);
                    }
                    if (myWin || myLose) {
                        g2.setFont(new Font("Arial", Font.PLAIN, 16));
                        g2.setColor(Color.BLACK);
                        g2.drawString("Press ESC to play again", 80,350);
                    }
                }
             
                g2.setFont(new Font("Arial", Font.PLAIN, 18));
                g2.drawString("Score: " + myScore, 200, 365);
            }
        }
    }
    
    
    //the key adapter class to make moves
    private class Control extends KeyAdapter{
    @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
              startGame();
            }
            if (!canMove()) {
              myLose = true;
            }

            if (!myWin && !myLose) {
              switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                  left();
                  break;
                case KeyEvent.VK_RIGHT:
                  right();
                  break;
                case KeyEvent.VK_DOWN:
                  down();
                  break;
                case KeyEvent.VK_UP:
                  up();
                  break;
                }
            }

            if (!myWin && !canMove()) {
              myLose = true;
            }
            repaint();
        }
    }
    
    
    //the cell class
    private class Cell {
        int value;
        private Cell() {
        this.value=0;
        }
        private Cell(int num) {
        value = num;
        }
        public boolean isEmpty() {
            if (value==0) {
                return true;
            }
        return false;
        }
        public Color getBackground() {
            switch (value) {
                case 2:    return new Color(0xeee4da);
                case 4:    return new Color(0xede0c8);
                case 8:    return new Color(0xf2b179);
                case 16:   return new Color(0xf59563);
                case 32:   return new Color(0xf67c5f);
                case 64:   return new Color(0xf65e3b);
                case 128:  return new Color(0xedcf72);
                case 256:  return new Color(0xedcc61);
                case 512:  return new Color(0xedc850);
                case 1024: return new Color(0xedc53f);
                case 2048: return new Color(0xedc22e);
            }
            return new Color(0xcdc1b4);
        }
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String[] args) {
    // TODO code application logic here
    JFrame game = new JFrame();
    game.setTitle("2048 Game");
    game.setDefaultCloseOperation(EXIT_ON_CLOSE);
    game.setSize(340, 400);
    game.setResizable(false);
    game.add(new Game2048());
    game.setVisible(true);
    }

}
