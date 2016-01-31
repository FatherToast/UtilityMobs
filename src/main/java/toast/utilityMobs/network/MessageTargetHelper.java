package toast.utilityMobs.network;

import toast.utilityMobs.TargetHelper;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTargetHelper implements IMessage {

    private String owner;

    public MessageTargetHelper() {
    }

    public MessageTargetHelper(TargetHelper targetHelper) {
        this.owner = targetHelper.owner;
    }

    /*
     * @see cpw.mods.fml.common.network.simpleimpl.IMessage#fromBytes(io.netty.buffer.ByteBuf)
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        this.owner = ByteBufUtils.readUTF8String(buf);
        TargetHelper.getTargetHelper(this.owner).load(buf);
    }

    /*
     * @see cpw.mods.fml.common.network.simpleimpl.IMessage#toBytes(io.netty.buffer.ByteBuf)
     */
    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.owner);
        TargetHelper.getTargetHelper(this.owner).save(buf);
    }

    public static class Handler implements IMessageHandler<MessageTargetHelper, IMessage> {

        /*
         * @see cpw.mods.fml.common.network.simpleimpl.IMessageHandler#onMessage(cpw.mods.fml.common.network.simpleimpl.IMessage, cpw.mods.fml.common.network.simpleimpl.MessageContext)
         */
        @Override
        public IMessage onMessage(MessageTargetHelper message, MessageContext ctx) {
            // Already loaded in fromBytes()
            return null;
        }

    }
}
