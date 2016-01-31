package toast.utilityMobs.event;

import toast.utilityMobs.TickHandler;

public abstract class UtilityMobsEvent
{
    {
        TickHandler.register(this);
    }

    /// Actually triggers this event's effects.
    public abstract void execute();
}