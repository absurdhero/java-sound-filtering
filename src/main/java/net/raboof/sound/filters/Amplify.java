package net.raboof.sound.filters;

public class Amplify extends SingleSampleFilter {
    float factor;

    public Amplify(float factor) {
        this.factor = factor;
    }

    @Override
    protected int transform(int x) {
        return (int) (x * factor);
    }
}
