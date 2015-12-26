package kana.AutoRegionWorldguard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kana.AutoRegionWorldguard.AutoRegionWorldguard;
import kana.AutoRegionWorldguard.Vault;

public class ARWcommand implements CommandExecutor{
	
	private WorldGuardPlugin worldGuard;
	private WorldEditPlugin worldEdit;
	private Selection selection;
	private BlockVector Pmin;
	private BlockVector Pmax;
	private BlockVector p1;
	private BlockVector p2;
	private ProtectedRegion parent;
	private RegionManager regionManager;
	private String parentConf;
	private World world;
	private ProtectedCuboidRegion pr;
	private int priority;
	private String nomTerrain;
	
	AutoRegionWorldguard plugin;
	public ARWcommand(AutoRegionWorldguard plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		// Récupération du joueur qui envoie la commande
		//----------------------------------------------
		Player player = null;
    	if(sender instanceof Player){
    		player = (Player) sender;
    	}
    	if(commandLabel.equalsIgnoreCase("arw")){
	        if(args.length == 0){
	        	sender.sendMessage(ChatColor.GOLD + "[ARW] " + ChatColor.WHITE + "Tapez /arw help");
	        	return true;
	        }
	        else if(args.length == 1){
	        	//--------------
	        	//---- HELP ----
	        	//--------------
	        	if(args[0].equalsIgnoreCase("help")){
	        		sender.sendMessage(ChatColor.WHITE + "----------- HELP AutoRegionWorldguard -----------");
					sender.sendMessage(ChatColor.WHITE + "/arw help" + ChatColor.GREEN + " Obtenir l'aide");
					sender.sendMessage(ChatColor.WHITE + "/arw create" + ChatColor.GREEN + " Créer un terrain");
					sender.sendMessage(ChatColor.WHITE + "/arw reload" + ChatColor.GREEN + " Recharger la configuration");
		        	return true;
	        	}
	        	//----------------
	        	//---- RELOAD ----
	        	//----------------
	        	else if(args[0].equalsIgnoreCase("reload")){
	        		if(!Vault.permission.has(player, "autoregionworldguard.reload")){
	        			sender.sendMessage(ChatColor.RED + "[ARW] " + ChatColor.WHITE + "Vous n'avez pas la permission d'utiliser cette commande !");
	        			return false;
	        		}
	        		this.plugin.reloadConfig();
		        	return true;
	        	}
	        	//----------------
	        	//---- CREATE ----
	        	//----------------
	        	else if(args[0].equalsIgnoreCase("create")){
	        		
	        		// On vérifi la permission
	        		// -----------------------
	        		if(!Vault.permission.has(player, "autoregionworldguard.create")){
	        			sender.sendMessage(ChatColor.RED + "[ARW] " + ChatColor.WHITE + "Vous n'avez pas la permission d'utiliser cette commande !");
	        			return true;
	        		}
	        		
	        		this.worldGuard = this.plugin.getWorldGuard();
	        		this.worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		    		this.selection = worldEdit.getSelection(player);
		    		
		    		this.Pmax = recupBlocVectorMax(selection);
					this.Pmin = recupBlocVectorMin(selection);
					
					// On vérification si le parent existe
					//------------------------------------
					this.world = player.getWorld();
					this.parentConf = this.plugin.getConfig().getString("parent");
					this.regionManager = worldGuard.getRegionManager(world);
    				this.parent = regionManager.getRegion(parentConf);
	    			if(parent == null){
    					sender.sendMessage(ChatColor.RED + "[ARW] " + ChatColor.WHITE + "Region parent introuvable, contactez un admin !");
    					return true;
	    			}
	    			// On récupère la priorité et on défini le nom du terrain
	    			//-------------------------------------------------------
	    			this.priority = this.plugin.getConfig().getInt("priority");
	    			this.nomTerrain = "t_" + System.currentTimeMillis() + "0";
	    			this.pr = new ProtectedCuboidRegion(nomTerrain, Pmin, Pmax);
	    			this.pr.setPriority(priority);
	    			
	    			try{
	    				this.pr.setParent(parent);
		    			regionManager.addRegion(pr);
		    			regionManager.save();
		    			sender.sendMessage(ChatColor.GREEN + "[ARW] " + ChatColor.WHITE + "La région " + ChatColor.GREEN + nomTerrain + ChatColor.YELLOW + " créé avec succés");
		    			return true;
					} 
			    	catch (Exception exp){
			    		sender.sendMessage(ChatColor.RED + "[ARW] " + ChatColor.WHITE + "Un problème est survenu, contactez un admin !");
			    		sender.sendMessage(ChatColor.RED + "[ARW] " + ChatColor.WHITE + exp);
			    		return false;
					}
	        	}
	        	else{
	        		sender.sendMessage(ChatColor.RED + "[ARW] " + ChatColor.WHITE + "Tapez /arw help !");
		    		return false;
	        	}
	        }
	        else{
	        	sender.sendMessage(ChatColor.RED + "[ARW] " + ChatColor.WHITE + "Tapez /arw help !");
	    		return false;
	        }
    	}
		return false;
	}
	
	public BlockVector recupBlocVectorMax(Selection selection){
		int up = this.plugin.getConfig().getInt("up");
		int posX = selection.getMaximumPoint().getBlockX();
		int posY = up;
		int posZ = selection.getMaximumPoint().getBlockZ();
	    p1 = new BlockVector(posX, posY, posZ);	    
	    return p1;
	}
	public BlockVector recupBlocVectorMin(Selection selection){
		int down = this.plugin.getConfig().getInt("down");
		int posX = selection.getMinimumPoint().getBlockX();
		int posY = down;
		int posZ = selection.getMinimumPoint().getBlockZ();
	    p2 = new BlockVector(posX, posY, posZ);	    
	    return p2;
	}
}
