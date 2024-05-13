package com.straight.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FlappyBird extends ApplicationAdapter {
    static float speed = 100;                       // ---> Speed at which pipes could spawn
    static final float width = 640, height = 480;   // ---> Screen Width and height
    static final float gap = 150;                  // ---> The gap between two consecutive pipes
    static float count = 120;                       // ---> Total number of pipes
    Array<Pipe> Pipes;                              // ---> Container of all Pipes
    SpriteBatch batch;                              // ---> For drawing stuff
    Texture background, gameOver, bird, playScreen; // ---> Textures for game
    Texture gameComplete;
    OrthographicCamera camera;                      // ---> camera for frame
    Vector2 birdPosition, birdVelocity;             // ---> Helper variable for position of bird
    Rectangle birdRectangle;                        // ---> Storing bounds of bird
    boolean isGameOver, isGameStarted = false;      // ---> Switch for Game On and Game Over
    boolean isGameComplete = false;
    Animation<TextureRegion> birdAnimation;         // ---> Contains three animations of bird
    float stateTime;                                // ---> Helper variable for smooth frame rendering
    int score = 0;                                  // ---> to keep track of score
    BitmapFont font;                                // ---> Font for our Score
    AssetManager assetManager;
    Music backgroundMusic;

    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.load("musicBackground.mp3", Music.class);
        assetManager.finishLoading();

        gameComplete = new Texture("gameComplete.png");

        backgroundMusic = assetManager.get("musicBackground.mp3", Music.class);

        batch = new SpriteBatch();
        background = new Texture("background.png");
        bird = new Texture("bird.png");

        playScreen = new Texture("playbutton.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);

        birdPosition = new Vector2(width / 4, height / 2);
        birdVelocity = new Vector2(0, 0);
        birdRectangle = new Rectangle(birdPosition.x, birdPosition.y, bird.getWidth(), bird.getHeight());

        gameOver = new Texture("gameover.png");
        isGameOver = false;

        Texture birdSheet = new Texture("birdanimation.png");
        TextureRegion[][] tmp = TextureRegion.split(birdSheet, birdSheet.getWidth() / 3, birdSheet.getHeight());
        Array<TextureRegion> birdFrames = new Array<>();
        for (int i = 0; i < 3; i++) {
            birdFrames.add(tmp[0][i]); // -> Loading three animations in one array(birdFrames)
        }
        birdAnimation = new Animation<>(0.1f, birdFrames);
        stateTime = 0f; // a variable to ensure proper and smooth rendering of bird animation

        Pipes = new Array<>();
        float PipeX = width;
        for (int i = 1; i <= count; i++) {
            Pipes.add(new Pipe(PipeX));
            PipeX += gap + Pipes.peek().getTopPipeTexture().getWidth();
        }
        font = new BitmapFont();   // ---> Score Font
        font.setColor(Color.BLACK);
    }

    @Override
    public void render() {
        update();  // -> First update the things like position, pipes, etc...
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Remove all other Color graphics

        camera.update();
        batch.setProjectionMatrix(camera.combined); // --> camera draws things with sprite batch perspective

        batch.begin();
        batch.draw(background, 0, 0, width, height); // -> Drawing Background
        if (!isGameStarted) {
            batch.draw(playScreen, width / 2 - (float) playScreen.getWidth() / 2,
                    height / 2 - (float) playScreen.getHeight() / 2);
        } else {
            batch.draw(bird, birdPosition.x, birdPosition.y); // Drawing Bird if game is started
        }
        batch.end();

        if (isGameComplete){
            batch.begin();
            batch.draw(gameComplete, 0, 0, width, height);
            batch.end();
        }

        if (isGameOver) { // -> Drawing GameOver Image if the game is over
            batch.begin();
            batch.draw(gameOver, width / 2 - (float) gameOver.getWidth() / 2,
                    height / 2 - (float) gameOver.getHeight() / 2);
            font.draw(batch, "Score: " + score, width / 2, height - 100);
            batch.end();
        }

        if (isGameStarted) {
            batch.begin();
            for (Pipe pipe : Pipes) {
                if (score == 51){batch.setColor(Color.SKY);}
                if (score == 52){batch.setColor(Color.WHITE);}
                if (score == 56){batch.setColor(Color.FOREST);}
                if (score == 57){batch.setColor(Color.WHITE);}
                if (score == 61){batch.setColor(Color.GRAY);}
                if (score == 62){batch.setColor(Color.WHITE);}
                if (score >= 30 && score < 47
                        || (score >= 94 && score < 111)) {
                    batch.setColor(Color.GOLD);
                }
                if (score >= 16 && score < 30) {
                    if (System.currentTimeMillis() % 1000 < 385) {
                        batch.setColor(Color.CYAN);
                    } else {
                        batch.setColor(Color.WHITE);
                    }
                }
                batch.draw(pipe.getTopPipeTexture(), pipe.getTopPipePosition().x, pipe.getTopPipePosition().y);
                batch.draw(pipe.getBottomPipeTexture(), pipe.getBottomPipePosition().x, pipe.getBottomPipePosition().y);
                if (! (score >= 30 && score < 47)){batch.setColor(Color.WHITE);}
            }

            font.draw(batch, "Score: " + score, 20, height - 20); // -->  Draws score

            stateTime += Gdx.graphics.getDeltaTime(); // --> current time in the frame

            batch.draw(birdAnimation.getKeyFrame(stateTime, true)
                    , birdPosition.x, birdPosition.y);
            batch.end();
        }
    }

    private void update() {
        if (score == count){isGameComplete = true;}
        if (!isGameOver && isGameStarted && !isGameComplete) {
            if (Gdx.input.justTouched()) {
                birdVelocity.y = 225; // ---> Jumps
            }

            birdVelocity.y -= 5; // (GRAVITY) of 5 pixels/frame
            birdPosition.y += birdVelocity.y * Gdx.graphics.getDeltaTime(); // <-(GRAVITY)->

            birdPosition.x += Gdx.graphics.getDeltaTime(); // ---> moves birds horizontally
            birdRectangle.setPosition(birdPosition); // ---> bird rectangle updated with new values

            if (birdPosition.y < 0) {    // ---> Ground Collision
                isGameOver = true;
                backgroundMusic.stop();
                birdPosition.y = 0;
                birdVelocity.y = 0;
            }

            if (isGameComplete){
                backgroundMusic.stop();
                birdPosition.y = width / 2;
                birdVelocity.y = height / 2 - 10;
            }

            if (birdPosition.y > height - bird.getHeight()) { // ---> Ceiling Collision
                isGameOver = true;
                backgroundMusic.stop();
                birdPosition.y = height - bird.getHeight();
                birdVelocity.y = 0;
            }

            for (Pipe pipe : Pipes) {  // ---> Moving to the next pipe
                pipe.topPipePosition.x -= speed * Gdx.graphics.getDeltaTime();
                pipe.bottomPipePosition.x -= speed * Gdx.graphics.getDeltaTime();
                pipe.updateBounds();

                if (pipe.collides(birdRectangle)) {
                    backgroundMusic.stop();
                    isGameOver = true;   // ---> bird collided with the Pipe
                }

                if (!pipe.isScored() && pipe.getTopPipePosition().x < birdPosition.x) {
                    pipe.setScored(true);  // ---> Passed the Pipe
                    score++;
                }
            }
        } else {
            if (Gdx.input.justTouched()) {
                isGameStarted = true;  // --> starts the game after a click on the screen
                backgroundMusic.play();
            }
        }

        //I know, this is a bad way, but like this is the easiest

        if (score == 1) { speed = 100; }
        if (score == 2) { speed = 109; }
        if (score == 3) { speed = 100; }
        if (score == 5) { speed = 290; }
        if (score == 7) { speed = 120; }
        if (score == 10){ speed = 140; }
        if (score == 15){ speed = 160; }
        if (score == 16){ speed = 250; }
        if (score == 20){ speed = 180; }
        if (score == 25){ speed = 200; }
        if (score == 30){ speed = 220; }
        if (score == 35){ speed = 240; }
        if (score == 40){ speed = 260; }
        if (score == 45){ speed = 280; }
        if (score == 50){ speed = 300; }
        if (score == 55){ speed = 320; }
        if (score == 60){ speed = 340; }
        if (score == 65){ speed = 360; }
        if (score == 70){ speed = 360; }
        if (score == 75){ speed = 380; }
        if (score == 80){ speed = 400; }
    }

    @Override
    public void dispose() { // ---> disposes textures and our Sprite Batch
        batch.dispose();
        gameOver.dispose();
        background.dispose();
        bird.dispose();
        font.dispose();
        backgroundMusic.dispose();
        assetManager.dispose();
        for (Pipe pipe : Pipes) { pipe.dispose(); }
    }
}
