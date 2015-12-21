package kana.AutoRegionWorldguard;

import java.util.logging.Logger;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import kana.AutoRegionWorldguard.ARWcommand;
import kana.AutoRegionWorldguard.Vault;

public class AutoRegionWorldguard extends JavaPlugin implements Listener {
	
	private Logger logger = Logger.getLogger("Minecraft");
	public Plugin plugin;
	public ARWcommand commandL;
	public PluginCommand batchcommand;
	
	public void onEnable()
    {
    	this.commandL = new ARWcommand(this);
        this.batchcommand = getCommand("arw");
        batchcommand.setExecutor(commandL);
        
    	Vault.load(this);
    	Vault.setupChat();
    	Vault.setupPermissions();
    	Vault.setupEconomy();
    	if (!Vault.setupEconomy()) 
    	{
            logger.info(String.format("[%s] - AutoRegionWorldguard necessite Vault pour fonctionner!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    	
    	this.loadConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
        
		logger.info("[AutoRegionWorldguard] Plugin charge parfaitement!");
    }
    public void onDisable()
    {
            logger.info("[AutoRegionWorldguard] Plugin desactive...");
    }
    public void loadConfig(){           
    	this.getConfig().options().copyDefaults(true);
		this.saveConfig();
    }
    WorldGuardPlugin getWorldGuard() {
        this.plugin = getServer().getPluginManager().getPlugin("WorldGuard");   
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) { 
            getServer().getPluginManager().disablePlugin(this);
            return null; // Maybe you want throw an exception instead
        }     
        return (WorldGuardPlugin) plugin;
    }
}
