package com.straight.game;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Pipe {
    static int height = 150;                         // ---> Height = Max Height of pipe
    static int opening = 275;                        // ---> opening between top and bottom pipe
    Texture topPipe, bottomPipe;                     // ---> Textures of our Pipes
    Vector2 topPipePosition, bottomPipePosition;     // ---> For position of our pipes
    Rectangle topPipeRectangle, bottomPipeRectangle; // ---> to store bounds of pipes
    boolean isScored;

    public Pipe(float x) {
        topPipe = new Texture("toppipe.png");
        bottomPipe = new Texture("bottompipe.png");

        // RANDOM TOP PIPE Y POSITION
        topPipePosition = new Vector2(x, (float) (Math.random() * height) + opening + 25);
        bottomPipePosition = new Vector2(x, topPipePosition.y - opening - bottomPipe.getHeight() + 25);

        topPipeRectangle = new Rectangle(topPipePosition.x, topPipePosition.y, topPipe.getWidth(), topPipe.getHeight());
        bottomPipeRectangle = new Rectangle(bottomPipePosition.x, bottomPipePosition.y, bottomPipe.getWidth(), bottomPipe.getHeight());

        isScored = false;
    }

    public Texture getTopPipeTexture() {
        return topPipe;
    }

    public Texture getBottomPipeTexture() {
        return bottomPipe;
    }

    public Vector2 getTopPipePosition() {
        return topPipePosition;
    }

    public Vector2 getBottomPipePosition() {
        return bottomPipePosition;
    }

    public void updateBounds() {
        topPipeRectangle.setPosition(topPipePosition.x, topPipePosition.y);
        bottomPipeRectangle.setPosition(bottomPipePosition.x, bottomPipePosition.y);
    }

    public boolean collides(Rectangle bird) {
        return bird.overlaps(topPipeRectangle) || bird.overlaps(bottomPipeRectangle);
    }

    public boolean isScored() {
        return isScored;
    }

    public void setScored(boolean scored) {
        isScored = scored;
    }

    public void dispose() {
        topPipe.dispose();
        bottomPipe.dispose();
    }
}
