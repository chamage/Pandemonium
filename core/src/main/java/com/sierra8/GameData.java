package com.sierra8;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class GameData {
    public Vector2 playerPosition;
    public List<Vector2> enemyPositions;
    public List<Vector2> bulletPositions;

    public GameData(Vector2 playerPosition, List<Vector2> enemyPositions, List<Vector2> bulletPositions) {
        this.playerPosition = playerPosition;
        this.enemyPositions = enemyPositions;
        this.bulletPositions = bulletPositions;
    }
}
