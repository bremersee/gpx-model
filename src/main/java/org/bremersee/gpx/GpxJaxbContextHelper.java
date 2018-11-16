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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.bremersee.gpx.model.ExtensionsType;
import org.w3c.dom.Element;

/**
 * GPX XML context helper.
 *
 * @author Christian Bremer
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class GpxJaxbContextHelper {

  private GpxJaxbContextHelper() {
  }

  public static Map<Class<?>, List<Object>> parseExtensions(
      final ExtensionsType extensions,
      final JAXBContext jaxbContext) {

    try {
      return parseExtensions(extensions, jaxbContext.createUnmarshaller());
    } catch (final Exception ignored) {
      return parseExtensions(extensions, (Unmarshaller) null);
    }
  }

  public static Map<Class<?>, List<Object>> parseExtensions(
      final ExtensionsType extensions,
      final Unmarshaller unmarshaller) {

    final Map<Class<?>, List<Object>> map = new HashMap<>();
    if (extensions == null || extensions.getAnies() == null) {
      return map;
    }
    for (final Element element : extensions.getAnies()) {
      if (element != null) {
        final Object strictElement = parseElement(element, unmarshaller);
        final List<Object> values = map.computeIfAbsent(
            strictElement.getClass(), k -> new ArrayList<>());
        values.add(strictElement);
      }
    }
    return Collections.unmodifiableMap(map);
  }

  private static Object parseElement(final Element element, final Unmarshaller unmarshaller) {
    try {
      return unmarshaller.unmarshal(element);

    } catch (final Exception ignored) {
      return element;
    }
  }

  public static <T> List<T> findExtensions(
      final Class<T> cls,
      final boolean instancesOf,
      final Map<Class<?>, List<Object>> parsedExtensions) {

    if (cls == null || parsedExtensions == null || parsedExtensions.isEmpty()) {
      return Collections.emptyList();
    }
    final List<T> list = new ArrayList<>();
    for (Map.Entry<Class<?>, List<Object>> entry : parsedExtensions.entrySet()) {
      final Class<?> c = entry.getKey();
      if (cls.equals(c) || (instancesOf && cls.isAssignableFrom(c))) {
        final List<Object> values = entry.getValue();
        if (values != null) {
          for (final Object value : values) {
            if (value != null
                && (cls.equals(value.getClass())
                || (instancesOf && cls.isAssignableFrom(value.getClass())))) {
              //noinspection unchecked
              list.add((T) value);
            }
          }
        }
      }
    }
    return Collections.unmodifiableList(list);
  }

  public static <T> List<T> findExtensions(
      final Class<T> cls,
      final boolean instancesOf,
      final ExtensionsType extensions,
      final JAXBContext jaxbContext) {

    return findExtensions(cls, instancesOf, parseExtensions(extensions, jaxbContext));
  }

  public static <T> List<T> findExtensions(
      final Class<T> cls,
      final boolean instancesOf,
      final ExtensionsType extensions,
      final Unmarshaller unmarshaller) {

    return findExtensions(cls, instancesOf, parseExtensions(extensions, unmarshaller));
  }

  public static <T> Optional<T> findFirstExtension(
      final Class<T> cls,
      final boolean instancesOf,
      final Map<Class<?>, List<Object>> parsedExtensions) {

    final List<T> list = findExtensions(cls, instancesOf, parsedExtensions);
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
  }

  public static <T> Optional<T> findFirstExtension(
      final Class<T> cls,
      final boolean instancesOf,
      final ExtensionsType extensions,
      final JAXBContext jaxbContext) {

    final List<T> list = findExtensions(cls, instancesOf, extensions, jaxbContext);
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
  }

  public static <T> Optional<T> findFirstExtension(
      final Class<T> cls,
      final boolean instancesOf,
      final ExtensionsType extensions,
      final Unmarshaller unmarshaller) {

    final List<T> list = findExtensions(cls, instancesOf, extensions, unmarshaller);
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
  }

}
