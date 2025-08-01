package io.github.sefiraat.equivalencytech.recipes;

import io.github.sefiraat.equivalencytech.EMCShopMiragEdge;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipes {

    public Recipes(EMCShopMiragEdge plugin) {
        addRecipes(plugin);
    }

    private void addRecipes(EMCShopMiragEdge plugin) {
        plugin.getServer().addRecipe(Recipes.recipeCoal1(plugin));
        plugin.getServer().addRecipe(Recipes.recipeCoal2(plugin));
        plugin.getServer().addRecipe(Recipes.recipeCoal3(plugin));
        plugin.getServer().addRecipe(Recipes.recipeDarkMatter(plugin));
        plugin.getServer().addRecipe(Recipes.recipeRedMatter(plugin));
        plugin.getServer().addRecipe(Recipes.recipeTransmutationOrb(plugin));
        plugin.getServer().addRecipe(Recipes.recipeDissolutionChest(plugin));
        plugin.getServer().addRecipe(Recipes.recipeCondensatorChest(plugin));
    }

    public static Recipe recipeCoal1(EMCShopMiragEdge plugin) {
        ItemStack i = plugin.getEqItems().getAlchemicalCoal().getItemClone();
        NamespacedKey key = new NamespacedKey(plugin, "coal1");
        ShapedRecipe r = new ShapedRecipe(key, i);
        r.shape("NNN","NN ","   ");
        r.setIngredient('N', Material.COAL);
        return r;
    }

    public static List<ItemStack> recipeCoal1Check() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemStack(Material.COAL));
        itemStacks.add(new ItemStack(Material.COAL));
        itemStacks.add(new ItemStack(Material.COAL));
        itemStacks.add(new ItemStack(Material.COAL));
        itemStacks.add(new ItemStack(Material.COAL));
        itemStacks.add(null);
        itemStacks.add(null);
        itemStacks.add(null);
        itemStacks.add(null);
        return itemStacks;
    }

    public static Recipe recipeCoal2(EMCShopMiragEdge plugin) {
        ItemStack i = plugin.getEqItems().getMobiusFuel().getItemClone();
        NamespacedKey key = new NamespacedKey(plugin, "coal2");
        ShapedRecipe r = new ShapedRecipe(key, i);
        r.shape("NNN","NN ","   ");
        r.setIngredient('N', Material.PLAYER_HEAD);
        return r;
    }

    public static List<ItemStack> recipeCoal2Check(EMCShopMiragEdge plugin) {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(plugin.getEqItems().getAlchemicalCoal().getItemClone());
        itemStacks.add(plugin.getEqItems().getAlchemicalCoal().getItemClone());
        itemStacks.add(plugin.getEqItems().getAlchemicalCoal().getItemClone());
        itemStacks.add(plugin.getEqItems().getAlchemicalCoal().getItemClone());
        itemStacks.add(plugin.getEqItems().getAlchemicalCoal().getItemClone());
        itemStacks.add(null);
        itemStacks.add(null);
        itemStacks.add(null);
        itemStacks.add(null);
        return itemStacks;
    }

    public static Recipe recipeCoal3(EMCShopMiragEdge plugin) {
        ItemStack i = plugin.getEqItems().getAeternalisFuel().getItemClone();
        NamespacedKey key = new NamespacedKey(plugin, "coal3");
        ShapedRecipe r = new ShapedRecipe(key, i);
        r.shape("NNN","NN ","   ");
        r.setIngredient('N', Material.PLAYER_HEAD);
        return r;
    }

    public static List<ItemStack> recipeCoal3Check(EMCShopMiragEdge plugin) {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(null);
        itemStacks.add(null);
        itemStacks.add(null);
        itemStacks.add(null);
        return itemStacks;
    }


    public static Recipe recipeDarkMatter(EMCShopMiragEdge plugin) {
        ItemStack i = plugin.getEqItems().getDarkMatter().getItemClone();
        NamespacedKey key = new NamespacedKey(plugin, "darkmatter");
        ShapedRecipe r = new ShapedRecipe(key, i);
        r.shape("NNN","NEN","NNN");
        r.setIngredient('N', Material.PLAYER_HEAD);
        r.setIngredient('E', Material.NETHERITE_BLOCK);
        return r;
    }

    public static List<ItemStack> recipeDarkMatterCheck(EMCShopMiragEdge plugin) {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(new ItemStack(Material.NETHERITE_BLOCK));
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        return itemStacks;
    }

    public static Recipe recipeRedMatter(EMCShopMiragEdge plugin) {
        ItemStack i = plugin.getEqItems().getDarkMatter().getItemClone();
        NamespacedKey key = new NamespacedKey(plugin, "redmatter");
        ShapedRecipe r = new ShapedRecipe(key, i);
        r.shape("NNN","EEE","NNN");
        r.setIngredient('N', Material.PLAYER_HEAD);
        r.setIngredient('E', Material.PLAYER_HEAD);
        return r;
    }

    public static List<ItemStack> recipeRedMatterCheck(EMCShopMiragEdge plugin) {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getDarkMatter().getItemClone());
        itemStacks.add(plugin.getEqItems().getDarkMatter().getItemClone());
        itemStacks.add(plugin.getEqItems().getDarkMatter().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        return itemStacks;
    }

    public static Recipe recipeTransmutationOrb(EMCShopMiragEdge plugin) {
        ItemStack i = plugin.getEqItems().getDarkMatter().getItemClone();
        NamespacedKey key = new NamespacedKey(plugin, "t_orb");
        ShapedRecipe r = new ShapedRecipe(key, i);
        r.shape("BDB","DRD","BDB");
        r.setIngredient('B', Material.DIAMOND_BLOCK);
        r.setIngredient('D', Material.PLAYER_HEAD);
        r.setIngredient('R', Material.PLAYER_HEAD);
        return r;
    }


    public static List<ItemStack> recipeTransmutationOrbCheck(EMCShopMiragEdge plugin) {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemStack(Material.DIAMOND_BLOCK));
        itemStacks.add(plugin.getEqItems().getDarkMatter().getItemClone());
        itemStacks.add(new ItemStack(Material.DIAMOND_BLOCK));
        itemStacks.add(plugin.getEqItems().getDarkMatter().getItemClone());
        itemStacks.add(plugin.getEqItems().getRedMatter().getItemClone());
        itemStacks.add(plugin.getEqItems().getDarkMatter().getItemClone());
        itemStacks.add(new ItemStack(Material.DIAMOND_BLOCK));
        itemStacks.add(plugin.getEqItems().getDarkMatter().getItemClone());
        itemStacks.add(new ItemStack(Material.DIAMOND_BLOCK));
        return itemStacks;
    }

    public static List<ItemStack> recipeDissolutionChestCheck(EMCShopMiragEdge plugin) {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(new ItemStack(Material.DIAMOND_BLOCK));
        itemStacks.add(new ItemStack(Material.CHEST));
        itemStacks.add(new ItemStack(Material.DIAMOND_BLOCK));
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getMobiusFuel().getItemClone());
        return itemStacks;
    }

    public static Recipe recipeDissolutionChest(EMCShopMiragEdge plugin) {
        ItemStack i = plugin.getEqItems().getDarkMatter().getItemClone();
        NamespacedKey key = new NamespacedKey(plugin, "d_chest");
        ShapedRecipe r = new ShapedRecipe(key, i);
        r.shape("MMM","DCD","MMM");
        r.setIngredient('M', Material.PLAYER_HEAD);
        r.setIngredient('D', Material.DIAMOND_BLOCK);
        r.setIngredient('C', Material.CHEST);
        return r;
    }

    public static List<ItemStack> recipeCondensatorChestCheck(EMCShopMiragEdge plugin) {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(new ItemStack(Material.NETHERITE_BLOCK));
        itemStacks.add(plugin.getEqItems().getDissolutionChest().getItemClone());
        itemStacks.add(new ItemStack(Material.NETHERITE_BLOCK));
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        itemStacks.add(plugin.getEqItems().getAeternalisFuel().getItemClone());
        return itemStacks;
    }

    public static Recipe recipeCondensatorChest(EMCShopMiragEdge plugin) {
        ItemStack i = plugin.getEqItems().getDarkMatter().getItemClone();
        NamespacedKey key = new NamespacedKey(plugin, "c_chest");
        ShapedRecipe r = new ShapedRecipe(key, i);
        r.shape("MMM","DCD","MMM");
        r.setIngredient('M', Material.PLAYER_HEAD);
        r.setIngredient('D', Material.NETHERITE_BLOCK);
        r.setIngredient('C', Material.CHEST);
        return r;
    }

    public static Map<List<ItemStack>, ItemStack> getEQRecipes(EMCShopMiragEdge plugin) {
        Map<List<ItemStack>, ItemStack> recipes = new HashMap<>();
        recipes.put(recipeCoal1Check(), plugin.getEqItems().getAlchemicalCoal().getItemClone());
        recipes.put(recipeCoal2Check(plugin), plugin.getEqItems().getMobiusFuel().getItemClone());
        recipes.put(recipeCoal3Check(plugin), plugin.getEqItems().getAeternalisFuel().getItemClone());
        recipes.put(recipeDarkMatterCheck(plugin), plugin.getEqItems().getDarkMatter().getItemClone());
        recipes.put(recipeRedMatterCheck(plugin), plugin.getEqItems().getRedMatter().getItemClone());
        recipes.put(recipeTransmutationOrbCheck(plugin), plugin.getEqItems().getTransmutationOrb().getItemClone());
        recipes.put(recipeDissolutionChestCheck(plugin), plugin.getEqItems().getDissolutionChest().getItemClone());
        recipes.put(recipeCondensatorChestCheck(plugin), plugin.getEqItems().getCondensatorChest().getItemClone());
        return recipes;
    }

    public static List<ItemStack> getEQRecipe(EMCShopMiragEdge plugin, ItemStack itemStack) {
        if (itemStack.equals(plugin.getEqItems().getAlchemicalCoal().getItem())) {
            return recipeCoal1Check();
        } else if (itemStack.equals(plugin.getEqItems().getMobiusFuel().getItem())) {
            return recipeCoal2Check(plugin);
        } else if (itemStack.equals(plugin.getEqItems().getAeternalisFuel().getItem())) {
            return recipeCoal3Check(plugin);
        } else if (itemStack.equals(plugin.getEqItems().getDarkMatter().getItem())) {
            return recipeDarkMatterCheck(plugin);
        } else if (itemStack.equals(plugin.getEqItems().getRedMatter().getItem())) {
            return recipeRedMatterCheck(plugin);
        } else if (itemStack.equals(plugin.getEqItems().getTransmutationOrb().getItem())) {
            return recipeTransmutationOrbCheck(plugin);
        } else if (itemStack.equals(plugin.getEqItems().getDissolutionChest().getItem())) {
            return recipeDissolutionChestCheck(plugin);
        } else if (itemStack.equals(plugin.getEqItems().getCondensatorChest().getItem())) {
            return recipeCondensatorChestCheck(plugin);
        }
        return new ArrayList<>();
    }

}