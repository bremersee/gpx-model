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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ServiceLoader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.bremersee.gpx.model.Gpx;
import org.bremersee.gpx.model.WptType;
import org.bremersee.xml.JaxbContextBuilder;
import org.bremersee.xml.JaxbContextDataProvider;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * XML schema test.
 *
 * @author Christian Bremer
 */
public class JaxbContextTest {

  private static JAXBContext jaxbContext;

  /**
   * Create jaxb context.
   */
  @BeforeClass
  public static void createJAXBContext() {
    jaxbContext = JaxbContextBuilder
        .builder()
        .processAll(ServiceLoader.load(JaxbContextDataProvider.class))
        .buildJaxbContext();
  }

  /**
   * Test xml schema.
   *
   * @throws IOException   the io exception
   * @throws JAXBException the jaxb exception
   */
  @Test
  public void testXmlSchema() throws IOException, JAXBException {
    System.out.println("Testing XML schema ...");

    final BufferSchemaOutputResolver res = new BufferSchemaOutputResolver();
    jaxbContext.generateSchema(res);
    System.out.print(res);

    System.out.println("OK\n");

    WptType wpt = new WptType();
    wpt.setLat(new BigDecimal("52.4"));
    wpt.setLon(new BigDecimal("10.8"));

    Gpx gpx = new Gpx();
    gpx.setCreator("org.bremersee");
    gpx.setVersion("1.1");
    gpx.getWpts().add(wpt);

    jaxbContext.createMarshaller().marshal(gpx, System.out);
  }
}
