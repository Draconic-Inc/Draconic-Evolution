package com.brandon3055.draconicevolution.client.shader;

import codechicken.lib.render.shader.CCShaderInstance;
import codechicken.lib.render.shader.CCUniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.brandon3055.draconicevolution.DraconicEvolution.MODID;

/**
 * Created by covers1624 on 1/10/22.
 */
public class DEShader<T extends DEShader<T>> {

    private final ResourceLocation location;
    private final VertexFormat format;
    private final List<Consumer<T>> applyCallbacks = new LinkedList<>();

    protected CCShaderInstance shaderInstance;

    protected CCUniform modelMatUniform;
    protected CCUniform timeUniform;

    public DEShader(String path, VertexFormat format) {
        this(new ResourceLocation(MODID, path), format);
    }

    public DEShader(ResourceLocation location, VertexFormat format) {
        this.location = location;
        this.format = format;
    }

    public final void register(IEventBus bus) {
        bus.addListener(this::onRegisterShaders);
    }

    public final T onShaderApplied(Consumer<T> cons) {
        applyCallbacks.add(cons);
        //noinspection unchecked
        return (T) this;
    }

    // @formatter:off
    public final boolean isLoaded() { return shaderInstance != null; }
    public final CCShaderInstance getShaderInstance() { return Objects.requireNonNull(shaderInstance, "Shader not loaded yet."); }
    public final CCUniform getModelMatUniform() { return Objects.requireNonNull(modelMatUniform, missingUniformMessage("ModelMat"));}
    public final boolean hasModelMatUniform() { return modelMatUniform != null; }
    public final CCUniform getTimeUniform() { return Objects.requireNonNull(timeUniform, missingUniformMessage("Time")); }
    public final boolean hasTimeUniform() { return timeUniform != null; }
    // @formatter:on

    private void onRegisterShaders(RegisterShadersEvent event) {
        event.registerShader(CCShaderInstance.create(event.getResourceManager(), location, format), e -> {
            shaderInstance = (CCShaderInstance) e;
            onShaderLoaded();
            shaderInstance.onApply(() -> {
                for (Consumer<T> applyCallback : applyCallbacks) {
                    //noinspection unchecked
                    applyCallback.accept((T) DEShader.this);
                }
            });
        });
    }

    protected void onShaderLoaded() {
        modelMatUniform = shaderInstance.getUniform("ModelMat");
        timeUniform = shaderInstance.getUniform("Time");
    }

    protected final String missingUniformMessage(String name) {
        if (shaderInstance == null) return "Shader not yet loaded.";

        return "Shader does not have '" + name + "' uniform.";
    }
}
