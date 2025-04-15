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

    public MainMenuScreen mainMenuScreen;
    public GameScreen gameScreen;
    public OptionsScreen optionsScreen;


    @Override
    public void create() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/ThaleahFat.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2f;
        parameter.borderColor = Color.BLACK;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = new Color(0, 0, 0, 0.5f);
        fontMain = generator.generateFont(parameter);
        parameter.size = 20;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2f;
        parameter.borderColor = Color.BLACK;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = new Color(0, 0, 0, 0.5f);
        fontSmaller = generator.generateFont(parameter);
        generator.dispose();

        musicVolume = 1f;
        soundVolume = 1f;

        mainMenuScreen = new MainMenuScreen(this);
        setScreen(mainMenuScreen);

    }
}
