package main;

import main.controller.KeyBoardHandler;
import main.controller.impl.KeyBoardHandlerIml;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;

import java.applet.Applet;
import java.awt.*;
import java.util.Random;

import static main.Constants.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static main.Direction.*;

/**
 * Created by faos7 on 01.06.17.
 */
public class GUI extends javax.swing.JApplet {
    ///CELLS_COUNT_X и CELLS_COUNT_Y -- константы
    //Cell -- класс, который реализует main.GUIElement; им займёмся немного позже
    private static Cell[][] cells;

    private KeyBoardHandler keyBoardHandler;
    private static boolean isExitRequested=false;
    private static boolean isLoosed = false;
    private static boolean newGame = true;
    private static int x=-1,y=0, length=3;
    private static Direction direction = EAST;
    private static boolean have_to_decrease = true;

    AWTGLCanvas display_parent;

    public static Direction getDirection() {
        return direction;
    }

    public static void setDirection(Direction direction) {
        GUI.direction = direction;
    }

    public static boolean isNewGame() {
        return newGame;
    }

    public static void setNewGame(boolean newGame) {
        GUI.newGame = newGame;
    }

    public static boolean isIsExitRequested() {
        return isExitRequested;
    }

    public static void setIsExitRequested(boolean isExitRequested) {
        GUI.isExitRequested = isExitRequested;
    }

    public static boolean isIsLoosed() {
        return isLoosed;
    }

    public static void setIsLoosed(boolean isLoosed) {
        GUI.isLoosed = isLoosed;
    }

    public void destroy() {
        remove(display_parent);
        super.destroy();
    }

    public void init(){
        keyBoardHandler = new KeyBoardHandlerIml();
        initializeOpenGL();


        while (true){

            if (newGame){
                newGame = false;
                newGame();

                generate_new_obj();
            }
            updateOpenGL();
            keyBoardHandler.input();
            while (!isIsExitRequested() &&!isLoosed  && !newGame){
                runGame();
            }
        }
    }



    public void newGame(){
        x=-1; y=0; direction= EAST; length=3;
        cells = new Cell[CELLS_COUNT_X][CELLS_COUNT_Y];

        Random rnd = new Random();

        for(int i=0; i<CELLS_COUNT_X; i++){
            for(int j=0; j<CELLS_COUNT_Y; j++){
                cells[i][j]=new Cell(i*CELL_SIZE, j*CELL_SIZE,rnd.nextInt(100)<INITIAL_SPAWN_CHANCE?-1:0);
                //TODO randomize objects
            }
        }
    }

    public void runGame(){
        keyBoardHandler.input();
            if (newGame){
                return;
            }
            if (!isExitRequested && !isLoosed) {
                move();

                draw();
                update(have_to_decrease);
            } else {

                return;
            }

    }

    //Этот метод будет вызываться извне
    public void update(boolean have_to_decrease) {
        updateOpenGL();

        for(Cell[] line:cells){
            for(Cell cell:line){
                cell.update(have_to_decrease);
            }
        }
    }

    ///Рисует все клетки
    public void draw(){
        ///Очищает экран от старого изображения
        glClear(GL_COLOR_BUFFER_BIT);

        for(Cell[] line:cells){
            for(Cell cell:line){
                drawElement(cell);
            }
        }
    }

    ///Рисует элемент, переданный в аргументе
    private void drawElement(Cell elem){
        if(elem.getSprite()==null) return;

        elem.getSprite().getTexture().bind();

        glBegin(GL_QUADS);
        glTexCoord2f(0,0);
        glVertex2f(elem.getX(),elem.getY()+elem.getHeight());
        glTexCoord2f(1,0);
        glVertex2f(elem.getX()+elem.getWidth(),elem.getY()+elem.getHeight());
        glTexCoord2f(1,1);
        glVertex2f(elem.getX()+elem.getWidth(), elem.getY());
        glTexCoord2f(0,1);
        glVertex2f(elem.getX(), elem.getY());
        glEnd();
    }

    public int getState(int x, int y){
        return cells[x][y].getState();
    }

    public void setState(int x, int y, int state){
        cells[x][y].setState(state);
    }

    ///А этот метод будет использоваться только локально,
    /// т.к. базовым другие классы должны работать на более высоком уровне
    private void updateOpenGL() {
        Display.update();
        Display.sync(FPS);
    }

    private void initializeOpenGL(){
        setLayout(new BorderLayout());
        try {
            display_parent = new AWTGLCanvas() {
                public final void addNotify() {
                    super.addNotify();
                }
                public final void removeNotify() {
                    super.removeNotify();
                }
            };
            display_parent.setSize(getWidth(),getHeight());
            this.add(display_parent);
            display_parent.setFocusable(true);
            display_parent.requestFocus();
            display_parent.setIgnoreRepaint(true);
            setVisible(true);
        } catch (Exception e) {
            System.err.println(e);
            throw new RuntimeException("Unable to create display");
        }

        try {
            Display.setParent(display_parent);
            Display.setDisplayMode(new DisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT));
            Display.create();

        } catch (LWJGLException e) {
            e.printStackTrace();
            return;
        }

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0,SCREEN_WIDTH,0,SCREEN_HEIGHT,1,-1);
        glMatrixMode(GL_MODELVIEW);

        /*
         * Для поддержки текстур
         */
        glEnable(GL_TEXTURE_2D);

        /*
         * Для поддержки прозрачности
         */
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        /*
         * Белый фоновый цвет
         */
        glClearColor(1,1,1,1);
    }

    private void move() {
        have_to_decrease=true;

        switch(direction){
            case NORTH:
                y++; break;
            case EAST:
                x++; break;
            case SOUTH:
                y--; break;
            case WEST:
                x--; break;
        }

        if(x<0 || x>=CELLS_COUNT_X || y<0 || y>=CELLS_COUNT_Y){
            //TODO gameover
//            System.exit(1);
            isLoosed = true;
            return;
        }

        int next_cell_state = getState(x,y);

        if(next_cell_state>0){
            //TODO gameover
//            System.exit(1);
            isLoosed = true;
            return;
        }else{
            if(next_cell_state<0){
                length++;
                generate_new_obj();
                have_to_decrease=false;
            }
            setState(x,y,length);
        }
    }

    private void generate_new_obj() {
        int point = new Random().nextInt(CELLS_COUNT_X*CELLS_COUNT_Y-length);

        for(int i=0; i<CELLS_COUNT_X; i++){
            for(int j=0; j<CELLS_COUNT_Y; j++){
                if(getState(i,j)<=0) {
                    if (point == 0) {
                        setState(i, j, -1); //TODO randomize objects
                        return;
                    }else {
                        point--;
                    }
                }
            }
        }

    }

    @Override
    public void start() {
        super.start();
    }
}