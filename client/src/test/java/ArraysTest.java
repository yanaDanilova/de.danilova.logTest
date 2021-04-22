import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public  class ArraysTest {
    private static ArraysApp arraysApp;


    @BeforeEach
    public void init() {
        arraysApp = new ArraysApp();
    }

    @ParameterizedTest
    @MethodSource("dataForArrOperChange")
    public void testChangeArr(int[] arr, int[] arr2) {
        Assertions.assertArrayEquals(arr, arraysApp.changeArr(arr2));
    }

    static Stream<Arguments> dataForArrOperChange() {
        return Stream.of(
                Arguments.arguments(new int[]{3, 2, 1}, new int[]{1, 2, 3, 4, 5, 4, 3, 2, 1}),
                Arguments.arguments(new int[]{5, 6, 7}, new int[]{1, 2, 3, 4, 5, 6, 7}),
                Arguments.arguments(new int[]{5}, new int[]{1, 2, 3, 4, 5})
        );

    }
    @Test
    public void testChangeArr() {
        Assertions.assertThrows(RuntimeException.class, () -> arraysApp.changeArr(new int[]{1, 2, 3, 5}));
    }

    @ParameterizedTest
    @MethodSource("dataForArrOperCheckTrue")
    public void testCheckArrTrue(int[] arr) {
        Assertions.assertTrue(arraysApp.checkArr(arr), String.valueOf(true));
    }
    static Stream<Arguments> dataForArrOperCheckTrue() {
        return Stream.of(
                Arguments.arguments((Object) new int[]{1, 1, 4, 4}),
                Arguments.arguments((Object) new int[]{1, 1, 1, 4}),
                Arguments.arguments((Object) new int[]{4, 1, 4, 4})
        );

}
    @ParameterizedTest
    @MethodSource("dataForArrOperCheckFalse")
    public void testCheckArrFalse(int[] arr) {
        Assertions.assertFalse(arraysApp.checkArr(arr), String.valueOf(false));
    }
    static Stream<Arguments> dataForArrOperCheckFalse() {
        return Stream.of(
                Arguments.arguments((Object) new int[]{1, 1, 1, 1}),
                Arguments.arguments((Object) new int[]{4, 4, 4, 4}),
                Arguments.arguments((Object) new int[]{4, 3, 1, 4})
        );

    }

}
