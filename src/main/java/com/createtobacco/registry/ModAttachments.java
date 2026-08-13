package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import com.createtobacco.attachment.SmokingData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CreateTobacco.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SmokingData>> SMOKING_DATA =
            ATTACHMENTS.register("smoking_data", () -> AttachmentType.builder(SmokingData::new)
                    .serialize(SmokingData.CODEC)
                    .copyOnDeath()
                    .build());

    private ModAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}
