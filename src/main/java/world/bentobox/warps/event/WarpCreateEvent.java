package world.bentobox.warps.event;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import world.bentobox.warps.Warp;

/**
 * This event is fired when a Warp is created
 * A Listener to this event can use it only to get informations. e.g: broadcast something
 * 
 * @author Poslovitch
 *
 */
public class WarpCreateEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	private Location warpLoc;
	private UUID creator;
	
	/**
	 * @param plugin - BSkyBlock plugin objects
	 * @param warpLoc
	 * @param creator
	 */
	public WarpCreateEvent(Warp plugin, Location warpLoc, UUID creator){
		this.warpLoc = warpLoc;
		this.creator = creator;
	}
	
	/**
	 * Get the location of the created Warp
	 * @return created warp's location
	 */
	public Location getWarpLocation(){return this.warpLoc;}
	
	/**
	 * Get who has created the warp
	 * @return the warp's creator
	 */
	public UUID getCreator(){return this.creator;}
	
	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
