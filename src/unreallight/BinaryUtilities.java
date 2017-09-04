package unreallight;
public class BinaryUtilities {
    
    public static final int BITMASK_1 = 1;
    public static final int BITMASK_2 = 1 << 1;
    public static final int BITMASK_3 = 1 << 2;
    public static final int BITMASK_4 = 1 << 3;
    public static final int BITMASK_5 = 1 << 4;
    public static final int BITMASK_6 = 1 << 5;
    public static final int BITMASK_7 = 1 << 6;
    public static final int BITMASK_8 = 1 << 7;
    public static final int BITMASK_9 = 1 << 8;
    public static final int BITMASK_10 = 1 << 9;
    public static final int BITMASK_11 = 1 << 10;
    public static final int BITMASK_12 = 1 << 11;
    public static final int BITMASK_13 = 1 << 12;
    public static final int BITMASK_14 = 1 << 13;
    public static final int BITMASK_15 = 1 << 14;
    public static final int BITMASK_16 = 1 << 15;
    public static final int BITMASK_17 = 1 << 16;
    public static final int BITMASK_18 = 1 << 17;
    public static final int BITMASK_19 = 1 << 18;
    public static final int BITMASK_20 = 1 << 19;
    public static final int BITMASK_21 = 1 << 20;
    public static final int BITMASK_22 = 1 << 21;
    public static final int BITMASK_23 = 1 << 22;
    public static final int BITMASK_24 = 1 << 23;
    public static final int BITMASK_25 = 1 << 24;
    public static final int BITMASK_26 = 1 << 25;
    public static final int BITMASK_27 = 1 << 26;
    public static final int BITMASK_28 = 1 << 27;
    public static final int BITMASK_29 = 1 << 28;
    public static final int BITMASK_30 = 1 << 29;
    public static final int BITMASK_31 = 1 << 30;
    public static final int BITMASK_32 = 1 << 31;
    // The zero is included at the start so that the array index matches the bitmask index
    public static final int[] BITMASKS = new int[] { 0, BITMASK_1, BITMASK_2,
        BITMASK_3, BITMASK_4, BITMASK_5, BITMASK_6, BITMASK_7, BITMASK_8,
        BITMASK_9, BITMASK_10, BITMASK_11, BITMASK_12, BITMASK_13, BITMASK_14,
        BITMASK_15, BITMASK_16, BITMASK_17, BITMASK_18, BITMASK_19, BITMASK_20,
        BITMASK_21, BITMASK_22, BITMASK_23, BITMASK_24, BITMASK_25, BITMASK_26,
        BITMASK_27, BITMASK_28, BITMASK_29, BITMASK_30, BITMASK_31, BITMASK_32
    };
    
    public static byte countBits(byte b) {
        return (byte) Integer.bitCount((b & 0b00000000000000000000000011111111));
    }
    public static byte countMatchingBits(byte a, byte b) {
        return (byte) (a & b);
    }
    
    /**
     * Returns the maximum possible integer if the binary sting is <i>length</i> digits long
     * @param length The number of digits in the binary string
     * @return the maximum possible integer
     */
    public static int getMaxIntFromBinaryStringOfLength(int length) {
        int maxVal = 0;
        for (int i = 0;i<=length;i++) {
            maxVal += BinaryUtilities.BITMASKS[i];
        }
        return maxVal;
    }

    public static int binaryStrToInt(String sb) {
        return Integer.parseInt(sb.toString(), 2);
    }
    public static String intToBinaryString(int val) {
        StringBuilder sb = new StringBuilder();
        for (int i=BITMASKS.length-1; i>0; i--) {
            if (isFlagSet(val,BITMASKS[i])) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }
        return sb.toString();
    }
    public static String byteToBinaryString(byte val) {
        String s = intToBinaryString(val);
        return s.substring(s.length()-8, s.length());
    }
    /**
     * Clears (sets 0) the given bit from the supplied val
     * @param val The val to edit
     * @param bitmask The bit to clear
     * @return The altered val
     */
    public static int clearFlag(int val, int bitmask) {
        if (BinaryUtilities.isFlagSet(val,bitmask)) {
            val = BinaryUtilities.toggleBit(val, bitmask);
        }
        return val;
    }
    /**
     * Checks if the bit is set
     * @param val The val to alter
     * @param bitmask The bit to check
     * @return true if the bit is 1, false if low
     */
    public static boolean isFlagSet(int val, int bitmask) {
        return ((val & bitmask) == bitmask);
    }
    public static boolean isFlagSet(byte val, int bitmask) {
        return ((val & bitmask) == bitmask);
    }
    public static boolean isFlagSet(long val, long bitmask) {
        return ((val & bitmask) == bitmask);
    }
    /**
     * Toogles the given bit
     * @param val The val to alter
     * @param bitmask The bit to toggle
     * @return The val after the bit has been toggled
     */
    public static int toggleBit(int val, int bitmask) {
        return val & ~bitmask;
    }
    /**
     * Sets the bit flag
     * @param val The int to alter
     * @param bitmask The bit to set
     * @return The val after the bit has been set
     */
    public static int setFlag(int val, int bitmask) {
        return val | bitmask;
    }
    public static int setFlag(byte val, int bitmask) {
        return (val | bitmask);
    }
    public static long setFlag(long val, long bitmask) {
        return val | bitmask;
    }
    /**
     * Takes a single int and breaks it down into 3 rgb values by bit shifting.
     * Int should effectively be a 24-bit value in format rrrrrrrrggggggggbbbbbbbb (red, green, blue)
     * @param color a single int rgb value (probably obtained from BinaryUtilities.rgbToSingleInt()
     * @return an array containing rgb values
     */
    public static int[] singleIntToRGB(int color) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0;i<=24;i++) {
            if (BinaryUtilities.isFlagSet(color, BinaryUtilities.BITMASKS[i])) {
                if (i<9) {
                    b = BinaryUtilities.setFlag(b, BinaryUtilities.BITMASKS[i]);
                }
                else if (i<17) {
                    g = BinaryUtilities.setFlag(g, BinaryUtilities.BITMASKS[i-8]);
                }
                else {
                    r = BinaryUtilities.setFlag(r, BinaryUtilities.BITMASKS[i-16]);
                }
            }
        }
        return new int[] { r, g, b };
    }
    /**
     * Converts the given rgb values to a single int by bit-shifting.
     * The resulting int is effectively 24-bit in format rrrrrrrrggggggggbbbbbbbb (red, green, blue)
     * @param r The red value
     * @param g The green value
     * @param b The blue value
     * @return a single int containing all the rgb values
     */
    public static int rgbToSingleInt(int r, int g, int b) {
        r = r << 16;
        g = g << 8;
        return r+g+b;
    }
    /**
     * Enables a byte to be cast to an int while keeping the bits the same.
     * eg, 10101010 = 170 int or -85 byte.
     * Casting a byte value of -85 to int traditionally will result in a int with bit pattern
     * 11111111111111111111111110101010
     * when really what we want is
     * 00000000000000000000000010101010
     * which is int value 170. This is what'll be returned by this function
     * @param b
     * @return
     */
    public static int bitCorrectCasting(byte b) {
        int a;
        if (b < 0) { a = b+256; }
        else { a = b; }
        return a;
    }
    
    /**
     * Amends a byte with the given string -
     * @param b The byte to amend
     * @param s String must be 8 characters, use 1 for on, 0 for off and ? for unknown
     * @return 
     */
    public static int byteFromString(byte b, String s) {
        if (s.length() != 8) { return 0; }
        
        for (int i=0; i<s.length(); i++) {
            if (s.charAt(i) == '1') {
                b = (byte) BinaryUtilities.setFlag(b, 1 << i);
            } else if (s.charAt(i) == '0') {
                b = (byte) BinaryUtilities.clearFlag(b, 1 << i);
            }
        }
        return b;
    }
}


