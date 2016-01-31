package toast.utilityMobs.turret;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import toast.utilityMobs._UtilityMobs;

public class EntityObsidianTurret extends EntityTurretGolem
{
    /// The texture for this class.
    public static final ResourceLocation TEXTURE = new ResourceLocation(_UtilityMobs.TEXTURE + "turret/obsidianTurret.png");

    public EntityObsidianTurret(World world) {
        super(world);
        this.texture = EntityObsidianTurret.TEXTURE;
        this.isImmuneToFire = true;
    }

    /// Initializes this entity's attributes.
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0);
    }

    @Override
    public int getTotalArmorValue() {
        return 20;
    }
}