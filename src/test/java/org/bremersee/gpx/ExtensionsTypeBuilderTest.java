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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.ServiceLoader;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.bremersee.garmin.gpx.v3.model.ext.CategoriesT;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.bremersee.xml.SchemaMode;
import org.bremersee.xml.XmlDocumentBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Element;

/**
 * The extensions type builder test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class ExtensionsTypeBuilderTest {

  private static final XmlDocumentBuilder documentBuilder = XmlDocumentBuilder.newInstance();

  private static JaxbContextBuilder jaxbContextBuilder;

  /**
   * Create jaxb context builder.
   */
  @BeforeAll
  static void createJaxbContextBuilder() {
    jaxbContextBuilder = JaxbContextBuilder.newInstance()
        .withSchemaMode(SchemaMode.ALWAYS)
        .processAll(ServiceLoader.load(JaxbContextDataProvider.class))
        .initJaxbContext();
  }

  /**
   * Use.
   */
  @Test
  void use() {
    ExtensionsTypeBuilder actual = ExtensionsTypeBuilder.newInstance()
        .use(documentBuilder);
    assertThat(actual).isNotNull();
  }

  /**
   * Add no element.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void addNoElement(SoftAssertions softly) throws Exception {
    ExtensionsType actual = ExtensionsTypeBuilder.newInstance()
        .addElement(null)
        .build(false);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual.getAnies()).isEmpty();

    actual = ExtensionsTypeBuilder.newInstance()
        .addElement(null, jaxbContextBuilder.buildJaxbContext())
        .build(false);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual.getAnies()).isEmpty();

    actual = ExtensionsTypeBuilder.newInstance()
        .addElement(null, jaxbContextBuilder.buildJaxbContext().createMarshaller())
        .build(true);

    softly.assertThat(actual).isNull();
  }

  /**
   * Add element.
   *
   * @param softly the soft assertions
   */
  @Test
  void addElement(SoftAssertions softly) {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    Element element = documentBuilder
        .buildDocument(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .getDocumentElement();

    ExtensionsType actual = ExtensionsTypeBuilder.newInstance()
        .addElement(element)
        .build(true);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual.getAnies()).isNotEmpty();
  }

  /**
   * Add element with jaxb context.
   *
   * @param softly the soft assertions
   */
  @Test
  void addElementWithJaxbContext(SoftAssertions softly) {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType actual = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .build(true);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual.getAnies()).isNotEmpty();
  }

  /**
   * Add element with marshaller.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void addElementWithMarshaller(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType actual = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildJaxbContext().createMarshaller())
        .build(true);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual.getAnies()).isNotEmpty();
  }

  /**
   * Build.
   */
  @Test
  void build() {
  }

  /**
   * Builder with elements.
   *
   * @param softly the soft assertions
   */
  @Test
  void builderWithElements(SoftAssertions softly) {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    Element element = documentBuilder
        .buildDocument(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .getDocumentElement();

    ExtensionsType actual = ExtensionsTypeBuilder.newInstance(List.of(element))
        .build(true);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual.getAnies()).isNotEmpty();
  }

  /**
   * Builder from extensions type.
   *
   * @param softly the soft assertions
   */
  @Test
  void builderFromExtensionsType(SoftAssertions softly) {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    Element element = documentBuilder
        .buildDocument(waypointExtension, jaxbContextBuilder.buildJaxbContext())
        .getDocumentElement();

    ExtensionsType tmp = ExtensionsTypeBuilder.newInstance(List.of(element))
        .build(true);

    ExtensionsType actual = ExtensionsTypeBuilder.newInstance(tmp)
        .build(true);

    softly.assertThat(actual).isNotNull();
    softly.assertThat(actual.getAnies()).isNotEmpty();
  }

}