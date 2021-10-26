package com.dmytroporoshyn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ShoppingCartTest {

    @DisplayName("Testing discount calculation for the Shopping card")
    @ParameterizedTest(name = "When type = {1} and quantity = {2} then discount should = {0}")
    @MethodSource("calculateDiscountSourceValues")
    void calculateDiscount(int expectedDiscount, ShoppingCart.ItemType type, int quantity) {
        assertEquals(expectedDiscount,
                ShoppingCart.calculateDiscount(type, quantity));
    }


    static Stream<Arguments> calculateDiscountSourceValues() {
        return Stream.of(
                arguments(0, ShoppingCart.ItemType.NEW, 1),
                arguments(0, ShoppingCart.ItemType.NEW, 20),
                arguments(0, ShoppingCart.ItemType.SECOND_FREE, 1),
                arguments(50, ShoppingCart.ItemType.SECOND_FREE, 2),
                arguments(60, ShoppingCart.ItemType.SECOND_FREE, 100),
                arguments(70, ShoppingCart.ItemType.SECOND_FREE, 200),
                arguments(80, ShoppingCart.ItemType.SECOND_FREE, 300),
                arguments(80, ShoppingCart.ItemType.SECOND_FREE, 400),
                arguments(70, ShoppingCart.ItemType.SALE, 1),
                arguments(80, ShoppingCart.ItemType.SALE, 100),
                arguments(80, ShoppingCart.ItemType.SALE, 200),
                arguments(0, ShoppingCart.ItemType.REGULAR, 1),
                arguments(1, ShoppingCart.ItemType.REGULAR, 10),
                arguments(80, ShoppingCart.ItemType.REGULAR, 800),
                arguments(80, ShoppingCart.ItemType.REGULAR, 900)
        );
    }

    @DisplayName("Testing append formatted for the Shopping card")
    @ParameterizedTest(name = "When align = {1} and width = {2} then value = {0}")
    @MethodSource("appendFormattedSourceValues")
    void appendFormatted(String expectedValue, int align, int width) {
        String testValue = "test";
        StringBuilder sb = new StringBuilder();
        ShoppingCart.appendFormatted(sb, testValue, align, width);
        assertEquals(expectedValue + " ", sb.toString());
    }

    static Stream<Arguments> appendFormattedSourceValues() {
        return Stream.of(
                arguments("test  ", -1, 6),
                arguments("tes", -1, 3),
                arguments("  test", 1, 6),
                arguments("tes", 1, 3),
                arguments(" test ", 0, 6),
                arguments("tes", 0, 3)
        );
    }
}