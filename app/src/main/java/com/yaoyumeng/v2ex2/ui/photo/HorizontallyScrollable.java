package com.yaoyumeng.v2ex2.ui.photo;

/**
 * Created by yw on 2015/6/28.
 * Interface for components that are internally scrollable left-to-right.
 */
public interface HorizontallyScrollable {
    /**
     * Return {@code true} if the component needs to receive right-to-left
     * touch movements.
     *
     * @param origX the raw x coordinate of the initial touch
     * @param origY the raw y coordinate of the initial touch
     */

    public boolean interceptMoveLeft(float origX, float origY);

    /**
     * Return {@code true} if the component needs to receive left-to-right
     * touch movements.
     *
     * @param origX the raw x coordinate of the initial touch
     * @param origY the raw y coordinate of the initial touch
     */
    public boolean interceptMoveRight(float origX, float origY);
}
