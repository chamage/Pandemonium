package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class OptionsScreen implements Screen {

    private final SierraGame game;
    final Screen previousScreen;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final Texture background;

    private SelectBox<String> resolutionDropdown;
    private CheckBox fullscreenCheck;


    public OptionsScreen(final SierraGame game, Screen previousScreen) {

        this.game = game;
        this.previousScreen = previousScreen;

        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.stage = new Stage(new ScreenViewport(), batch);

        background = new Texture(Gdx.files.internal("ui/gamebg.png"));
    }

    private void initializeUI(){
        Label.LabelStyle labelStyle = new Label.LabelStyle(SierraGame.fontMain, Color.WHITE);

        Label musicLabel = new Label("Music", labelStyle);
        musicLabel.setFontScale(1.2f);

        Slider musicSlider = new Slider(0f, 1f, 0.1f, false, skin);
        musicSlider.setValue(game.musicVolume);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.musicVolume = musicSlider.getValue();
            }
        });

        Label soundLabel = new Label("Sound", labelStyle);
        soundLabel.setFontScale(1.2f);

        Slider soundSlider = new Slider(0f, 1f, 0.1f, false, skin);
        soundSlider.setValue(game.soundVolume);
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.soundVolume = soundSlider.getValue();
            }
        });

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = SierraGame.fontMain;
        TextButton backButton = new TextButton("Back", buttonStyle);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.setScreen(previousScreen);
            }
        });

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle(skin.get(CheckBox.CheckBoxStyle.class));
        checkBoxStyle.font = SierraGame.fontMain;
        fullscreenCheck = new CheckBox(" Fullscreen", checkBoxStyle);
        fullscreenCheck.setChecked(Gdx.graphics.isFullscreen());
        fullscreenCheck.getLabel().setFontScale(1.1f);
        fullscreenCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fullscreenCheck.isChecked()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(1280, 720);
                }
            }
        });

        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle(skin.get(SelectBox.SelectBoxStyle.class));
        selectBoxStyle.font = SierraGame.fontMain;

        selectBoxStyle.listStyle = new List.ListStyle(skin.get(List.ListStyle.class));
        selectBoxStyle.listStyle.font = SierraGame.fontMain;
        resolutionDropdown = new SelectBox<>(selectBoxStyle);
        resolutionDropdown.setItems("800x600", "1024x768", "1280x720", "1920x1080");
        resolutionDropdown.setSelected("1280x720");
        resolutionDropdown.getList().setScale(1f);
        resolutionDropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!fullscreenCheck.isChecked()) {
                    String[] res = resolutionDropdown.getSelected().split("x");
                    Gdx.graphics.setWindowedMode(Integer.parseInt(res[0]), Integer.parseInt(res[1]));
                }
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center().pad(20);

        table.add(musicLabel).left().padBottom(10).row();
        table.add(musicSlider).width(500).padBottom(20).row();

        table.add(soundLabel).left().padBottom(10).row();
        table.add(soundSlider).width(500).padBottom(20).row();

        table.add(fullscreenCheck).left().padBottom(20).row();
        table.add(resolutionDropdown).width(250).padBottom(80).row();

        table.add().height(45).row();

        table.add(backButton).width(300).height(50).padTop(60).center();


        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();
        initializeUI();
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
