package anon.seamlessauth.util;

import java.util.Arrays;

public class Bytes {

    public byte[] data;

    public Bytes(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Bytes) {
            Bytes oBytes = (Bytes) other;
            return Arrays.equals(data, oBytes.data);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
