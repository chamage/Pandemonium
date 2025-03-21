package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class OptionsScreen implements Screen {

    final SierraGame game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Texture background;

    public OptionsScreen(final SierraGame game) {
        this.game = game;

        batch = new SpriteBatch();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.fontMain, Color.WHITE);
        Label label = new Label("Music", labelStyle);
        label.setFontScale(1.2f);
        label.setPosition(50, Gdx.graphics.getHeight() - 200);
        stage.addActor(label);

        Slider volume = new Slider(0f, 1f, 0.1f, false, skin);
        volume.setPosition(200, Gdx.graphics.getHeight() - 190);
        volume.setValue(1f);
        volume.setSize(500, 20);
        volume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.musicVolume = volume.getValue();
            }
        });

        Label label2 = new Label("Sound", labelStyle);
        label2.setFontScale(1.2f);
        label2.setPosition(50, Gdx.graphics.getHeight() - 250);
        stage.addActor(label2);

        Slider volume2 = new Slider(0f, 1f, 0.1f, false, skin);
        volume2.setPosition(200, Gdx.graphics.getHeight() - 240);
        volume2.setValue(1f);
        volume2.setSize(500, 20);
        volume2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.soundVolume = volume2.getValue();
            }
        });

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = game.fontMain;

        TextButton startButton = new TextButton("Back", buttonStyle);
        startButton.setPosition(50, 50);
        startButton.setSize(400, 50);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                stage.dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(startButton);
        stage.addActor(volume);
        stage.addActor(volume2);

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
        batch.dispose();
    }
}
