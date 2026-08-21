package net.anvian.chiseledbookshelfvisualizer.client.config;

import net.anvian.chiseledbookshelfvisualizer.ChiseledBookshelfVisualizerMod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LegacyConfigMigration {
    private static final Pattern SCALE = Pattern.compile("(?:\"scale\"|scale)\\s*[:=]\\s*([^,}\\n]+)");
    private static final Pattern USE_ROMAN = Pattern.compile("(?:\"useRoman\"|useRoman)\\s*[:=]\\s*([^,}\\n]+)");

    private LegacyConfigMigration() {}

    public record Values(Double scale, Boolean useRoman) {}

    public static Optional<Values> read(Path configDir) {
        Path target = configDir.resolve(ChiseledBookshelfVisualizerMod.MOD_ID + ".toml");
        if (Files.isRegularFile(target)) {
            return Optional.empty();
        }

        Path legacy = findLegacyFile(configDir);
        if (legacy == null) {
            return Optional.empty();
        }

        try {
            String contents = Files.readString(legacy).replaceAll("(?m)//.*$|#.*$", "");
            Double scale = matchDouble(contents);
            Boolean useRoman = matchBoolean(contents);
            if (scale == null && useRoman == null) {
                throw new IllegalArgumentException("no recognized settings");
            }
            ChiseledBookshelfVisualizerMod.LOGGER.info("Migrating legacy config from {}", legacy);
            return Optional.of(new Values(scale, useRoman));
        } catch (IOException | RuntimeException exception) {
            ChiseledBookshelfVisualizerMod.LOGGER.warn("Could not migrate legacy config from {}", legacy, exception);
            return Optional.empty();
        }
    }

    private static Path findLegacyFile(Path configDir) {
        Path directory = configDir.resolve(ChiseledBookshelfVisualizerMod.MOD_ID);
        for (String name : new String[] {
            ChiseledBookshelfVisualizerMod.MOD_ID + "-config.json",
            ChiseledBookshelfVisualizerMod.MOD_ID + "-config.json5",
            ChiseledBookshelfVisualizerMod.MOD_ID + "-config.toml"
        }) {
            Path candidate = directory.resolve(name);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static Double matchDouble(String contents) {
        Matcher matcher = LegacyConfigMigration.SCALE.matcher(contents);
        return matcher.find() ? Double.valueOf(matcher.group(1).trim()) : null;
    }

    private static Boolean matchBoolean(String contents) {
        Matcher matcher = LegacyConfigMigration.USE_ROMAN.matcher(contents);
        if (!matcher.find()) {
            return null;
        }
        String value = matcher.group(1).trim();
        if (!value.equals("true") && !value.equals("false")) {
            throw new IllegalArgumentException("invalid boolean value");
        }
        return Boolean.valueOf(value);
    }
}
