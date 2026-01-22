package it.ethereallabs.biomescompass.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class EditorEventData {

    public String action = "";
    public String targetName = "";
    public String targetType = "";
    public String value = "";
    public boolean booleanValue = false;

    public EditorEventData() {
    }

    public static final BuilderCodec<EditorEventData> CODEC =
            BuilderCodec.builder(EditorEventData.class, EditorEventData::new)
                    .append(
                            new KeyedCodec<>("Action", Codec.STRING),
                            (e, v) -> e.action = v,
                            e -> e.action
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("TargetName", Codec.STRING),
                            (e, v) -> e.targetName = v,
                            e -> e.targetName
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("TargetType", Codec.STRING),
                            (e, v) -> e.targetType = v,
                            e -> e.targetType
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("@Value", Codec.STRING),
                            (e, v) -> e.value = v,
                            e -> e.value
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("@BooleanValue", Codec.BOOLEAN),
                            (e, v) -> e.booleanValue = v,
                            e -> e.booleanValue
                    )
                    .add()
                    .build();
}
