package com.brandon3055.draconicevolution.api.crafting;

import net.minecraft.network.chat.Component;

/**
 * Created by brandon3055 on 10/7/21
 * This is used by the fusion recipe tick to manage and update the fusion grafting process.
 */
public interface IFusionStateMachine {

    /**
     * @return the current fusion crafting state.
     */
    FusionState getFusionState();

    /**
     * Sets the current fusion crafting state
     *
     * @param state The state to set.
     */
    void setFusionState(FusionState state);

    /**
     * Called when the crafting operation is complete.
     * At this point all required ingredients will have been consumed and the output will have been
     * inserted into the fusion output slot.
     */
    void completeCraft();

    /**
     * Called if the crafting process needs to be canceled.
     * This should reset the fusion crafting and counter states.
     */
    void cancelCraft();

    /**
     * This is a general purpose counter field used to track the fusion crafting process.
     * And also to control the animation for the current state.
     *
     * @return the current counter value.
     */
    int getCounter();

    /**
     * Sets the counter value.
     *
     * @param count the new counter value.
     */
    void setCounter(int count);

    /**
     * Sets the current fusion state progress and text for display in GUI
     *
     * @param progress The current progress 0 to 1 or -1 to disable progress display.
     * @param stateText Text to display in gui for current state.
     */
    void setFusionStatus(double progress, Component stateText);

    /**
     * Used to update the crafting animation.
     * Note: in order for the animation to run smoothly time must progress at a rate of (1/length) per tick and length must be constant.
     * But if you want the animation stop progressing and run continuously at a specific progress state then set a fixed progress value
     * and set length to -1.
     *
     * @param progress The animation progress.
     * @param length The The total length of the animation.
     */
    void setCraftAnimation(float progress, int length);

    enum FusionState {
        START,
        CHARGING,
        CRAFTING,
        //Optional extra states that can be used in "non standard" fusion recipes
        STATE4,
        STATE5,
        STATE6,
        STATE7,
        STATE8
    }


}
