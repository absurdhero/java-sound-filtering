package net.raboof.sound.filters;

public interface Filter {
    /** Modifies the data array in place */
    void filter(int length, int[] data);
}
