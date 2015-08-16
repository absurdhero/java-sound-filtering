package net.raboof.sound.filters;

/** Filter that operates on a single sample at a time */
public abstract class SingleSampleFilter implements Filter {
    @Override
    public void filter(int length, int[] data) {
        for (int i = 0; i < length; i++) {
            data[i] = transform(data[i]);
        }
    }

    protected abstract int transform(int x);
}
