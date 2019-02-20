package com.nukkitx.server.common.messaging;

import com.nukkitx.server.common.SharedConstants;
import com.nukkitx.server.common.messaging.impl.MessageSetOp;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.binary.netty.NettyUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unchecked")
public class MessageRegistry {
    public static final MessageRegistry INSTANCE = new MessageRegistry();

    protected final AtomicInteger idCounter = new AtomicInteger(0);
    protected final Supplier<? extends Message>[] ids = (Supplier<Message>[]) new Supplier[256];
    protected final Map<Class<? extends Message>, Integer> classes = new IdentityHashMap<>();

    {
        this.register(MessageSetOp.class, MessageSetOp::new);
    }

    protected <T extends Message> MessageRegistry register(@NonNull Class<T> clazz, @NonNull Supplier<T> supplier) {
        int id = this.idCounter.getAndIncrement();
        this.ids[id] = supplier;
        this.classes.put(clazz, id);
        return this;
    }

    public void send(@NonNull Message message, @NonNull BiConsumer<String, byte[]>... sendFunctions) {
        if (!this.classes.containsKey(message.getClass())) {
            throw new IllegalArgumentException(String.format("Unregistered message: %s", message.getClass().getCanonicalName()));
        }
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(512);
        try (DataOut out = NettyUtil.wrapOut(buf)) {
            out.writeVarInt(this.classes.get(message.getClass()), true);
            message.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] encoded = buf.array().clone();
        buf.release();
        for (BiConsumer<String, byte[]> sendFunction : sendFunctions)   {
            sendFunction.accept(SharedConstants.CHANNEL_NAME, encoded);
        }
    }

    public void handle(@NonNull byte[] raw) {
        Message message;
        try (DataIn in = DataIn.wrap(new ByteArrayInputStream(raw))) {
            message = this.ids[in.readVarInt(true)].get();
            message.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        message.handle(this);
    }
}
