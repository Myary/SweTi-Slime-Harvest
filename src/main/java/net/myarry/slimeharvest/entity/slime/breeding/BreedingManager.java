package net.myarry.slimeharvest.entity.slime.breeding;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BreedingManager {
    private static final Gson GSON = new Gson();
    private static final List<BreedingRecipe> RECIPES = new ArrayList<>();
    private static final Path CONFIG_DIR = Paths.get("config/slimeharvest/");

    public static void init() {
        RECIPES.clear();

        try {
            // Создаём папку если нет
            Files.createDirectories(CONFIG_DIR);

            Path recipesFile = CONFIG_DIR.resolve("breeding_recipes.json");

            // Если файла нет - создаём пример
            if (!Files.exists(recipesFile)) {
                createExampleRecipes(recipesFile);
            }

            // Читаем и парсим JSON
            String json = Files.readString(recipesFile);
            List<BreedingRecipe> loaded = GSON.fromJson(
                    json,
                    new TypeToken<List<BreedingRecipe>>(){}.getType()
            );

            if (loaded != null) {
                RECIPES.addAll(loaded);
                System.out.println("Загружено " + RECIPES.size() + " рецептов размножения");
            }

        } catch (Exception e) {
            System.err.println("Ошибка загрузки рецептов:");
            e.printStackTrace();
        }
    }

    private static void createExampleRecipes(Path file) throws Exception {
        List<BreedingRecipe> examples = Arrays.asList(
                new BreedingRecipe("natural", "miner", "coal", 0.5f),
                new BreedingRecipe("natural", "natural", "natural", 1.0f),
                new BreedingRecipe("coal", "coal", "coal", 1.0f)
        );

        String json = GSON.toJson(examples);
        Files.writeString(file, json);
        System.out.println("Создан файл с примерами: " + file);
    }

    public static BreedingRecipe findRecipe(String type1, String type2) {
        return RECIPES.stream()
                .filter(r -> r.matches(type1, type2))
                .findFirst()
                .orElse(null);
    }
}
