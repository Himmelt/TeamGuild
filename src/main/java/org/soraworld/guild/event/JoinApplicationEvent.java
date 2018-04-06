package org.soraworld.guild.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JoinApplicationEvent extends Event implements Cancellable {

    public final String guild;
    public final String applicant;
    private boolean canceled = false;
    private static final HandlerList handlers = new HandlerList();

    public JoinApplicationEvent(String guild, String applicant) {
        this.guild = guild;
        this.applicant = applicant;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean canceled) {
        this.canceled = canceled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
