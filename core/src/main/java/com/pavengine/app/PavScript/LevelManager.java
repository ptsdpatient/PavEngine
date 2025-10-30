package com.pavengine.app.PavScript;

import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavScreen.GameScreen.lanes;
import static com.pavengine.app.PavScreen.GameScreen.robots;
import static com.pavengine.app.PavScreen.GameWorld.levelStatusButton;

import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavScript.Enemies.Enemy;
import com.pavengine.app.PavScript.Enemies.EnemyBlueprint;
import com.pavengine.app.PavScript.Enemies.Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LevelManager {
    PavEngine game;
    private List<LevelData> levels = new ArrayList<>();
    private int currentLevelIndex = 0;
    private float levelTimer = 0;
    private float spawnTimer = 0;

    private Random random = new Random();
    // base spawn settings
    private float minSpawnInterval = 0.8f;
    private float maxSpawnInterval = 3.0f;
    private float variation = 0.3f;
    private Map<Integer, Integer> remainingEnemies = new HashMap<>();

    public LevelManager(String jsonPath, PavEngine game) {
        this.game = game;
        loadLevels(jsonPath);
        startLevel(0);
    }

    private void loadLevels(String path) {
        JsonValue root = new JsonReader().parse(Gdx.files.internal(path));
        for (JsonValue levelNode : root) {
            LevelData data = new LevelData();
            data.level = levelNode.getInt("level");
            data.duration = levelNode.getFloat("duration");
            JsonValue counts = levelNode.get("enemyCounts");
            for (JsonValue entry : counts) {
                data.enemyCounts.put(Integer.parseInt(entry.name), entry.asInt());
            }
            levels.add(data);
        }
    }

    private void startLevel(int index) {
        currentLevelIndex = index;
        remainingEnemies.clear();
        remainingEnemies.putAll(levels.get(index).enemyCounts);
//        levelTimer = -MathUtils.random(3, 7);
        levelTimer = 0f;
        spawnTimer = 0;
        Gdx.app.log("LevelManager", "Starting Level " + getCurrentLevel());
        if (currentLevelIndex > 0) {
            PavEngine.enableCursor = true;
            PavEngine.gamePause = true;
            lockCursor(false);
            PavEngine.levelStatus = true;
        }
    }

    public void update(float delta) {
        LevelData current = levels.get(currentLevelIndex);
        levelTimer += delta;
        spawnTimer -= delta;

        // Compute progress safely (0 → 1)
        float progress = Math.min(1f, Math.max(0f, levelTimer / current.duration));

        // Early exit if level duration is over
//        print(levelTimer + " , " + current.duration);

        if (levelTimer >= current.duration && robots.isEmpty()) {
            switch (MathUtils.random(1, 3)) {
                case 1: {
                    soundBox.playSound("/winning/1.mp3");
                }
                break;
                case 2: {
                    soundBox.playSound("/winning/2.mp3");
                }
                break;
                case 3: {
                    soundBox.playSound("/winning/3.mp3");
                }
                break;
            }
            levelStatusButton.text = "You Won! (Click to Continue)";
            nextLevel();
            return;
        }

        // Dynamically adjust interval to spread spawns across duration
        float dynamicInterval = getDynamicSpawnInterval(progress, current);

        // spawn logic
        if (spawnTimer <= 0 && hasEnemiesRemaining() && levelTimer >= 0f) {
            spawnEnemy();
            spawnTimer = dynamicInterval;
        }

        // auto move to next level when finished
        if (!hasEnemiesRemaining() && robots.isEmpty()) {
            nextLevel();
        }
    }

    private void spawnEnemy() {
        List<Integer> available = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : remainingEnemies.entrySet()) {
            if (entry.getValue() > 0) available.add(entry.getKey());
        }
        if (available.isEmpty()) return;

        int typeId = available.get(random.nextInt(available.size()));
        remainingEnemies.put(typeId, remainingEnemies.get(typeId) - 1);

        Enemy e = createEnemyById(typeId);
        if (e != null) robots.add(e);
    }

    private Enemy createEnemyById(int id) {
        switch (id) {
            case 1:
                return new Robot(lanes.random(), new EnemyBlueprint(
                    "sweeper",
                    new String[]{"Roll"},
                    0f,       // yOffset
                    7f,     // speed
                    50f,     // health
                    10f       // damage
                ));

            case 2:
                return new Robot(lanes.random(), new EnemyBlueprint(
                    "drone",
                    new String[]{""},
                    5f,       // yOffset
                    3.5f,       // speed
                    50f,      // health
                    15f        // damage
                ));

            case 3:
                return new Robot(lanes.random(), new EnemyBlueprint(
                    "spider",
                    new String[]{"Crawl"},
                    0f,       // yOffset
                    8f,       // speed
                    75f,      // health
                    30f        // damage
                ));

            case 4:
                return new Robot(lanes.random(), new EnemyBlueprint(
                    "doraemon",
                    new String[]{"Walk"},
                    0f,       // yOffset
                    3f,     // speed
                    180f,     // health
                    50f       // damage
                ));
            case 5:
                return new Robot(lanes.random(), new EnemyBlueprint(
                    "bipod",
                    new String[]{"Walk"},
                    0f,    // yOffset
                    7f,     // speed
                    200f,     // health
                    75f       // damage
                ));

            case 6:
                return new Robot(lanes.random(), new EnemyBlueprint(
                    "prime",
                    new String[]{"Walk"},
                    0f,       // yOffset
                    4f,     // speed
                    350f,     // health
                    75f       // damage
                ));

            default:
                return null;
        }
    }

    private float getDynamicSpawnInterval(float progress, LevelData current) {
        // total remaining enemies
        int remaining = 0;
        for (int c : remainingEnemies.values()) remaining += c;

        if (remaining <= 0) return 1f; // fallback

        // remaining time in this level
        float remainingTime = Math.max(0.1f, current.duration - levelTimer);

        // average spacing so they fill the level duration
        float targetInterval = remainingTime / remaining;

        // make it dynamic (start slow, end fast)
        float scale = 1f - 0.5f * progress; // reduces by 50% toward end
        targetInterval *= scale;

        // add small random variation
        float offset = (random.nextFloat() * 2 - 1) * variation;
        return Math.max(minSpawnInterval, targetInterval + offset);
    }

    private boolean hasEnemiesRemaining() {
        for (int count : remainingEnemies.values())
            if (count > 0) return true;
        return false;
    }

    private boolean isLevelComplete() {
        return !hasEnemiesRemaining();
    }

    private void nextLevel() {
        if (currentLevelIndex + 1 < levels.size()) {
            startLevel(currentLevelIndex + 1);
        } else {
            Gdx.app.log("LevelManager", "All levels complete!");
        }
    }

    public int getCurrentLevel() {
        return levels.get(currentLevelIndex).level;
    }

    // Returns 0.0 → 1.0
    public float getLevelProgress() {
        LevelData current = levels.get(currentLevelIndex);
        return Math.min(1f, levelTimer / current.duration);
    }

    public void restart() {

        print("restart");

        currentLevelIndex = 0;
        remainingEnemies.clear();

        for (int i = robots.size - 1; i >= 0; i--) {
            Enemy robot = robots.get(i);
            if (robot.getObject() != null) {
                sceneManager.removeScene(robot.getObject().scene); // or removeRenderable()
                targetObjects.removeValue(robot.getObject(), true);
            }
            robots.removeIndex(i);
        }


        LevelData first = levels.get(0);

        remainingEnemies.putAll(first.enemyCounts);
        levelTimer = -MathUtils.random(3, 7);
        spawnTimer = 0;

        PavEngine.health = 100;
        PavEngine.enableCursor = false;
        lockCursor(true);
        PavEngine.gamePause = false;
        PavEngine.levelStatus = false;

        startLevel(0);
        Gdx.app.log("LevelManager", "Restarting Game at Level " + first.level);
    }

    private static class LevelData {
        int level;
        float duration;
        Map<Integer, Integer> enemyCounts = new HashMap<>();
    }

}
