package anon.seamlessauth.util;

import java.util.Objects;

public class Pair<T, K> {

    public T first;
    public K second;

    public Pair(T t, K k) {
        first = t;
        second = k;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pair<?, ?>) {
            Pair<?, ?> oPair = (Pair<?, ?>) other;
            return Objects.equals(first, oPair.first) && Objects.equals(second, oPair.second);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
