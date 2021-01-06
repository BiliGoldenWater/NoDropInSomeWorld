package indi.goldenwater.nodropinsomeworld.listener;

import indi.goldenwater.nodropinsomeworld.NoDropInSomeWorld;
import indi.goldenwater.nodropinsomeworld.utils.CheckPermissions;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.List;
import java.util.Objects;

public class OnPlayerDropItemEvent implements Listener {

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event){
        FileConfiguration configuration = NoDropInSomeWorld.getInstance().getConfig();
        List<String> denyWorlds = configuration.getStringList("denyWorlds");

        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();


        if (!denyWorlds.isEmpty()){
            denyWorlds.forEach(value -> {
                if (Objects.equals(value, worldName)){
                    String message = configuration.getString("messageWhenTriggerDropBan");
                    Material material = event.getItemDrop().getItemStack().getType();

                    String permItem = "nodropinsomeworld.bypass.item." + material.name();
                    String permWorld = "nodropinsomeworld.bypass.world." + worldName;
                    String finalMessage = message == null ?
                            "§c你没有权限 %premItem 或 %permWorld 在这个世界丢弃物品" : message;
                    finalMessage = finalMessage.replaceAll("%premItem", permItem)
                            .replaceAll("%permWorld", permWorld);

                    if (!CheckPermissions.hasPermissions(player,permItem) || !CheckPermissions.hasPermissions(player,permWorld)){
                        event.setCancelled(true);
                        player.sendMessage(finalMessage);
                    }
                }
            });
        }
    }

}
