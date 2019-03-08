/*
 * Copyright 2018 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bremersee.garmin.creationtime.v1.model.ext.CreationTimeExtension;
import org.bremersee.garmin.gpx.v3.model.ext.WaypointExtension;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.LinkType;
import org.bremersee.gpx.model.WptType;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XML schema test.
 *
 * @author Christian Bremer
 */
public class JaxbContextTest {

  private static JAXBContext jaxbContext;

  private static JAXBContext jaxbContextWithGarmin;

  /**
   * Create jaxb context.
   */
  @BeforeClass
  public static void createJAXBContext() {
    final JaxbContextBuilder builder = JaxbContextBuilder
        .builder()
        .processAll(ServiceLoader.load(JaxbContextDataProvider.class));

    jaxbContext = builder.buildJaxbContext(GpxJaxbContextDataProvider.NAMESPACE);
    jaxbContextWithGarmin = builder.buildJaxbContext();
  }

  private static Object unmarshalClassPathResource(
      final JAXBContext jaxbContext,
      final String classPathResource) throws JAXBException {

    return jaxbContext
        .createUnmarshaller()
        .unmarshal(JaxbContextTest.class.getResourceAsStream(classPathResource));
  }

  /**
   * Test xml schema.
   *
   * @throws IOException the io exception
   */
  @Test
  public void testXmlSchema() throws IOException {
    System.out.println("Testing XML schema ...");

    final BufferSchemaOutputResolver res = new BufferSchemaOutputResolver();
    jaxbContext.generateSchema(res);
    System.out.print(res);

    System.out.println("OK\n");
  }

  /**
   * Test gpx with wpt.
   *
   * @throws JAXBException the jaxb exception
   */
  @Test
  public void testGpxWithWpt() throws JAXBException {
    LinkType link = new LinkType();
    link.setHref("http://localhost");

    WptType wpt = new WptType();
    wpt.setLat(new BigDecimal("52.4"));
    wpt.setLon(new BigDecimal("10.8"));
    wpt.setSrc("test");
    wpt.getLinks().add(link);

    Gpx gpx = new Gpx();
    gpx.setCreator("org.bremersee");
    gpx.setVersion("1.1");
    gpx.getWpts().add(wpt);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    jaxbContext.createMarshaller().marshal(gpx, System.out);
    jaxbContext.createMarshaller().marshal(gpx, out);

    Gpx readGpx = (Gpx) jaxbContext.createUnmarshaller()
        .unmarshal(new ByteArrayInputStream(out.toByteArray()));
    Assert.assertNotNull(readGpx);
    Assert.assertFalse(readGpx.getWpts().isEmpty());
    WptType readWpt = readGpx.getWpts().get(0);
    Assert.assertEquals(wpt.getLat(), readWpt.getLat());
    Assert.assertEquals(wpt.getLon(), readWpt.getLon());
    Assert.assertEquals(wpt.getSrc(), readWpt.getSrc());
  }

  /**
   * Test address.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAddress() throws Exception {
    final Object obj = unmarshalClassPathResource(jaxbContextWithGarmin, "/Adresse.GPX");
    Assert.assertNotNull(obj);
    jaxbContextWithGarmin.createMarshaller().marshal(obj, System.out);
  }

  /**
   * Test address data.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAddressData() throws Exception {
    final Object obj = unmarshalClassPathResource(jaxbContextWithGarmin, "/Adresse.GPX");
    Assert.assertNotNull(obj);
    Assert.assertTrue(obj instanceof Gpx);

    final Gpx gpx = (Gpx) obj;
    Assert.assertFalse(gpx.getWpts().isEmpty());

    final WptType wpt = gpx.getWpts().get(0);
    Assert.assertNotNull(wpt.getExtensions());

    final ExtensionsType extensions = wpt.getExtensions();
    Assert.assertNotNull(extensions.getAnies());
    Assert.assertFalse(extensions.getAnies().isEmpty());

    final List<WaypointExtension> waypointExtensions = GpxJaxbContextHelper.findExtensions(
        WaypointExtension.class,
        true,
        GpxJaxbContextHelper.parseExtensions(extensions, jaxbContextWithGarmin));

    Assert.assertFalse(waypointExtensions.isEmpty());
    WaypointExtension wptExt = waypointExtensions.get(0);
    Assert.assertNotNull(wptExt.getAddress());
    Assert.assertNotNull(wptExt.getAddress().getStreetAddresses());
    Assert.assertFalse(wptExt.getAddress().getStreetAddresses().isEmpty());
    Assert.assertEquals("Seerosenweg 1", wptExt.getAddress().getStreetAddresses().get(0));

    Optional<WaypointExtension> optionalWaypointExtension = GpxJaxbContextHelper
        .findFirstExtension(
            WaypointExtension.class,
            true,
            extensions,
            jaxbContextWithGarmin.createUnmarshaller());

    Assert.assertTrue(optionalWaypointExtension.isPresent());
    wptExt = optionalWaypointExtension.get();
    Assert.assertNotNull(wptExt.getAddress());
    Assert.assertNotNull(wptExt.getAddress().getStreetAddresses());
    Assert.assertFalse(wptExt.getAddress().getStreetAddresses().isEmpty());
    Assert.assertEquals("Seerosenweg 1", wptExt.getAddress().getStreetAddresses().get(0));
  }

  /**
   * Test picture data.
   *
   * @throws Exception the exception
   */
  @Test
  public void testPictureData() throws Exception {
    final Object obj = unmarshalClassPathResource(jaxbContextWithGarmin, "/Bild.GPX");
    Assert.assertNotNull(obj);
    Assert.assertTrue(obj instanceof Gpx);

    final Gpx gpx = (Gpx) obj;
    Assert.assertFalse(gpx.getWpts().isEmpty());

    final WptType wpt = gpx.getWpts().get(0);
    Assert.assertNotNull(wpt.getExtensions());

    final ExtensionsType extensions = wpt.getExtensions();
    Assert.assertNotNull(extensions.getAnies());
    Assert.assertFalse(extensions.getAnies().isEmpty());

    Optional<CreationTimeExtension> cr = GpxJaxbContextHelper.findFirstExtension(
        CreationTimeExtension.class, true, extensions, jaxbContextWithGarmin);
    Assert.assertTrue(cr.isPresent());
    final XMLGregorianCalendar cal = cr.get().getCreationTime();
    Assert.assertNotNull(cal);
    Assert.assertEquals(2012, cal.getYear());
  }

}
