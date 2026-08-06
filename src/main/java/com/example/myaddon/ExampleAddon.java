

package com.example.myaddon;

import io.github.otiger.nexusprism.api.addon.AbstractNexusAddon;
import io.github.otiger.nexusprism.api.backpack.BackpackRegistry;
import io.github.otiger.nexusprism.api.chat.ChatRegistry;
import io.github.otiger.nexusprism.api.content.ContentLoadResult;
import io.github.otiger.nexusprism.api.discord.DiscordRegistry;
import io.github.otiger.nexusprism.api.economy.JobRegistry;
import io.github.otiger.nexusprism.api.energy.EnergyRegistry;
import io.github.otiger.nexusprism.api.events.EventsRegistry;
import io.github.otiger.nexusprism.api.holograms.HologramRegistry;
import io.github.otiger.nexusprism.api.mmo.MmoRegistry;
import io.github.otiger.nexusprism.api.protections.ProtectionsRegistry;
import io.github.otiger.nexusprism.api.traits.TraitsRegistry;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Example NexusPrism addon.
 *
 * <p>This class shows every major API feature in one place:
 * <ul>
 *   <li>Content loading (items, machines, recipes, infinity recipes) via YAML</li>
 *   <li>Programmatic machine processing recipe registration</li>
 *   <li>All provider lookups (MMO, chat, protections, events, holograms,
 *       backpacks, traits, discord, jobs, energy networks)</li>
 * </ul>
 *
 * <p>To create your own addon:
 * <ol>
 *   <li>Copy this project, change groupId/artifactId in pom.xml</li>
 *   <li>Update {@code nexusprism-addon.yml} (id, main class, etc.)</li>
 *   <li>Edit the YAML files and this class to implement your content</li>
 *   <li>{@code mvn package} → drop the jar in {@code addons/}</li>
 * </ol>
 */
public class ExampleAddon extends AbstractNexusAddon {

    @Override
    public void onEnable() {
        // ── 1. Load & register all YAML content ───────────────────────────────
        //
        // content() returns a fluent builder scoped to this addon.
        // Files are resolved from the addon's data folder first,
        // then from the addon JAR if not found on disk.
        ContentLoadResult result = content()
                .items("items.yml")           // registers custom items
                .machines("machines.yml")     // registers machine types
                .recipes("recipes.yml")       // registers standard crafting recipes
                .infinityRecipes("infinity_recipes") // loads all *.yml in the directory
                .register();

        // Log what was loaded (or any errors that occurred)
        result.logTo(getLogger());

        // ── 2. Register machine processing recipes programmatically ───────────
        //
        // Use this when you want to add input→output rules to a machine type
        // from Java code instead of YAML. These are checked by MachineEngine
        // alongside the YAML-defined recipes.
        MachineProcessingRegistry.register(
                MachineProcessingRecipe.builder("my_addon_bonus_smelt", "EXAMPLE_FURNACE")
                        .input("DIAMOND", 1)          // vanilla material
                        .input("EXAMPLE_DUST", 4)     // addon item ID
                        .output("EXAMPLE_INGOT", 3)   // addon item ID
                        .time(120)                    // ticks (0 = use machine default)
                        .source(getId())              // used for cleanup in onDisable
                        .build()
        );

        // ── 3. Providers — querying other NexusPrism systems ──────────────────
        //
        // Every provider is Optional — the module may not be loaded.
        // Always use isAvailable() or the Optional returned by get() before calling.

        // MMO system — stats, skills, mana, professions
        MmoRegistry.get().ifPresent(mmo -> {
            getLogger().info("MMO module is available — " + mmo + " loaded.");
            // Example usage (in a real addon this would be in a listener):
            // UUID uuid = player.getUniqueId();
            // int level  = mmo.getLevel(uuid);
            // int mana   = mmo.getCurrentMana(uuid);
            // boolean ok = mmo.consumeMana(uuid, 20);
        });

        // Chat — mute checks, channel broadcast
        ChatRegistry.get().ifPresent(chat -> {
            getLogger().info("Chat module is available.");
            // chat.isMuted(uuid)
            // chat.broadcastToChannel("global", "Hello from my addon!");
        });

        // Protections — region checks, duel state
        ProtectionsRegistry.get().ifPresent(prot -> {
            getLogger().info("Protections module is available.");
            // prot.isPvpAllowed(location)
            // prot.isInDuel(uuid)
        });

        // Events — blood moon, sacrifice arc, challenge boss
        EventsRegistry.get().ifPresent(events -> {
            getLogger().info("Events module is available.");
            // events.isBloodMoonActive()
            // events.getSacrificeStreak(uuid)
        });

        // Holograms — create/update floating text
        HologramRegistry.get().ifPresent(holo -> {
            getLogger().info("Holograms module is available.");
            // holo.create("my_addon_holo", location, List.of("&aHello!"))
        });

        // Backpacks
        BackpackRegistry.get().ifPresent(bp -> {
            getLogger().info("Backpack system is available.");
            // bp.getBackpackCount(uuid)
            // bp.openFirstBackpack(player)
        });

        // Traits (tarot card system)
        TraitsRegistry.get().ifPresent(traits -> {
            getLogger().info("Traits module is available.");
            // traits.getCards(uuid)          → List<String> card names
            // traits.getResearchLevel(uuid)
        });

        // Discord integration
        DiscordRegistry.get().ifPresent(discord -> {
            getLogger().info("Discord module is available.");
            // discord.sendMessage("server-log", "[MyAddon] Loaded successfully!");
            // discord.isLinked(uuid)
        });

        // Jobs
        JobRegistry.get().ifPresent(jobs -> {
            getLogger().info("Jobs module is available.");
            // jobs.getActiveJob(uuid)      → Optional<String> jobId
            // jobs.getLevel(uuid, "miner") → int level
        });

        // Energy networks — query networks, register custom components
        EnergyRegistry.get().ifPresent(energy -> {
            getLogger().info("Energy module is available — " + energy.getNetworkCount() + " network(s) active.");
            // energy.getNetworkAt(location)           → Optional<EnergyNetwork>
            // energy.getAllNetworks()                  → Collection<EnergyNetwork>
            // energy.registerComponent(myComponent)   → plug in a custom generator/consumer
            // energy.unregisterComponent(myComponent) → call in onDisable
        });

        getLogger().info("ExampleAddon enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Unregister all machine processing recipes contributed by this addon.
        // This is important to avoid stale entries if the addon is reloaded.
        MachineProcessingRegistry.unregisterBySource(getId());

        getLogger().info("ExampleAddon disabled.");
    }

    // ── Example helper: award XP to a player when they do something ───────────

    /**
     * Example: give a player MMO skill XP when they complete an addon action.
     * Call this from your event listeners.
     */
    public void awardMiningXp(Player player, long amount) {
        UUID uuid = player.getUniqueId();
        MmoRegistry.get().ifPresent(mmo -> mmo.addSkillXp(uuid, "mining", amount));
    }

    /**
     * Example: check if a player is allowed to use a feature
     * (not in a region that blocks interaction, not muted for chat features).
     */
    public boolean canUseAddonFeature(Player player) {
        UUID uuid = player.getUniqueId();

        // Block if the player is in an enemy clan's territory and PvP is on
        boolean pvpZone = ProtectionsRegistry.get()
                .map(p -> p.isPvpAllowed(player.getLocation()))
                .orElse(false);

        // Block if the player is currently in a duel
        boolean inDuel = ProtectionsRegistry.get()
                .map(p -> p.isInDuel(uuid))
                .orElse(false);

        return !inDuel && !pvpZone;
    }
}
