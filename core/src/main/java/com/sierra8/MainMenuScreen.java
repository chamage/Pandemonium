package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenuScreen implements Screen {

    final SierraGame game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture background;

    public MainMenuScreen(final SierraGame game) {
        this.game = game;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/ThaleahFat.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 82;
        parameter.color = Color.WHITE;

        batch = new SpriteBatch();
        font = generator.generateFont(parameter);


        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label("SIERRA8", labelStyle);
        label.setFontScale(3);
        label.setPosition(50, Gdx.graphics.getHeight() - 200);
        stage.addActor(label);

        parameter.size = 42;
        font = generator.generateFont(parameter);
        generator.dispose();


        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = font;

        TextButton startButton = new TextButton("Start Game", buttonStyle);
        startButton.setPosition(50, Gdx.graphics.getHeight() - 300);
        startButton.setSize(400, 50);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                stage.dispose();
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton quitButton = new TextButton("Quit", buttonStyle);
        quitButton.setPosition(50, Gdx.graphics.getHeight() - 360);
        quitButton.setSize(400, 50);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Gdx.app.exit();
            }
        });

        stage.addActor(startButton);
        stage.addActor(quitButton);

        background = new Texture(Gdx.files.internal("ui/gamebg.png"));

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        font.dispose();
        batch.dispose();
    }
}
