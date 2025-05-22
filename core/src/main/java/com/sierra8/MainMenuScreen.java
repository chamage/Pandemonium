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
import com.badlogic.gdx.scenes.scene2d.ui.Table;

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
        Label label = new Label("Pandemonium", labelStyle);
        label.setFontScale(3.5f);

        // Button setup
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = SierraGame.fontMain;

        // Using a Table for layout
        Table table = new Table();
        table.setFillParent(true);  // Make the table take the full screen
        table.left().top().padTop(60).padLeft(50);  // Align everything to the left with padding from top and left

        // Add elements to table
        table.add(label).left().padBottom(30).row();  // Add label with space at the bottom

        // Create buttons and add them to the table with spacing between them
        TextButton startButton = createButton("Start Game", buttonStyle, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.gameScreen == null) {
                    game.gameScreen = new GameScreen(game);
                }
                game.setScreen(game.gameScreen);
            }
        });

        TextButton optionsButton = createButton("Options", buttonStyle, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.optionsScreen == null || game.optionsScreen.previousScreen != MainMenuScreen.this) {
                    game.optionsScreen = new OptionsScreen(game, MainMenuScreen.this);
                }
                game.setScreen(game.optionsScreen);
            }
        });

        TextButton quitButton = createButton("Quit", buttonStyle, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Add buttons to table with equal spacing between them
        table.add(startButton).width(400).height(50).padBottom(15).row();
        table.add(optionsButton).width(400).height(50).padBottom(15).row();
        table.add(quitButton).width(400).height(50).padBottom(15).row();

        // Add table to stage
        stage.addActor(table);
    }

    private TextButton createButton(String text, TextButton.TextButtonStyle buttonStyle, ChangeListener listener) {
        TextButton button = new TextButton(text, buttonStyle);
        button.getLabel().setFontScale(1.2f);
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        background.dispose();
    }
}
