package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PlayerDeathScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final Texture background;

    public PlayerDeathScreen(final SierraGame game) {

        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new ScreenViewport(), batch);

        background = new Texture(Gdx.files.internal("ui/gamebg.png"));

        Label.LabelStyle labelStyle = new Label.LabelStyle(SierraGame.fontMain, Color.WHITE);
        Label label = new Label("You died!", labelStyle);
        label.setFontScale(3);
        label.setPosition(50, Gdx.graphics.getHeight() - 200);
        stage.addActor(label);


        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = SierraGame.fontMain;

        TextButton startButton = new TextButton("Restart", buttonStyle);
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
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenRenderer.UiScreenRender(delta, batch, background, stage);
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
        batch.dispose();
        background.dispose();
    }
}
