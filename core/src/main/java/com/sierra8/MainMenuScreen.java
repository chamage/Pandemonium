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

public class MainMenuScreen implements Screen {

    private final SierraGame game;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final Texture background;
    private final Texture pansLogo;

    public MainMenuScreen(final SierraGame game) {
        this.game = game;

        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.stage = new Stage(new ScreenViewport(), batch);

        background = new Texture("ui/gamebg.png");
        pansLogo = new Texture("ui/panslogo.png");

        initializeUI();
    }

    private void initializeUI() {
        // Label setup
        Label.LabelStyle labelStyle = new Label.LabelStyle(SierraGame.fontMain, Color.WHITE);
        Label label = new Label("SIERRA8", labelStyle);
        label.setFontScale(4f);
        label.setPosition(40, Gdx.graphics.getHeight() - 200f);

        // Button setup
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = SierraGame.fontMain;

        TextButton startButton = createButton("Start Game", 300, buttonStyle, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.gameScreen == null) {
                    game.gameScreen = new GameScreen(game);
                }
                game.setScreen(game.gameScreen);
            }
        });
        TextButton optionsButton = createButton("Options", 370, buttonStyle, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.optionsScreen == null || game.optionsScreen.previousScreen != MainMenuScreen.this) {
                    game.optionsScreen = new OptionsScreen(game, MainMenuScreen.this);
                }
                game.setScreen(game.optionsScreen);

            }
        });
        TextButton quitButton = createButton("Quit", 440, buttonStyle, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Actors to stage
        stage.addActor(label);
        stage.addActor(startButton);
        stage.addActor(optionsButton);
        stage.addActor(quitButton);
    }

    private TextButton createButton(String text, float yOffset, TextButton.TextButtonStyle buttonStyle, ChangeListener listener) {
        TextButton button = new TextButton(text, buttonStyle);
        button.getLabel().setFontScale(1.2f);
        button.setPosition(50f, Gdx.graphics.getHeight() - yOffset);
        button.setSize(400f, 50f);
        button.addListener(listener);
        return button;
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();
        initializeUI();

    }

    @Override
    public void render(float delta) {
        ScreenRenderer.UiScreenRender(delta, batch, background, stage, pansLogo);
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
