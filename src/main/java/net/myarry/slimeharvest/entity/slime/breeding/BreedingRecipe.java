package net.myarry.slimeharvest.entity.slime.breeding;

public record BreedingRecipe(
        String parent1,     // "magma"
        String parent2,     // "miner"
        String result,      // "coal"
        float chance        // 0.5
) {
    public boolean matches(String type1, String type2) {
        return (parent1.equals(type1) && parent2.equals(type2)) ||
                (parent1.equals(type2) && parent2.equals(type1));
    }
}
