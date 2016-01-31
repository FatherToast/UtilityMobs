package toast.utilityMobs.network;

import io.netty.buffer.ByteBuf;
import toast.utilityMobs.TargetHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageFetchTargetHelper implements IMessage {

    public MessageFetchTargetHelper() {
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

    public static class Handler implements IMessageHandler<MessageFetchTargetHelper, IMessage> {

        /*
         * @see cpw.mods.fml.common.network.simpleimpl.IMessageHandler#onMessage(cpw.mods.fml.common.network.simpleimpl.IMessage, cpw.mods.fml.common.network.simpleimpl.MessageContext)
         */
        @Override
        public IMessage onMessage(MessageFetchTargetHelper message, MessageContext ctx) {
            return new MessageTargetHelper(TargetHelper.getTargetHelper(FMLClientHandler.instance().getClientPlayerEntity().getCommandSenderName()));
        }

    }
}
