package io.teamplayer.teamcore.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.*;

/**
 * Allows changing color of colored materials (Wool, Stained Glass, etc.)
 */
public final class ColoredBlockUtil {

    private static final Map<String, DyeColor> materialNameByColor;
    /* Use a set of material names to check for validity of names later. Enum.valuesOf can throw an exception */
    private static final Set<String> materialNames;

    static {
        final ImmutableSet.Builder<String> materialBuilder = ImmutableSet.builder();

        for (Material material : Material.values()) {
            materialBuilder.add(material.name());
        }

        materialNames = materialBuilder.build();

        final ImmutableMap.Builder<String, DyeColor> materialColorBuilder = ImmutableMap.builder();
        final Set<String> validSterileNames = new HashSet<>();

        for (Material material : Material.values()) {
            String name = material.name();
            final Optional<DyeColor> color = getDyeColorStringMatch(name);

            if (!color.isPresent()) continue;
            name = sterilizeMaterial(name, color.get());

            if (validSterileNames.contains(name) || colorableToEveryColor(name)) {
                validSterileNames.add(name);
                materialColorBuilder.put(material.name(), color.get());
            }
        }

        materialNameByColor = materialColorBuilder.build();
    }

    private ColoredBlockUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /**
     * Check if a given material can have it's color changed
     *
     * @param material material
     * @return whether or not specified material is colorable
     */
    public static boolean isColoredBlock(Material material) {
        return materialNameByColor.containsKey(material.name());
    }

    /**
     * Get the corresponding dye color of given material
     * Option will be empty if given material is not colorable
     *
     * @param material material to get dye color of
     * @return the corresponding dye color of material
     */
    public static Optional<DyeColor> getMaterialColor(Material material) {
        return Optional.ofNullable(materialNameByColor.get(material.name()));
    }

    /**
     * Take a given origin material and convert the color of the material to specified new dye color
     * Optional will be empty if given material is not colorable
     *
     * @param originalMaterial material to change color of
     * @param newDyeColor corresponding dye color of new material
     * @return optional containing material of new color
     */
    public static Optional<Material> transformMaterialColor(Material originalMaterial, DyeColor newDyeColor) {
        final Optional<DyeColor> currentColor = getMaterialColor(originalMaterial);

        if (!currentColor.isPresent()) return Optional.empty();

        return getColoredMaterial(sterilizeMaterial(originalMaterial.name(), currentColor.get()), newDyeColor);
    }

    /**
     * Checks for any dye colors who names match the beginning of this material name
     * Due to "REDstone" and colored tulips existing, getting a matching dye doesn't ensure
     * this material is colorable
     *
     * @param materialName name of material
     * @return optional containing dye color matching beginning of material name
     */
    private static Optional<DyeColor> getDyeColorStringMatch(String materialName) {
        return Arrays.stream(DyeColor.values())
                .filter(dye -> materialName.startsWith(dye.name()))
                .findFirst();
    }

    /**
     * Remove the color part of the material name, but leaves trailing underscore intact
     * Method doesn't check to ensure it's removing color, it only removes the first n characters of the string
     * equal to the amount of characters in the specified color
     *
     * @param materialName name of material that contains color
     * @param color        color of the material
     * @return material name that excludes the color
     */
    private static String sterilizeMaterial(String materialName, DyeColor color) {
        return materialName.substring(color.name().length());
    }

    /**
     * Takes a sterilized material name an ensures it's a valid material when paired with every dye color
     * Useful because stuff like "BLACKstone" and "REDsand" aren't actually colorable
     *
     * @param materialName sterilized material color
     * @return whether or not this material works with every dye color
     */
    private static boolean colorableToEveryColor(String materialName) {
        return Arrays.stream(DyeColor.values())
                .map(color -> getColoredMaterial(materialName, color))
                .allMatch(Optional::isPresent);
    }

    /**
     * Takes a sterilized material name and dye color and gets the material in that dye color
     * Optional will be null if that colored material is invalid
     *
     * @param materialName sterilized material color
     * @param color desired color of new material
     * @return an optional containing new colored material
     */
    private static Optional<Material> getColoredMaterial(String materialName, DyeColor color) {
        final String name = color + materialName;

        if (materialNames.contains(name)) {
            return Optional.of(Material.valueOf(name));
        } else {
            return Optional.empty();
        }
    }
}
