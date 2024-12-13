package io.github.steingen.bitstore;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Lewis
 * <p>
 * Date: 11/2/2024
 * Time: 8:05 PM
 * <p>
 */
class BitStoreTest {
    private enum TestEnum {
        SUBMITTED,
        PROCESSING,
        DISPATCHED,
        DELIVERED,
        COMPLETED,
        CANCELLED,
        EXPIRED,
        OPS_CANCELLED,
        PARTIALLY_DELIVERED
    }
    private final BitStore<TestEnum> bitStore = BitStore.empty();


    @Test
    void testEmptyBitStore() {
        // Initially, none of the values should be set
        assertFalse(bitStore.get(TestEnum.SUBMITTED));
        assertFalse(bitStore.get(TestEnum.COMPLETED));
        assertFalse(bitStore.get(TestEnum.DELIVERED));
        assertFalse(bitStore.get(TestEnum.EXPIRED));
    }

    @Test
    void testSetAndget() {
        // Set values then check presence
        bitStore.set(TestEnum.COMPLETED);
        bitStore.set(TestEnum.DISPATCHED);

        assertTrue(bitStore.get(TestEnum.COMPLETED));
        assertTrue(bitStore.get(TestEnum.DISPATCHED));

        assertFalse(bitStore.get(TestEnum.CANCELLED));
        assertFalse(bitStore.get(TestEnum.EXPIRED));
    }

    @Test
    void testSetAndgetAll() {
        // Set values then check presence
        bitStore.set(TestEnum.COMPLETED);
        bitStore.set(TestEnum.DISPATCHED);

        assertTrue(bitStore.getAll(BitStore.ofAll(TestEnum.COMPLETED, TestEnum.DISPATCHED)));

        assertFalse(bitStore.get(TestEnum.CANCELLED));
        assertFalse(bitStore.get(TestEnum.EXPIRED));
        assertFalse(bitStore.getAll(BitStore.ofAll(TestEnum.COMPLETED, TestEnum.EXPIRED)));
        assertFalse(bitStore.getAll(BitStore.ofAll(TestEnum.CANCELLED, TestEnum.EXPIRED)));
        assertFalse(bitStore.getAll(BitStore.ofAll(TestEnum.CANCELLED, TestEnum.DISPATCHED)));
    }

    @Test
    void testOfAllEnumArrayConstructor() {
        BitStore<TestEnum> bitStoreWithValues = BitStore.ofAll(TestEnum.PARTIALLY_DELIVERED,
            TestEnum.OPS_CANCELLED);

        assertTrue(bitStoreWithValues.get(TestEnum.PARTIALLY_DELIVERED));
        assertTrue(bitStoreWithValues.get(TestEnum.OPS_CANCELLED));

        assertFalse(bitStoreWithValues.get(TestEnum.CANCELLED));
        assertFalse(bitStoreWithValues.get(TestEnum.COMPLETED));
    }

    @Test
    void testOfAllEnumIterableConstructor() {
        BitStore<TestEnum> bitStoreWithValues = BitStore.ofAll(List.of(TestEnum.PARTIALLY_DELIVERED,
            TestEnum.OPS_CANCELLED));

        assertTrue(bitStoreWithValues.get(TestEnum.PARTIALLY_DELIVERED));
        assertTrue(bitStoreWithValues.get(TestEnum.OPS_CANCELLED));

        assertFalse(bitStoreWithValues.get(TestEnum.CANCELLED));
        assertFalse(bitStoreWithValues.get(TestEnum.COMPLETED));
    }

    @Test
    void testSetSameValueMultipleTimes() {
        // Set VALUE_ONE multiple times and ensure it remains set
        bitStore.set(TestEnum.PROCESSING);
        bitStore.set(TestEnum.PROCESSING);  // Setting again shouldn't affect the outcome

        assertTrue(bitStore.get(TestEnum.PROCESSING));
    }

    @Test
    void testSetAllArrayAndget() {
        // Set values then check presence
        bitStore.setAll(TestEnum.COMPLETED, TestEnum.DISPATCHED);

        assertTrue(bitStore.get(TestEnum.COMPLETED));
        assertTrue(bitStore.get(TestEnum.DISPATCHED));

        assertFalse(bitStore.get(TestEnum.CANCELLED));
        assertFalse(bitStore.get(TestEnum.EXPIRED));
    }

    @Test
    void testSetAllIterableAndget() {
        // Set values then check presence
        bitStore.setAll(List.of(TestEnum.COMPLETED, TestEnum.DISPATCHED));

        assertTrue(bitStore.get(TestEnum.COMPLETED));
        assertTrue(bitStore.get(TestEnum.DISPATCHED));

        assertFalse(bitStore.get(TestEnum.CANCELLED));
        assertFalse(bitStore.get(TestEnum.EXPIRED));
    }

    @Test
    void testgetAllExceptWithMatchingBits() {
        BitStore<TestEnum> bitStore1 = BitStore.ofAll(TestEnum.CANCELLED, TestEnum.EXPIRED, TestEnum.OPS_CANCELLED);
        BitStore<TestEnum> bitStore2 = BitStore.ofAll(TestEnum.CANCELLED, TestEnum.EXPIRED);

        // Check if all bits in bitStore2 (except CANCELLED) are in bitStore1
        assertTrue(bitStore1.getAllExcept(bitStore2, TestEnum.CANCELLED),
            "Expected bitStore1 to contain all bits of bitStore2 except CANCELLED");
    }

    @Test
    void testgetAllExceptWithNonMatchingBits() {
        BitStore<TestEnum> bitStore1 = BitStore.ofAll(TestEnum.CANCELLED, TestEnum.EXPIRED);
        BitStore<TestEnum> bitStore2 = BitStore.ofAll(TestEnum.CANCELLED, TestEnum.OPS_CANCELLED);

        // Check if all bits in bitStore2 (except CANCELLED) are in bitStore1
        assertFalse(bitStore1.getAllExcept(bitStore2, TestEnum.CANCELLED),
            "Expected bitStore1 NOT to contain all bits of bitStore2 except CANCELLED, as OPS_CANCELLED is missing");
    }

    @Test
    void testgetAllExceptWithEmptyOtherBitStore() {
        BitStore<TestEnum> bitStore1 = BitStore.ofAll(TestEnum.CANCELLED, TestEnum.EXPIRED, TestEnum.OPS_CANCELLED);
        BitStore<TestEnum> bitStore2 = BitStore.empty();

        // Check if all bits in an empty bitStore2 (ignoring any value) are in bitStore1
        assertTrue(bitStore1.getAllExcept(bitStore2, TestEnum.CANCELLED),
            "Expected bitStore1 to contain all bits of an empty bitStore2 regardless of the ignored bit");
    }

    @Test
    void testgetAllExceptWithOnlyExcludedBitSet() {
        BitStore<TestEnum> bitStore1 = BitStore.ofAll(TestEnum.CANCELLED);
        BitStore<TestEnum> bitStore2 = BitStore.ofAll(TestEnum.CANCELLED);

        // Check if all bits in bitStore2 (except CANCELLED) are in bitStore1
        // Since CANCELLED is the only bit in bitStore2, excluding it should match an empty set
        assertTrue(bitStore1.getAllExcept(bitStore2, TestEnum.CANCELLED),
            "Expected bitStore1 to match an empty bitStore2 after excluding CANCELLED");
    }

    @Test
    void testgetAllExceptWithNoOverlap() {
        BitStore<TestEnum> bitStore1 = BitStore.ofAll(TestEnum.PARTIALLY_DELIVERED);
        BitStore<TestEnum> bitStore2 = BitStore.ofAll(TestEnum.CANCELLED, TestEnum.EXPIRED);

        // Check if all bits in bitStore2 (except EXPIRED) are in bitStore1
        assertFalse(bitStore1.getAllExcept(bitStore2, TestEnum.EXPIRED),
            "Expected bitStore1 NOT to contain all bits of bitStore2 except EXPIRED, as CANCELLED is missing");
    }
}
