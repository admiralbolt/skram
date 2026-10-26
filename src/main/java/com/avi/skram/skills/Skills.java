package com.avi.skram.skills;

import com.avi.skram.Skram;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Skills {

  // Lord forgive me, for I have sinned.
  // Most of the pain here was just the fact that I can use a generic type like
  //   Supplier<AttachmentType<? extends Skill>>
  // As a result of using the non-typed Supplier, there WILL be unchecked casting.
  public static Map<String, Supplier> SKILLS_BY_NAME;

  public static Supplier<AttachmentType<SkillAcrobatics>> ACROBATICS;
  public static Supplier<AttachmentType<SkillAthletics>> ATHLETICS;
  public static Supplier<AttachmentType<SkillDigging>> DIGGING;

  public static void register() {
    SKILLS_BY_NAME = new HashMap<>();

    ACROBATICS = Skram.ATTACHMENT_TYPES.register(
      SkillAcrobatics.NAME, () -> AttachmentType.serializable(() -> new SkillAcrobatics())
        .sync((holder, to) -> holder == to, SkillAcrobatics.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillAcrobatics.NAME, ACROBATICS);

    ATHLETICS = Skram.ATTACHMENT_TYPES.register(
      SkillAthletics.NAME, () -> AttachmentType.serializable(() -> new SkillAthletics())
        .sync((holder, to) -> holder == to, SkillAthletics.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillAthletics.NAME, ATHLETICS);

    DIGGING = Skram.ATTACHMENT_TYPES.register(
      SkillDigging.NAME, () -> AttachmentType.serializable(() -> new SkillDigging())
        .sync((holder, to) -> holder == to, SkillDigging.STREAM_CODEC)
        .copyOnDeath()
        .build()
    );
    SKILLS_BY_NAME.put(SkillDigging.NAME, DIGGING);
  }

}
