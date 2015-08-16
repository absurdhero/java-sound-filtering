package net.raboof.sound.filters;

/** run multiple filters in sequence */
public class FilterChain implements Filter {
    private Filter[] filters;

    public FilterChain(Filter... filters) {
        this.filters = filters;
    }

    @Override
    public void filter(int length, int[] data) {
        for(Filter filter : filters) {
            filter.filter(length, data);
        }
    }
}
