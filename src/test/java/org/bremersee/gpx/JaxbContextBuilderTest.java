/*
 * Copyright 2018-2020 the original author or authors.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import javax.xml.datatype.XMLGregorianCalendar;
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
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * The jaxb context builder test.
 *
 * @author Christian Bremer
 */
class JaxbContextBuilderTest {

  private static final ResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

  private static JaxbContextBuilder jaxbContextBuilder;

  /**
   * Create jaxb context builder.
   */
  @BeforeAll
  static void createJaxbContextBuilder() {
    jaxbContextBuilder = JaxbContextBuilder.builder()
        .withSchemaMode(SchemaMode.ALWAYS)
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
   * @throws Exception the exception
   */
  @Test
  void testGpxWithWpt() throws Exception {
    CategoriesT categories = new CategoriesT();
    categories.getCategories().add("JUNIT");

    AddressT address = new AddressT();
    address.setCountry("Italy");
    address.setCountry("Rom");

    WaypointExtension waypointExtension = new WaypointExtension();
    waypointExtension.setCategories(categories);
    waypointExtension.setAddress(address);

    ExtensionsType extensionsType = ExtensionsTypeBuilder.builder()
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
    assertNotNull(xml);
    // System.out.println(xml);

    Gpx readGpx = (Gpx) jaxbContextBuilder.buildUnmarshaller(Gpx.class)
        .unmarshal(new StringReader(xml));
    assertNotNull(readGpx);
    assertFalse(readGpx.getWpts().isEmpty());

    WptType readWpt = readGpx.getWpts().get(0);
    assertEquals(wpt.getLat(), readWpt.getLat());
    assertEquals(wpt.getLon(), readWpt.getLon());
    assertEquals(wpt.getSrc(), readWpt.getSrc());
  }

  /**
   * Test address data.
   *
   * @throws Exception the exception
   */
  @Test
  void testAddressData() throws Exception {
    final Object obj = unmarshalClassPathResource("classpath:Adresse.GPX");
    assertNotNull(obj);
    assertTrue(obj instanceof Gpx);

    final Gpx gpx = (Gpx) obj;
    assertFalse(gpx.getWpts().isEmpty());

    final WptType wpt = gpx.getWpts().get(0);
    assertNotNull(wpt.getExtensions());

    final ExtensionsType extensions = wpt.getExtensions();
    assertNotNull(extensions.getAnies());
    assertFalse(extensions.getAnies().isEmpty());

    final List<WaypointExtension> waypointExtensions = GpxJaxbContextHelper.findExtensions(
        WaypointExtension.class,
        true,
        GpxJaxbContextHelper.parseExtensions(extensions, jaxbContextBuilder.buildJaxbContext()));

    assertFalse(waypointExtensions.isEmpty());
    WaypointExtension wptExt = waypointExtensions.get(0);
    assertNotNull(wptExt.getAddress());
    assertNotNull(wptExt.getAddress().getStreetAddresses());
    assertFalse(wptExt.getAddress().getStreetAddresses().isEmpty());
    assertEquals("Seerosenweg 1", wptExt.getAddress().getStreetAddresses().get(0));

    Optional<WaypointExtension> optionalWaypointExtension = GpxJaxbContextHelper
        .findFirstExtension(
            WaypointExtension.class,
            true,
            extensions,
            jaxbContextBuilder.buildUnmarshaller());

    assertTrue(optionalWaypointExtension.isPresent());
    wptExt = optionalWaypointExtension.get();
    assertNotNull(wptExt.getAddress());
    assertNotNull(wptExt.getAddress().getStreetAddresses());
    assertFalse(wptExt.getAddress().getStreetAddresses().isEmpty());
    assertEquals("Seerosenweg 1", wptExt.getAddress().getStreetAddresses().get(0));
  }

  /**
   * Test picture data.
   *
   * @throws Exception the exception
   */
  @Test
  void testPictureData() throws Exception {
    final Object obj = unmarshalClassPathResource("classpath:Bild.GPX");
    assertNotNull(obj);
    assertTrue(obj instanceof Gpx);

    final Gpx gpx = (Gpx) obj;
    assertFalse(gpx.getWpts().isEmpty());

    final WptType wpt = gpx.getWpts().get(0);
    assertNotNull(wpt.getExtensions());

    final ExtensionsType extensions = wpt.getExtensions();
    assertNotNull(extensions.getAnies());
    assertFalse(extensions.getAnies().isEmpty());

    Optional<CreationTimeExtension> cr = GpxJaxbContextHelper.findFirstExtension(
        CreationTimeExtension.class, true, extensions, jaxbContextBuilder.buildJaxbContext());
    assertTrue(cr.isPresent());
    final XMLGregorianCalendar cal = cr.get().getCreationTime();
    assertNotNull(cal);
    assertEquals(2012, cal.getYear());
  }

  /**
   * Test route.
   *
   * @throws Exception the exception
   */
  @Test
  void testRoute() throws Exception {
    Object obj = unmarshalClassPathResource("classpath:Route.GPX");
    assertNotNull(obj);
    assertTrue(obj instanceof Gpx);

    StringWriter sw = new StringWriter();
    jaxbContextBuilder.buildMarshaller(obj).marshal(obj, sw);
    String xml = sw.toString();
    assertNotNull(xml);
    assertTrue(xml.length() > 0);
  }

  /**
   * Test track.
   *
   * @throws Exception the exception
   */
  @Test
  void testTrack() throws Exception {
    Object obj = unmarshalClassPathResource("classpath:Track.GPX");
    assertNotNull(obj);
    assertTrue(obj instanceof Gpx);

    StringWriter sw = new StringWriter();
    jaxbContextBuilder.buildMarshaller(obj).marshal(obj, sw);
    String xml = sw.toString();
    assertNotNull(xml);
    assertTrue(xml.length() > 0);
  }

}
