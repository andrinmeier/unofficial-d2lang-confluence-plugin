package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import java.util.HashMap;
import java.util.Map;

public final class DiagramParameters {
    private final int themeId;
    private final String layoutEngine;
    private final boolean sketchMode;

    private static final Map<String, Integer> themeNameToIdMap = new HashMap<>();

    static {
        themeNameToIdMap.put("default", 0);
        themeNameToIdMap.put("neutral grey", 1);
        themeNameToIdMap.put("flagship terrastruct", 3);
        themeNameToIdMap.put("cool classics", 4);
        themeNameToIdMap.put("mixed berry blue", 5);
        themeNameToIdMap.put("grape soda", 6);
        themeNameToIdMap.put("aubergine", 7);
        themeNameToIdMap.put("colorblind clear", 8);
        themeNameToIdMap.put("vanilla nitro cola", 100);
        themeNameToIdMap.put("orange creamsicle", 101);
        themeNameToIdMap.put("shirley temple", 102);
        themeNameToIdMap.put("earth tones", 103);
        themeNameToIdMap.put("everglade green", 104);
        themeNameToIdMap.put("buttered toast", 105);
    }

    public DiagramParameters(final int themeId, final String layoutEngine, final boolean sketchMode) {
        this.themeId = themeId;
        this.layoutEngine = layoutEngine;
        this.sketchMode = sketchMode;
    }

    public int getThemeId() {
        return themeId;
    }

    public String getLayoutEngine() {
        return layoutEngine;
    }

    public boolean isSketchMode() {
        return sketchMode;
    }

    public String asString() {
        return String.format("%s/%s/%s", themeId, layoutEngine, sketchMode);
    }

    public static DiagramParameters fromMap(final Map<String, String> parameterMap) {
        final String theme = parameterMap.get("theme") == null ? "default" : parameterMap.get("theme");
        final String layoutEngine = parameterMap.get("layoutEngine") == null ? "dagre" : parameterMap.get("layoutEngine");
        final boolean sketchMode = parameterMap.get("sketchMode") == null ? false : Boolean.parseBoolean(parameterMap.get("sketchMode"));
        return new DiagramParameters(
                themeNameToIdMap.get(theme),
                layoutEngine,
                sketchMode
        );
    }
}
