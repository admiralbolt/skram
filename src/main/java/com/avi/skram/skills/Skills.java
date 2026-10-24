package com.avi.skram.skills;

import com.avi.skram.Skram;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class Skills {

  public static Supplier<AttachmentType<SkillDigging>> DIGGING;

  public static void REGISTER() {
    DIGGING = Skram.ATTACHMENT_TYPES.register(
      SkillDigging.NAME, () -> AttachmentType.serializable(() -> new SkillDigging())
        .sync(SkillDigging.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );

  }




}
