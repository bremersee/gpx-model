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

import java.util.Collection;
import java.util.Collections;
import org.bremersee.gpx.model.ObjectFactory;
import org.bremersee.xml.JaxbContextData;
import org.bremersee.xml.JaxbContextDataProvider;

/**
 * GPX jaxb context data provider.
 *
 * @author Christian Bremer
 */
public class GpxJaxbContextDataProvider implements JaxbContextDataProvider {

  /**
   * The GPX XML name space.
   */
  @SuppressWarnings("WeakerAccess")
  public static final String NAMESPACE = "http://www.topografix.com/GPX/1/1";

  @Override
  public Collection<JaxbContextData> getJaxbContextData() {
    return Collections.singletonList(new JaxbContextData(
        ObjectFactory.class.getPackage(),
        "http://www.topografix.com/GPX/1/1/gpx.xsd"));
  }

}
