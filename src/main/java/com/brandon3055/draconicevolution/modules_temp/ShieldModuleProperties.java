package com.brandon3055.draconicevolution.modules_temp;

/**
 * Created by covers1624 on 4/16/20.
 */
public interface ShieldModuleProperties extends IModuleProperties<ShieldModuleProperties> {

    ShieldModuleProperties setShieldPoints(int points);

    class Impl implements ShieldModuleProperties {

        public int width = 2;
        public int height = 2;
        public int shieldPoints;

        @Override
        public ShieldModuleProperties setShieldPoints(int points) {
            this.shieldPoints = points;
            return this;
        }

        @Override
        public ShieldModuleProperties setDimensions(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public void merge(Impl other) {
            shieldPoints += other.shieldPoints;
        }

    }
}
