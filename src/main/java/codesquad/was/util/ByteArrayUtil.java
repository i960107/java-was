package codesquad.was.util;

public final class ByteArrayUtil {
    private ByteArrayUtil() {
    }

    public static int indexOf(byte[] data, byte[] target, int start) {
        for (int i = start; i <= data.length - target.length; i++) {
            int j;
            for (j = 0; j < target.length; j++) {
                if (data[i + j] != target[j]) {
                    break;
                }
            }
            if (j == target.length) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(String data, byte[] target, int start) {
        for (int i = start; i <= data.length() - target.length; i++) {
            int j;
            for (j = 0; j < target.length; j++) {
                if (data.charAt(i + j) != target[j]) {
                    break;
                }
            }
            if (j == target.length) {
                return i;
            }
        }
        return -1;
    }

    public static byte[] copy(byte[] input, int startIndex, int size) {
        byte[] result = new byte[size];
        System.arraycopy(input, startIndex, result, 0, size);
        return result;
    }

}
