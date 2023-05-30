package com.sunekaer.toolkit.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.stream.IntStream;

public class DataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(new LanguageGenerator(fabricDataGenerator));
    }

    private static class LanguageGenerator extends FabricLanguageProvider {
        public LanguageGenerator(FabricDataGenerator gen) {
            super(gen, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            translationBuilder.add("commands.toolkit.wip", "--This command doesn't function yet--");
            translationBuilder.add("commands.toolkit.hand.handempty", "--HAND IS EMPTY--");
            translationBuilder.add("commands.toolkit.clipboard.copied", "§e--Copied to Clipboard--");
            translationBuilder.add("commands.toolkit.oredist.missing", "Missing arguments §r[AreaSize]");
            translationBuilder.add("commands.toolkit.remove.lagwarring", "§c--WARNING-- Lag incoming.. removing a lot of blocks");
            translationBuilder.add("commands.toolkit.remove.done", "--Block removal done--");
            translationBuilder.add("commands.toolkit.la.missing", "Missing argument 0=block/1=fluid");
            translationBuilder.add("commands.unknown.argument", "--Unknown argument--");
            translationBuilder.add("commands.dragonslayer.name", "Slayer");
            translationBuilder.add("commands.toolkit.kill.type.all", "All");
            translationBuilder.add("commands.toolkit.kill.type.animals", "Animals");
            translationBuilder.add("commands.toolkit.kill.type.monsters", "Monsters");
            translationBuilder.add("commands.toolkit.kill.type.items", "Items");
            translationBuilder.add("commands.toolkit.kill.type.xp", "XP Orbs");
            translationBuilder.add("commands.toolkit.kill.type.players", "Players");
            translationBuilder.add("commands.toolkit.kill.type.me", "Current player");
            translationBuilder.add("commands.toolkit.kill.start", "Killing %s entities.");
            translationBuilder.add("commands.toolkit.kill.no", "No %s found!");
            translationBuilder.add("commands.toolkit.kill.done", "Removed %s entities.");
            translationBuilder.add("commands.toolkit.enchant.failed.missing_enchant", "%s does not have the enchantment %s");
            translationBuilder.add("commands.toolkit.enchant.success", "%s has been enchanted with %s");
            translationBuilder.add("commands.toolkit.remove_enchant.success", "%s has been removed from %s");
            translationBuilder.add("commands.toolkit.remove_enchant.failed", "%s could not be removed from %s");
            translationBuilder.add("commands.toolkit.repair.success", "%s has been repaired");
            translationBuilder.add("commands.toolkit.failed.missing_player", "This command can only be run from in-game as a player");

            for (int i = 11; i <= 255; i ++) {
                try {
                    translationBuilder.add("enchantment.level." + i, createRomanNumeral(i));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        private String createRomanNumeral(int number) throws IllegalAccessException {
            if (number > 99999) {
                throw new IllegalAccessException("You can not generate a number higher than 3999 in standard roman numerals");
            }

            String[] singles = new String[] {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
            String[] doubles = new String[] {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
            String[] triples = new String[] {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
            String[] quads   = IntStream.of(9).mapToObj("M"::repeat).toArray(String[]::new);

            return quads[number/1000] + triples[(number % 1000) / 100] + doubles[(number % 100) / 10] + singles[number % 10];
        }
    }
}
