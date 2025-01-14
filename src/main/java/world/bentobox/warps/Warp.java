package world.bentobox.warps;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.util.Util;
import world.bentobox.level.Level;
import world.bentobox.warps.commands.WarpCommand;
import world.bentobox.warps.commands.WarpsCommand;
import world.bentobox.warps.config.Settings;

/**
 * Addin to BSkyBlock that enables welcome warp signs
 * @author tastybento
 *
 */
public class Warp extends Addon {
    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * This variable stores string for Level addon.
     */
    private static final String LEVEL_ADDON_NAME = "Level";

    /**
     * Warp panel Manager
     */
    private WarpPanelManager warpPanelManager;

    /**
     * Worlds Sign manager.
     */
    private WarpSignsManager warpSignsManager;

    /**
     * This variable stores in which worlds this addon is working.
     */
    private Set<World> registeredWorlds;

    /**
     * This variable stores if addon settings.
     */
    private Settings settings;

    /**
     * This variable stores if addon is hooked or not.
     */
    private boolean hooked;

    // ---------------------------------------------------------------------
    // Section: Methods
    // ---------------------------------------------------------------------


    /**
     * Executes code when loading the addon. This is called before {@link #onEnable()}. This should preferably
     * be used to setup configuration and worlds.
     */
    @Override
    public void onLoad()
    {
        super.onLoad();
        // Save default config.yml
        this.saveDefaultConfig();
        // Load the plugin's config
        this.loadSettings();
    }


    /**
     * Executes code when reloading the addon.
     */
    @Override
    public void onReload()
    {
        super.onReload();

        if (this.hooked) {
            this.warpSignsManager.saveWarpList();

            this.loadSettings();
            this.getLogger().info("WelcomeWarp addon reloaded.");
        }
    }


    @Override
    public void onEnable() {
        // Check if it is enabled - it might be loaded, but not enabled.
        if (!this.getPlugin().isEnabled()) {
            this.setState(State.DISABLED);
            return;
        }

        registeredWorlds = new HashSet<>();

        // Register commands
        this.getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
            if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName()))
            {
                if (gameModeAddon.getPlayerCommand().isPresent())
                {
                    this.registeredWorlds.add(gameModeAddon.getOverWorld());

                    new WarpCommand(this, gameModeAddon.getPlayerCommand().get());
                    new WarpsCommand(this, gameModeAddon.getPlayerCommand().get());
                    this.hooked = true;
                }
            }
        });

        if (hooked)
        {
            // Start warp signs
            warpSignsManager = new WarpSignsManager(this, this.getPlugin());
            warpPanelManager = new WarpPanelManager(this);
            // Load the listener
            getServer().getPluginManager().registerEvents(new WarpSignsListener(this), this.getPlugin());
        }
    }


    @Override
    public void onDisable(){
        // Save the warps
        if (warpSignsManager != null)
            warpSignsManager.saveWarpList();
    }


    /**
     * This method loads addon configuration settings in memory.
     */
    private void loadSettings() {
        this.settings = new Config<>(this, Settings.class).loadConfigObject();

        if (this.settings == null) {
            // Disable
            this.logError("WelcomeWarp settings could not load! Addon disabled.");
            this.setState(State.DISABLED);
        }
    }


    /**
     * Get warp panel manager
     * @return
     */
    public WarpPanelManager getWarpPanelManager() {
        return warpPanelManager;
    }

    public WarpSignsManager getWarpSignsManager() {
        return warpSignsManager;
    }

    public String getPermPrefix(World world) {
        return this.getPlugin().getIWM().getPermissionPrefix(world);
    }

    /**
     * Check if an event is in a registered world
     * @param world - world to check
     * @return true if it is
     */
    public boolean inRegisteredWorld(World world) {
        return registeredWorlds.contains(Util.getWorld(world));
    }

    /**
     * @return the settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Get the island level
     * @param world - world
     * @param uniqueId - player's UUID
     * @return island level or null if there is no level plugin
     */
    public Long getLevel(World world, UUID uniqueId) {
        return this.getPlugin().getAddonsManager().getAddonByName(LEVEL_ADDON_NAME).map(l -> ((Level) l).getIslandLevel(world, uniqueId)).orElse(null);
    }

}
