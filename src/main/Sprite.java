package main;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by faos7 on 28.05.17.
 */


public enum Sprite {
    BODY("circle"), CHERRIES("grapes");

    private Texture texture;

    Sprite(String textureName){
        try {
            this.texture = TextureLoader.getTexture("PNG", new FileInputStream(
                    new File("/home/faos7/IdeaProjects/snake_applet_demo/res/" + textureName + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Texture getTexture(){
        return this.texture;
    }
}