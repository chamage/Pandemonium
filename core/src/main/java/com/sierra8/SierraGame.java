package com.sierra8;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

@SuppressWarnings("GDXJavaStaticResource")
public class SierraGame extends Game {

    protected static BitmapFont fontMain;
    protected static BitmapFont fontSmaller;

    public float musicVolume;
    public float soundVolume;

    @Override
    public void create() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/ThaleahFat.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = Color.WHITE;
        fontMain = generator.generateFont(parameter);
        parameter.size = 20;
        fontSmaller = generator.generateFont(parameter);
        generator.dispose();

        musicVolume = 1f;
        soundVolume = 1f;

        setScreen(new MainMenuScreen(this));
    }
}
