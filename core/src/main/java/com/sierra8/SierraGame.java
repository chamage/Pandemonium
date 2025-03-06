package com.sierra8;

import com.badlogic.gdx.Game;

public class SierraGame extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
