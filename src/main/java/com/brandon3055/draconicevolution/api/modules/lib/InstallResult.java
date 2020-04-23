package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.draconicevolution.api.modules.IModule;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by covers1624 on 4/16/20.
 *
 * In the event of a failed install its because moduleA rejected moduleB because reason.
 */
public class InstallResult {

    public final InstallResultType resultType;
    public final IModule<?> module;
    public final IModule<?> incompatibleModule;
    
    public final ITextComponent reason;

    public InstallResult(InstallResultType resultType, IModule<?> module, IModule<?> incompatibleModule, ITextComponent reason) {
        this.resultType = resultType;
        this.module = module;
        this.incompatibleModule = incompatibleModule;
        this.reason = reason;
    }

    /**
    * If one or both of these results is blocking the other then return the blocking result.
    * */
    public InstallResult getBlockingResult(InstallResult other) {
        switch (resultType) {
            case YES:
                if (other.resultType == InstallResultType.YES) {
                    return this;
                }
                else if (other.resultType == InstallResultType.ONLY_WHEN_OVERRIDEN) {
                    return other;
                }
            case ONLY_WHEN_OVERRIDEN:
                if (other.resultType == InstallResultType.OVERRIDE) {
                    return other;
                }
            case OVERRIDE:
                if (other.resultType == InstallResultType.ONLY_WHEN_OVERRIDEN || other.resultType == InstallResultType.YES) {
                    return this;
                }
            default:
                if (other.resultType == InstallResultType.NO) {
                    return other;
                }
            case NO:
                return this;
        }
    }

    public enum InstallResultType {
        YES,
        ONLY_WHEN_OVERRIDEN,
        OVERRIDE,
        NO;
    }
}