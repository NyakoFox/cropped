package gay.nyako.cropped;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "cropped")
public class CroppedConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean rainWatering = true;
    @ConfigEntry.Gui.Tooltip
    public float rainWateringMultiplier = 12.5f;
    @ConfigEntry.Gui.Tooltip
    public boolean dontBreakOnJump = true;
    @ConfigEntry.Gui.Tooltip
    public boolean rightClickHarvest = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public boolean dispenserPlanting = true;
}
