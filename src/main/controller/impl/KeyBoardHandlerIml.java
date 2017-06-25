package main.controller.impl;

import main.GUI;
import main.controller.KeyBoardHandler;
import org.lwjgl.input.Keyboard;

import java.security.Key;

import static main.Direction.*;

/**
 * Created by faos7 on 12.06.17.
 */
public class KeyBoardHandlerIml implements KeyBoardHandler {


    @Override
    public void input() {
        while (Keyboard.next()){
            if (Keyboard.getEventKeyState()){
                switch (Keyboard.getEventKey()){
                    case Keyboard.KEY_ESCAPE:
                            GUI.setIsExitRequested(true);
                        break;
                    case Keyboard.KEY_SPACE:
                        if (GUI.isIsLoosed() || !GUI.isIsExitRequested()){
                            GUI.setIsLoosed(false);

                            GUI.setNewGame(true);
                        }
                        GUI.setIsExitRequested(false);

                        break;
                    case Keyboard.KEY_UP:
                        if(GUI.getDirection()!=SOUTH) GUI.setDirection(NORTH);
                        break;
                    case Keyboard.KEY_RIGHT:
                        if(GUI.getDirection()!=WEST) GUI.setDirection(EAST);
                        break;
                    case Keyboard.KEY_DOWN:
                        if(GUI.getDirection()!=NORTH) GUI.setDirection(SOUTH);
                        break;
                    case Keyboard.KEY_LEFT:
                        if(GUI.getDirection()!=EAST) GUI.setDirection(WEST);
                        break;
                }
            }
        }
    }
}
