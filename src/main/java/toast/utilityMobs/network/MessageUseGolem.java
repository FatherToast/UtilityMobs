package toast.utilityMobs.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import toast.utilityMobs.colossal.EntityColossalGolem;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageUseGolem implements IMessage {

    public MessageUseGolem() {
    }

    /*
     * @see cpw.mods.fml.common.network.simpleimpl.IMessage#fromBytes(io.netty.buffer.ByteBuf)
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        buf.readByte(); // Empty packets break things.
    }

    /*
     * @see cpw.mods.fml.common.network.simpleimpl.IMessage#toBytes(io.netty.buffer.ByteBuf)
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(0); // Empty packets break things.
    }

    public static class Handler implements IMessageHandler<MessageUseGolem, IMessage> {

        /*
         * @see cpw.mods.fml.common.network.simpleimpl.IMessageHandler#onMessage(cpw.mods.fml.common.network.simpleimpl.IMessage, cpw.mods.fml.common.network.simpleimpl.MessageContext)
         */
        @Override
        public IMessage onMessage(MessageUseGolem message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player.ridingEntity instanceof EntityColossalGolem) {
                ((EntityColossalGolem) player.ridingEntity).attackEntityAsMob(null);
            }
            return null;
        }

    }
}
