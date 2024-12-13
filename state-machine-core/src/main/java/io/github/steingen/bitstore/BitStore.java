package io.github.steingen.bitstore;


/**
 * @author Lewis
 * <p>
 * Date: 10/30/2024
 * Time: 11:06 AM
 * <p>
 *     This class uses a bitmask to represent the presence or absence of enum values.
 */
public class BitStore<E extends Enum<E>> {
    private long bitMask;

    public BitStore() {
        this.bitMask = 0;
    }


    public static <E extends Enum<E>> BitStore<E> empty() {
        return new BitStore<>();
    }

    /**
     * Creates a BitStore with the bits corresponding to the ordinal values of the provided enums set
     * */
    @SafeVarargs
    public static <E extends Enum<E>> BitStore<E> ofAll(E... enumValues) {
        BitStore<E> bitStore = empty();

        bitStore.setAll(enumValues);

        return bitStore;
    }

    /**
     * Creates a BitStore with the bits corresponding to the ordinal values of the provided enums set
     * */
    public static <E extends Enum<E>> BitStore<E> ofAll(Iterable<E> enumValues) {
        BitStore<E> bitStore = empty();

        bitStore.setAll(enumValues);

        return bitStore;
    }

    /**
     * Checks if the bit corresponding to the provided enum value is set. It uses the enum ordinal as the bit number
     * */
    public boolean get(E enumValue) {
        long bit = 1L << enumValue.ordinal();
        return (bitMask & bit) != 0;
    }

    /**
     * Checks if all bits set in the provided BitStore are also set in this BitStore.
     * This effectively checks if `other` is a subset of this BitStore.
     *
     * @param other the BitStore to check against
     * @return true if all bits in `other` are set in this BitStore, false otherwise
     */
    public boolean getAll(BitStore<E> other) {
        // Check if all bits in `other.bitMask` are also present in this `bitMask`
        return (this.bitMask & other.bitMask) == other.bitMask;
    }

    /**
     * Checks if all the bits in the provided BitStore are set in this BitStore,
     * except for the bit corresponding to the provided enum value.
     *
     * @param other the BitStore to check against
     * @param enumValue the enum value whose bit should be ignored
     * @return true if all bits in `other` (except for `enumValue`) are set in this BitStore, false otherwise
     */
    public boolean getAllExcept(BitStore<E> other, E enumValue) {
        // Create a bitmask with the bit of enumValue cleared
        long clearedBitMask = other.bitMask & ~(1L << enumValue.ordinal());

        // Check if all the remaining bits in `clearedBitMask` are set in this `bitMask`
        return (this.bitMask & clearedBitMask) == clearedBitMask;
    }

    /**
     * Sets the bit corresponding to the provided enum value. It uses the enum ordinal as the bit number
     * */
    public void set(E enumValue) {
        bitMask |= 1L << enumValue.ordinal();
    }

    /**
     * Sets the bits corresponding to the provided enum values. It uses the enum ordinals as the bit numbers
     * */
    @SafeVarargs
    public final void setAll(E... enumValues) {
        for (E enumValue : enumValues) {
            set(enumValue);
        }
    }

    /**
     * Sets the bits corresponding to the provided enum values. It uses the enum ordinals as the bit numbers
     * */
    public final void setAll(Iterable<E> enumValues) {
        for (E enumValue : enumValues) {
            set(enumValue);
        }
    }
}
