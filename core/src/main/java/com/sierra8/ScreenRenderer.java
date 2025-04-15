package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ScreenRenderer {
    static void UiScreenRender(float delta, SpriteBatch batch, Texture background, Stage stage) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    static void UiScreenRender(float delta, SpriteBatch batch, Texture background, Stage stage, Texture pansLogo) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int pansWidth = 420;
        int pansHeight = 94;

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(pansLogo, 20, 20, pansWidth, pansHeight);
        batch.end();

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    static void noBGScreenRender(float delta, SpriteBatch batch, Stage stage) {


        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }
}
