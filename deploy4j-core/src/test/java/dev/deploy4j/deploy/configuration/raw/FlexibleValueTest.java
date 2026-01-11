package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlexibleValueTest {

    @Test
    void shouldHandleSingleStringValue() {
        FlexibleValue value = FlexibleValue.from("443:443");

        assertThat(value.asSingleValue()).isEqualTo("443:443");
        assertThat(value.asList()).containsExactly("443:443");
        assertThat(value.isList()).isFalse();
        assertThat(value.isEmpty()).isFalse();
    }

    @Test
    void shouldHandleListOfStrings() {
        FlexibleValue value = FlexibleValue.from(List.of("443:443", "80:80"));

        assertThat(value.asSingleValue()).isEqualTo("443:443");
        assertThat(value.asList()).containsExactly("443:443", "80:80");
        assertThat(value.isList()).isTrue();
        assertThat(value.isEmpty()).isFalse();
    }

    @Test
    void shouldHandleNullValue() {
        FlexibleValue value = FlexibleValue.from(null);

        assertThat(value.asSingleValue()).isNull();
        assertThat(value.asList()).isEmpty();
        assertThat(value.isEmpty()).isTrue();
    }

    @Test
    void shouldHandleEmptyList() {
        FlexibleValue value = FlexibleValue.from(List.of());

        assertThat(value.asSingleValue()).isNull();
        assertThat(value.asList()).isEmpty();
        assertThat(value.isEmpty()).isTrue();
    }

    @Test
    void shouldHandleListWithNullItems() {
        java.util.List<String> listWithNull = new java.util.ArrayList<>();
        listWithNull.add("443:443");
        listWithNull.add(null);
        listWithNull.add("80:80");
        
        FlexibleValue value = FlexibleValue.from(listWithNull);

        assertThat(value.asList()).containsExactly("443:443", "80:80");
        assertThat(value.isList()).isTrue();
    }

    @Test
    void shouldConvertOtherTypesToString() {
        FlexibleValue value = FlexibleValue.from(8080);

        assertThat(value.asSingleValue()).isEqualTo("8080");
        assertThat(value.asList()).containsExactly("8080");
    }

    @Test
    void shouldSerializeToJsonAsSingleValue() {
        FlexibleValue value = FlexibleValue.from("443:443");

        assertThat(value.toJson()).isEqualTo("443:443");
    }

    @Test
    void shouldSerializeToJsonAsList() {
        FlexibleValue value = FlexibleValue.from(List.of("443:443", "80:80"));

        assertThat(value.toJson()).isEqualTo(List.of("443:443", "80:80"));
    }

    @Test
    void shouldSerializeToJsonAsNull() {
        FlexibleValue value = FlexibleValue.from(null);

        assertThat(value.toJson()).isNull();
    }
}
