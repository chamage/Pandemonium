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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlayerDeathScreen implements Screen {
    private final SierraGame game;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final Texture background;
    private final Texture goblin;
    private final Texture peter;

    public PlayerDeathScreen(final SierraGame game) {
        this.game = game;

        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.stage = new Stage(new ScreenViewport(), batch);

        background = new Texture(Gdx.files.internal("ui/gamebg.png"));
        goblin = new Texture(Gdx.files.internal("textures/goblin.png"));
        peter = new Texture(Gdx.files.internal("textures/death.png"));

        initializeUI();
    }

    private void initializeUI() {
        // Label Style
        Label.LabelStyle labelStyle = new Label.LabelStyle(SierraGame.fontMain, Color.WHITE);
        Label label = new Label("You died!", labelStyle);
        label.setFontScale(3);

        // Layout using Table
        Table table = new Table();
        table.setFillParent(true); // Make the table take the full screen
        table.top().padTop(100);  // Add padding from top

        // Center the label
        table.add(label).center().padBottom(50).row();

        // Restart Button
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = SierraGame.fontMain;

        TextButton startButton = new TextButton("Restart", buttonStyle);
        startButton.setSize(400, 50);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                stage.dispose();
                game.setScreen(new GameScreen(game));
            }
        });

        // Quit Button
        TextButton quitButton = new TextButton("Quit", buttonStyle);
        quitButton.setSize(400, 50);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Add buttons to table with spacing
        table.add(startButton).width(400).height(50).padBottom(20).row();
        table.add(quitButton).width(400).height(50).padBottom(20).row();

        // Add table to stage
        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float goblinOgWidth = 253f;
        float goblinOgHeight = 309f;

        float peterOgWidth = 932f;
        float peterOgHeight = 587f;

        float screenHeight = Gdx.graphics.getHeight();
        float scaleGoblin = screenHeight / goblinOgHeight;
        float scalePeter = screenHeight/2f / peterOgHeight;

        float scaledWidthGoblin = goblinOgWidth * scaleGoblin;
        float scaledWidthPeter = peterOgWidth * scalePeter;
        float scaledHeight = screenHeight;
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(peter, scaledWidthGoblin - scaledWidthGoblin /3.5f, scaledHeight/3f, scaledWidthPeter, scaledHeight/2f);
        batch.draw(goblin, 0, 0, scaledWidthGoblin, scaledHeight);
        batch.end();

        ScreenRenderer.noBGScreenRender(delta, batch, stage);
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
