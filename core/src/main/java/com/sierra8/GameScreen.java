package com.sierra8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Random;


public class GameScreen implements Screen {

    // Core game ref
    private final SierraGame game;

    // Utilities
    private final Random random = new Random();
    private boolean paused = false;

    // Camera and render
    private OrthographicCamera camera;
    private ShapeRenderer shape;
    private SpriteBatch batch;

    // Assets
    private Sound shootSound;
    private Music[] musicTracks;
    private int currentTrackIndex = 0;
    private Texture grassTexture;

    // Game components
    private Player player;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;

    private Stage pauseStage;
    private Skin skin;

    Array<RenderableEntity> renderables = new Array<>();

    public GameScreen(final SierraGame game){
        this.game = game;

        initialize();
        loadAssets();
    }

    private void initialize(){

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shape = new ShapeRenderer();
        batch = new SpriteBatch();

        player = new Player(0, 0);
        enemyManager = new EnemyManager(.4f, 80, 316f);
        objectManager = new ObjectManager(.1f, 10);

        enemyManager.setPlayerDeathListener(() -> {
            stopTrack();
            dispose();
            game.setScreen(new PlayerDeathScreen(game));
        });

        enemyManager.setEnemyDeathListener(() -> player.enemyKilled());

        player.setPistolShootListener(() -> shootSound.play(game.soundVolume));

        player.setTeleportListener(oldPos -> enemyManager.startTargetDelay(oldPos, .4f));

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.font = SierraGame.fontMain;

        pauseStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(null);

        Table table = new Table();
        table.setFillParent(true);
        pauseStage.addActor(table);

        TextButton resumeButton = new TextButton("Resume", buttonStyle);
        TextButton optionsButton = new TextButton("Options", buttonStyle);
        TextButton quitButton = new TextButton("Quit", buttonStyle);

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resume();
            }
        });

        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.optionsScreen == null || game.optionsScreen.previousScreen != GameScreen.this) {
                    game.optionsScreen = new OptionsScreen(game, GameScreen.this);
                }
                game.setScreen(game.optionsScreen);
            }
        });
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        table.add(resumeButton).width(300).height(50).pad(10).row();
        table.add(optionsButton).width(300).height(50).pad(10).row();
        table.add(quitButton).width(300).height(50).pad(10);
    }

    private void loadAssets(){
        musicTracks = new Music[8];
        for (int i = 0; i < musicTracks.length; i++){
            musicTracks[i] = Gdx.audio.newMusic(Gdx.files.internal("music/loop" + (i+1) + ".mp3"));
        }

        shootSound = Gdx.audio.newSound(Gdx.files.internal("sound/shoot.mp3"));

        grassTexture = new Texture(Gdx.files.internal("textures/grass.png"));

        playTrack();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(paused ? pauseStage : null);
        Gdx.input.justTouched();
        Gdx.input.setCursorCatched(false);
        musicTracks[currentTrackIndex].setVolume(game.musicVolume);
    }

    @Override
    public void render(float delta) {
        clearScreen();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            if (paused) resume();
            else pause();
        }

        renderGame();

        if (!paused) {
            update(delta);
        }
        else{
            noUpdate();
        }
    }

    private void update(float delta){
        renderables.clear();
        renderables.add(player);
        for(Enemy enemy : enemyManager.getEnemies()){
            if (!enemy.isDead()){
                renderables.add(enemy);
            }
        }
        for (Object object : objectManager.getObjects()) {
            renderables.add(object);
        }
        renderables.sort((a, b) -> Float.compare(b.getRenderY(), a.getRenderY()));
        player.update(delta, camera, objectManager.getObjects());
        enemyManager.update(delta, player, player.getBullets());
        objectManager.update(delta, player);
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
    }

    private void noUpdate(){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0, 0, 0, 0.7f);
        shape.rect(camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth, camera.viewportHeight);
        shape.end();

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        pauseStage.act(Gdx.graphics.getDeltaTime());
        pauseStage.draw();
    }

    private void renderGame(){
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        createBackground();
        player.renderBullets(shape);
        shape.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (RenderableEntity entity : renderables){
            entity.render(batch);
        }

        String debug = "X: " + player.getPosition().x + " Y: " + player.getPosition().y
            + " Bullets: " + player.getBullets().size() + " Enemies: " + enemyManager.getEnemies().size() + " FPS: " + Gdx.graphics.getFramesPerSecond();
        SierraGame.fontSmaller.draw(batch, debug, camera.position.x - camera.viewportWidth / 2 + 5,
            camera.position.y + camera.viewportHeight / 2 - 10);
        String killStreak = "Enemies killed: " + player.getEnemiesKilled() + " Chasing: " + enemyManager.getStateCount("chasing");
        SierraGame.fontSmaller.draw(batch, killStreak, player.getPosition().x + 30, player.getPosition().y + 30);

        player.renderAbilities(batch);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        player.renderStaminaBar(shape);
        player.renderManaBar(shape);
        player.renderAbilitiesUsed(shape);

        /* DEBUG STUFF
        player.renderBoxes(shape);
        enemyManager.renderBoxes(shape);
         */

        shape.end();
    }

    private void clearScreen(){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();
        pauseStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        paused = true;
        Gdx.input.setInputProcessor(pauseStage);
    }

    @Override
    public void resume() {
        paused = false;
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        stopTrack();
        for (Music music : musicTracks){
            music.dispose();
        }
    }

    private void createBackground() {
        float textureWidth = grassTexture.getWidth();
        float textureHeight = grassTexture.getHeight();

        float camX = camera.position.x - camera.viewportWidth / 2f;
        float camY = camera.position.y - camera.viewportHeight / 2f;

        float offsetX = camX % textureWidth;
        float offsetY = camY % textureHeight;

        if (offsetX < 0) offsetX += textureWidth;
        if (offsetY < 0) offsetY += textureHeight;

        int tilesX = (int)Math.ceil(camera.viewportWidth / textureWidth) + 2;
        int tilesY = (int)Math.ceil(camera.viewportHeight / textureHeight) + 2;

        batch.begin();
        for (int x = -1; x < tilesX - 1; x++) {
            for (int y = -1; y < tilesY - 1; y++) {
                batch.draw(
                    grassTexture,
                    camX - offsetX + x * textureWidth,
                    camY - offsetY + y * textureHeight
                );
            }
        }
        batch.end();
    }

    private void playTrack(){
        if (musicTracks[currentTrackIndex].isPlaying()){stopTrack();}
        currentTrackIndex = random.nextInt(musicTracks.length);
        musicTracks[currentTrackIndex].setLooping(false);
        musicTracks[currentTrackIndex].setVolume(game.musicVolume);
        musicTracks[currentTrackIndex].play();

        musicTracks[currentTrackIndex].setOnCompletionListener(music -> playTrack());
    }

    private void stopTrack(){
        for (Music music : musicTracks){
            if (music.isPlaying()){
                music.stop();
            }
        }
    }
}
