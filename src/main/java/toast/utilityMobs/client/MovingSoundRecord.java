package toast.utilityMobs.client;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import toast.utilityMobs.block.EntityJukeboxGolem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MovingSoundRecord extends MovingSound {

    private final EntityJukeboxGolem golem;
    private String record;

    public MovingSoundRecord(EntityJukeboxGolem golem, String record, PositionedSoundRecord original) {
        super(original.getPositionedSoundLocation());
        this.volume = original.getVolume();
        this.field_147663_c = original.getPitch();
        this.repeat = original.canRepeat();
        this.field_147665_h = original.getRepeatDelay();
        this.field_147666_i = original.getAttenuationType();

        this.golem = golem;
        this.xPosF = (float)golem.posX;
        this.yPosF = (float)golem.posY;
        this.zPosF = (float)golem.posZ;

        this.record = record;
    }

    @Override
    public void update() {
        if (this.golem.isDead || !this.golem.getRecord().equals(this.record)) {
            this.donePlaying = true;
        }
        else {
            this.xPosF = (float)this.golem.posX;
            this.yPosF = (float)this.golem.posY;
            this.zPosF = (float)this.golem.posZ;
        }
    }
}
