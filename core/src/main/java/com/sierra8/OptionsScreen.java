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
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class OptionsScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final Texture background;

    public OptionsScreen(final SierraGame game) {

        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        background = new Texture(Gdx.files.internal("ui/gamebg.png"));

        Label.LabelStyle labelStyle = new Label.LabelStyle(SierraGame.fontMain, Color.WHITE);
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
        buttonStyle.font = SierraGame.fontMain;

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
    }

    @Override
    public void show() {

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
