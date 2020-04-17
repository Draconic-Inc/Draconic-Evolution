package com.brandon3055.draconicevolution.modules_temp;

/**
 * Created by covers1624 on 4/16/20.
 */
public interface EnergyModuleProperties extends IModuleProperties<EnergyModuleProperties> {

    EnergyModuleProperties setStorageSize(long capacity);

    class Impl implements EnergyModuleProperties {

        public int width = 2;
        public int height = 4;
        public long capacity;

        public Impl(long capacity) {
            this.capacity = capacity;
        }

        @Override
        public EnergyModuleProperties setStorageSize(long capacity) {
            this.capacity = capacity;
            return this;
        }

        @Override
        public EnergyModuleProperties setDimensions(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public void merge(Impl other) {
            capacity += other.capacity;
        }

    }
}
