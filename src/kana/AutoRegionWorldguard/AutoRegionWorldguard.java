package kana.AutoRegionWorldguard;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import kana.AutoRegionWorldguard.ARWcommand;
import kana.AutoRegionWorldguard.Vault;

public class AutoRegionWorldguard extends JavaPlugin implements Listener {
	
	private Logger logger = Logger.getLogger("Minecraft");
	public Plugin plugin;
	public ARWcommand commandL;
	public PluginCommand batchcommand;
	
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
		this.commandL = new ARWcommand(this);
        this.batchcommand = getCommand("arw");
        batchcommand.setExecutor(commandL);
        
    	Vault.load(this);
    	Vault.setupChat();
    	Vault.setupPermissions();
    	Vault.setupEconomy();
    	if (!Vault.setupEconomy()){
            logger.info(String.format("[%s] - Necessite Vault pour fonctionner!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }    	
    	
    	this.loadConfig();
		        
		logger.info("[AutoRegionWorldguard] Plugin charge parfaitement!");
		
		// Metric
    	//-------
    	try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
        	logger.info("[AutoRegionWorldguard - Metric] Un probleme est survenue avec Metric !");
        }
    }
	
    public void onDisable(){
            logger.info("[AutoRegionWorldguard] Plugin desactive...");
    }
    
    public void loadConfig(){           
    	this.getConfig().options().copyDefaults(true);	
    	this.saveConfig();
    }
    
    WorldGuardPlugin getWorldGuard() {
        this.plugin = getServer().getPluginManager().getPlugin("WorldGuard");   
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) { 
            getServer().getPluginManager().disablePlugin(this);
            return null;
        }     
        return (WorldGuardPlugin) plugin;
    }
}
