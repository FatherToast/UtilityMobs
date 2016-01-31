package toast.utilityMobs.client.model;

import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.entity.Entity;
import toast.utilityMobs.golem.EntityUtilityGolem;

public class ModelStackGolem extends ModelSnowMan {

    // Sets the model's various rotation angles.
    @Override
    public void setRotationAngles(float time, float moveSpeed, float rotationFloat, float rotationYaw, float rotationPitch, float partialTicks, Entity entity) {
        super.setRotationAngles(time, moveSpeed, rotationFloat, rotationYaw, rotationPitch, partialTicks, entity);
        EntityUtilityGolem golem = (EntityUtilityGolem) entity;
        if (golem.isSitting()) {
            this.rightHand.rotateAngleZ = 0.0F;
            this.leftHand.rotateAngleZ = 0.0F;
        }
    }
}
