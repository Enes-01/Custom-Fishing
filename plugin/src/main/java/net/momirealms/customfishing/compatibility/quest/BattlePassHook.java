package net.momirealms.customfishing.compatibility.quest;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.server.PluginReloadEvent;
import net.advancedplugins.bp.impl.actions.ActionRegistry;
import net.advancedplugins.bp.impl.actions.external.executor.ActionQuestExecutor;
import net.momirealms.customfishing.api.CustomFishingPlugin;
import net.momirealms.customfishing.api.event.FishingResultEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BattlePassHook implements Listener {

    public BattlePassHook() {
        Bukkit.getPluginManager().registerEvents(this, CustomFishingPlugin.get());
    }

    public void register() {
        ActionRegistry actionRegistry = BattlePlugin.getPlugin().getActionRegistry();
        actionRegistry.hook("customfishing", BPFishingQuest::new);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBattlePassReload(PluginReloadEvent event){
        register();
    }


    private static class BPFishingQuest extends ActionQuestExecutor {
        public BPFishingQuest(JavaPlugin plugin) {
            super(plugin, "customfishing");
        }

        @EventHandler
        public void onFish(FishingResultEvent event){
            if (event.isCancelled() || event.getResult() == FishingResultEvent.Result.FAILURE)
                return;
            Player player = event.getPlayer();

            // Single Fish Quest
            if (event.getLoot().getID() != null)
                this.executionBuilder("fish")
                        .player(player)
                        .root(event.getLoot().getID())
                        .progress(event.getAmount())
                        .buildAndExecute();

            // Group Fish Quest
            String[] lootGroup = event.getLoot().getLootGroup();
            if (event.getLoot() != null && lootGroup != null)
                for (String group : lootGroup) {
                    this.executionBuilder("group")
                            .player(player)
                            .root(group)
                            .progress(event.getAmount())
                            .buildAndExecute();
                }
        }
    }
}
