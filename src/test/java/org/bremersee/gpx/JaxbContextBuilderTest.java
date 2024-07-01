/*
 * Copyright 2019-2022 the original author or authors.
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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import javax.xml.datatype.XMLGregorianCalendar;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.garmin.creationtime.v1.model.ext.CreationTimeExtension;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.bremersee.garmin.gpx.v3.model.ext.CategoriesT;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.LinkType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.bremersee.xml.SchemaMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * The jaxb context builder test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class JaxbContextBuilderTest {

  private static final ResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

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

  private static Object unmarshalClassPathResource(final String classPathResource)
      throws Exception {
    return jaxbContextBuilder
        .buildUnmarshaller()
        .unmarshal(RESOURCE_LOADER.getResource(classPathResource).getInputStream());
  }

  /**
   * Test gpx with wpt.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void testGpxWithWpt(SoftAssertions softly) throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensionsType = ExtensionsTypeBuilder.newInstance()
        .addElement(waypointExtension, jaxbContextBuilder.buildMarshaller(waypointExtension))
        .build(true);

    LinkType link = new LinkType();
    link.setHref("http://localhost");

    WptType wpt = new WptType();
    wpt.setLat(new BigDecimal("52.4"));
    wpt.setLon(new BigDecimal("10.8"));
    wpt.setSrc("test");
    wpt.getLinks().add(link);
    wpt.setExtensions(extensionsType);

    Gpx gpx = new Gpx();
    gpx.setCreator("org.bremersee");
    gpx.setVersion("1.1");
    gpx.getWpts().add(wpt);

    StringWriter sw = new StringWriter();
    jaxbContextBuilder.buildMarshaller(gpx).marshal(gpx, sw);
    String xml = sw.toString();
    softly.assertThat(xml).isNotNull();

    Gpx readGpx = (Gpx) jaxbContextBuilder.buildUnmarshaller(Gpx.class)
        .unmarshal(new StringReader(xml));
    softly.assertThat(readGpx).isNotNull();
    softly.assertThat(readGpx.getWpts()).isNotEmpty();

    WptType readWpt = readGpx.getWpts().get(0);
    softly.assertThat(readWpt.getLat()).isEqualTo(wpt.getLat());
    softly.assertThat(readWpt.getLon()).isEqualTo(wpt.getLon());
    softly.assertThat(readWpt.getSrc()).isEqualTo(wpt.getSrc());
  }

  /**
   * Test address data.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void testAddressData(SoftAssertions softly) throws Exception {
    final Object obj = unmarshalClassPathResource("classpath:Adresse.GPX");
    softly.assertThat(obj).isInstanceOf(Gpx.class);

    final Gpx gpx = (Gpx) obj;
    softly.assertThat(gpx.getWpts()).isNotEmpty();

    final WptType wpt = gpx.getWpts().get(0);
    softly.assertThat(wpt.getExtensions()).isNotNull();

    final ExtensionsType extensions = wpt.getExtensions();
    softly.assertThat(extensions.getAnies()).isNotEmpty();

    final List<WaypointExtension> waypointExtensions = GpxJaxbContextHelper.findExtensions(
        WaypointExtension.class,
        true,
        GpxJaxbContextHelper.parseExtensions(extensions, jaxbContextBuilder.buildJaxbContext()));
    softly.assertThat(waypointExtensions).isNotEmpty();

    WaypointExtension wptExt = waypointExtensions.get(0);
    softly.assertThat(wptExt).isNotNull();
    softly.assertThat(wptExt.getAddress()).isNotNull();
    softly.assertThat(wptExt.getAddress().getStreetAddresses()).isNotEmpty();
    softly.assertThat(wptExt.getAddress().getStreetAddresses()).contains("Seerosenweg 1");

    Optional<WaypointExtension> optionalWaypointExtension = GpxJaxbContextHelper
        .findFirstExtension(
            WaypointExtension.class,
            true,
            extensions,
            jaxbContextBuilder.buildUnmarshaller());

    softly.assertThat(optionalWaypointExtension)
        .map(WaypointExtension::getAddress)
        .map(AddressT::getStreetAddresses)
        .contains(List.of("Seerosenweg 1"));
  }

  /**
   * Test picture data.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void testPictureData(SoftAssertions softly) throws Exception {
    final Object obj = unmarshalClassPathResource("classpath:Bild.GPX");
    softly.assertThat(obj).isInstanceOf(Gpx.class);

    final Gpx gpx = (Gpx) obj;
    softly.assertThat(gpx.getWpts()).isNotEmpty();

    final WptType wpt = gpx.getWpts().get(0);
    softly.assertThat(wpt.getExtensions()).isNotNull();

    final ExtensionsType extensions = wpt.getExtensions();
    softly.assertThat(extensions.getAnies()).isNotEmpty();

    Optional<CreationTimeExtension> cr = GpxJaxbContextHelper.findFirstExtension(
        CreationTimeExtension.class, true, extensions, jaxbContextBuilder.buildJaxbContext());

    softly.assertThat(cr)
        .map(CreationTimeExtension::getCreationTime)
        .map(XMLGregorianCalendar::getYear)
        .get()
        .isEqualTo(2012);
  }

  /**
   * Test route.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void testRoute(SoftAssertions softly) throws Exception {
    Object obj = unmarshalClassPathResource("classpath:Route.GPX");
    softly.assertThat(obj).isInstanceOf(Gpx.class);

    StringWriter sw = new StringWriter();
    jaxbContextBuilder.buildMarshaller(obj).marshal(obj, sw);
    String xml = sw.toString();
    softly.assertThat(xml).containsIgnoringCase("<gpx");
  }

  /**
   * Test track.
   *
   * @param softly the soft assertions
   * @throws Exception the exception
   */
  @Test
  void testTrack(SoftAssertions softly) throws Exception {
    Object obj = unmarshalClassPathResource("classpath:Track.GPX");
    assertNotNull(obj);
    assertInstanceOf(Gpx.class, obj);

    StringWriter sw = new StringWriter();
    jaxbContextBuilder.buildMarshaller(obj).marshal(obj, sw);
    String xml = sw.toString();
    softly.assertThat(xml).containsIgnoringCase("<gpx");
  }

}
