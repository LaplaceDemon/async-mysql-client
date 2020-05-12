package io.github.laplacedemon.asyncmysql.util;

public class ArrayUtils {

    private ArrayUtils(){}

    public static int compareBytes(byte[] a, byte[] b) {
        if (a == b)
            return 0;

        if (a == null || b == null)
            return a == null ? -1 : 1;

        int ml = Math.min(a.length, b.length);
        for (int i = 0; i < ml; i++) {
            if(a[i]!=b[i]) {
                return a[i] - b[i];
            }
        }

        return a.length - b.length;
    }
}
