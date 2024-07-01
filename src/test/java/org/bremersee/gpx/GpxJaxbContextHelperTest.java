/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.gpx;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.bremersee.garmin.gpx.v3.model.ext.CategoriesT;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.garmin.model.CommonWaypointExtension;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.bremersee.xml.SchemaMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The gpx jaxb context helper test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class GpxJaxbContextHelperTest {

  private static JaxbContextBuilder jaxbContextBuilder;

  /**
   * Create jaxb context builder.
   */
  @BeforeAll
  static void createJaxbContextBuilder() {
    jaxbContextBuilder = JaxbContextBuilder.newInstance()
        .withSchemaMode(SchemaMode.NEVER)
        .processAll(ServiceLoader.load(JaxbContextDataProvider.class))
        .initJaxbContext();
  }

  /**
   * Parse extensions with jaxb context.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void parseExtensionsWithJaxbContext(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensions = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .build(false);

    Map<Class<?>, List<Object>> actual = GpxJaxbContextHelper
        .parseExtensions(extensions, jaxbContextBuilder.buildJaxbContext());

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual).containsOnlyKeys(WaypointExtension.class);
    softly.assertThat(actual.get(WaypointExtension.class)).hasSize(1);

    WaypointExtension actualWaypointExtension = (WaypointExtension) actual
        .get(WaypointExtension.class)
        .get(0);
    StringWriter actualStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(actualWaypointExtension, actualStringWriter);

    StringWriter expectedStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(waypointExtension, expectedStringWriter);

    softly.assertThat(actualStringWriter.toString()).isEqualTo(expectedStringWriter.toString());
  }

  /**
   * Parse extensions with jaxb context and expect element.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void parseExtensionsWithJaxbContextAndExpectElement(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensions = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .build(false);

    JAXBContext jaxbContext = mock(JAXBContext.class);
    when(jaxbContext.createUnmarshaller())
        .thenThrow(new JAXBException("Thrown for testing"));

    Map<Class<?>, List<Object>> actual = GpxJaxbContextHelper
        .parseExtensions(extensions, jaxbContext);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual).hasSize(1);
    softly.assertThat(actual.entrySet())
        .allMatch(entry -> Element.class.isAssignableFrom(entry.getKey()));
  }

  /**
   * Parse extensions with unmarshaller and empty extensions.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void parseExtensionsWithUnmarshallerAndEmptyExtensions(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensions = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .build(false);

    Unmarshaller unmarshaller = mock(Unmarshaller.class);
    when(unmarshaller.unmarshal(any(Node.class)))
        .thenThrow(new JAXBException("Thrown for testing"));

    Map<Class<?>, List<Object>> actual = GpxJaxbContextHelper
        .parseExtensions(extensions, unmarshaller);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual).hasSize(1);
    softly.assertThat(actual.entrySet())
        .allMatch(entry -> Element.class.isAssignableFrom(entry.getKey()));
  }

  /**
   * Parse extensions with unmarshaller and expect element.
   *
   * @param softly the soft assertions
   */
  @Test
  void parseExtensionsWithUnmarshallerAndExpectElement(SoftAssertions softly) {
    Map<Class<?>, List<Object>> actual = GpxJaxbContextHelper
        .parseExtensions(null, jaxbContextBuilder.buildUnmarshaller());

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual).isEmpty();
  }

  /**
   * Find extensions.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void findExtensions(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    Map<Class<?>, List<Object>> parsedExtensions = Map
        .of(WaypointExtension.class, List.of(waypointExtension));

    List<WaypointExtension> actual = GpxJaxbContextHelper
        .findExtensions(WaypointExtension.class, false, parsedExtensions);

    softly.assertThat(actual).hasSize(1);

    WaypointExtension actualWaypointExtension = actual.get(0);
    StringWriter actualStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(actualWaypointExtension, actualStringWriter);

    StringWriter expectedStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(waypointExtension, expectedStringWriter);

    softly.assertThat(actualStringWriter.toString()).isEqualTo(expectedStringWriter.toString());
  }

  /**
   * Find extensions with instance of.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void findExtensionsWithInstanceOf(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    Map<Class<?>, List<Object>> parsedExtensions = Map
        .of(WaypointExtension.class, List.of(waypointExtension));

    List<CommonWaypointExtension> actual = GpxJaxbContextHelper
        .findExtensions(CommonWaypointExtension.class, true, parsedExtensions);

    softly.assertThat(actual).hasSize(1);

    CommonWaypointExtension actualWaypointExtension = actual.get(0);
    StringWriter actualStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(actualWaypointExtension, actualStringWriter);

    StringWriter expectedStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(waypointExtension, expectedStringWriter);

    softly.assertThat(actualStringWriter.toString()).isEqualTo(expectedStringWriter.toString());
  }

  /**
   * Find extensions with empty map.
   *
   * @param softly the soft assertions
   */
  @Test
  void findExtensionsWithEmptyMap(SoftAssertions softly) {
    Map<Class<?>, List<Object>> parsedExtensions = Map.of();

    List<WaypointExtension> actual = GpxJaxbContextHelper
        .findExtensions(WaypointExtension.class, false, parsedExtensions);

    softly.assertThat(actual).isEmpty();
  }

  /**
   * Find extensions with jaxb context.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void findExtensionsWithJaxbContext(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensions = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .build(false);

    List<WaypointExtension> actual = GpxJaxbContextHelper
        .findExtensions(WaypointExtension.class, false, extensions,
            jaxbContextBuilder.buildJaxbContext());

    softly.assertThat(actual).hasSize(1);

    WaypointExtension actualWaypointExtension = actual.get(0);
    StringWriter actualStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(actualWaypointExtension, actualStringWriter);

    StringWriter expectedStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(waypointExtension, expectedStringWriter);

    softly.assertThat(actualStringWriter.toString()).isEqualTo(expectedStringWriter.toString());
  }

  /**
   * Find extensions with unmarshaller.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void findExtensionsWithUnmarshaller(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensions = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .build(false);

    List<WaypointExtension> actual = GpxJaxbContextHelper
        .findExtensions(WaypointExtension.class, false, extensions,
            jaxbContextBuilder.buildUnmarshaller());

    softly.assertThat(actual).hasSize(1);

    WaypointExtension actualWaypointExtension = actual.get(0);
    StringWriter actualStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(actualWaypointExtension, actualStringWriter);

    StringWriter expectedStringWriter = new StringWriter();
    jaxbContextBuilder.buildMarshaller().marshal(waypointExtension, expectedStringWriter);

    softly.assertThat(actualStringWriter.toString()).isEqualTo(expectedStringWriter.toString());
  }

  /**
   * Find first extension of parsed extensions.
   *
   * @param softly the soft assertions
   */
  @Test
  void findFirstExtensionOfParsedExtensions(SoftAssertions softly) {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    Map<Class<?>, List<Object>> parsedExtensions = Map
        .of(WaypointExtension.class, List.of(waypointExtension));

    Optional<CommonWaypointExtension> actual = GpxJaxbContextHelper
        .findFirstExtension(CommonWaypointExtension.class, true, parsedExtensions);

    softly.assertThat(actual).isPresent();
  }

  /**
   * Find first extension with jaxb context.
   *
   * @param softly the soft assertions
   */
  @Test
  void findFirstExtensionWithJaxbContext(SoftAssertions softly) {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensions = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .build(false);

    Optional<WaypointExtension> actual = GpxJaxbContextHelper
        .findFirstExtension(WaypointExtension.class, false, extensions,
            jaxbContextBuilder.buildUnmarshaller());

    softly.assertThat(actual).isPresent();
  }

  /**
   * Find first extension with unmarshaller.
   *
   * @param softly the soft assertions
   */
  @Test
  void findFirstExtensionWithUnmarshaller(SoftAssertions softly) {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensions = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .build(false);

    Optional<WaypointExtension> actual = GpxJaxbContextHelper
        .findFirstExtension(WaypointExtension.class, false, extensions,
            jaxbContextBuilder.buildJaxbContext());

    softly.assertThat(actual).isPresent();
  }
}