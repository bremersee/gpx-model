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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bremersee.gpx.model.ExtensionsType;
import org.w3c.dom.Element;

/**
 * GPX XML context helper.
 *
 * @author Christian Bremer
 */
public abstract class GpxJaxbContextHelper {

  private GpxJaxbContextHelper() {
  }

  /**
   * Parses the elemants of the given GPX extensions ({@link ExtensionsType#getAnies()}) with the
   * given {@link JAXBContext}:
   *
   * <p>If the {@link JAXBContext} can unmarshall the {@link Element} to a concrete object, a map
   * entry will be created with the class of the object as key and a list with all unmarshalled
   * objects as value.
   *
   * <p>If the {@link JAXBContext} cannot unmarshall the {@link Element} to a concrete object, a
   * map entry will be created with the class of the element as key and a list with all elements as
   * value.
   *
   * @param extensions the GPX extension
   * @param jaxbContext the {@link JAXBContext} to parse the elements
   * @return an unmodifiable map with the unmarshalled objects (key is the class of the objects,
   *     value is a list with all unmarshalled objects of this class)
   */
  public static Map<Class<?>, List<Object>> parseExtensions(
      final ExtensionsType extensions,
      final JAXBContext jaxbContext) {

    try {
      return parseExtensions(extensions, jaxbContext.createUnmarshaller());
    } catch (final Exception ignored) {
      return parseExtensions(extensions, (Unmarshaller) null);
    }
  }

  /**
   * Parses the elemants of the given GPX extensions ({@link ExtensionsType#getAnies()}) with the
   * given {@link Unmarshaller}:
   *
   * <p>If the {@link Unmarshaller} can unmarshall the {@link Element} to a concrete object, a map
   * entry will be created with the class of the object as key and a list with all unmarshalled
   * objects as value.
   *
   * <p>If the {@link Unmarshaller} cannot unmarshall the {@link Element} to a concrete object, a
   * map entry will be created with the class of the element as key and a list with all elements as
   * value.
   *
   * @param extensions the GPX extension
   * @param unmarshaller the {@link Unmarshaller} to parse the elements
   * @return an unmodifiable map with the unmarshalled objects (key is the class of the objects,
   *     value is a list with all unmarshalled objects of this class)
   */
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

  /**
   * Find all extensions of the given type.
   *
   * @param cls the type
   * @param instancesOf if {@code true} instanceof will be used, otherwise {@link
   *     Class#equals(Object)} will be used
   * @param parsedExtensions the parsed extensions (see {@link GpxJaxbContextHelper#parseExtensions(ExtensionsType,
   *     JAXBContext)})
   * @param <T> the type
   * @return an unmodifiable list of all extensions of the given type
   */
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

  /**
   * Find all extensions of the given type.
   *
   * @param cls the type
   * @param instancesOf if {@code true} instanceof will be used, otherwise {@link
   *     Class#equals(Object)} will be used
   * @param extensions the GPX extensions
   * @param jaxbContext the {@link JAXBContext} to parse the elements
   * @param <T> the type
   * @return an unmodifiable list of all extensions of the given type
   */
  public static <T> List<T> findExtensions(
      final Class<T> cls,
      final boolean instancesOf,
      final ExtensionsType extensions,
      final JAXBContext jaxbContext) {

    return findExtensions(cls, instancesOf, parseExtensions(extensions, jaxbContext));
  }

  /**
   * Find all extensions of the given type.
   *
   * @param cls the type
   * @param instancesOf if {@code true} instanceof will be used, otherwise {@link
   *     Class#equals(Object)} will be used
   * @param extensions the GPX extensions
   * @param unmarshaller the {@link Unmarshaller} to parse the elements
   * @param <T> the type
   * @return an unmodifiable list of all extensions of the given type
   */
  public static <T> List<T> findExtensions(
      final Class<T> cls,
      final boolean instancesOf,
      final ExtensionsType extensions,
      final Unmarshaller unmarshaller) {

    return findExtensions(cls, instancesOf, parseExtensions(extensions, unmarshaller));
  }

  /**
   * Find the first extension of the given type.
   *
   * @param cls the type
   * @param instancesOf if {@code true} instanceof will be used, otherwise {@link
   *     Class#equals(Object)} will be used
   * @param parsedExtensions the parsed extensions (see {@link GpxJaxbContextHelper#parseExtensions(ExtensionsType,
   *     JAXBContext)})
   * @param <T> the type
   * @return {@link Optional#empty()} if there is no such element, otherwise an optional with the
   *     parsed element
   */
  public static <T> Optional<T> findFirstExtension(
      final Class<T> cls,
      final boolean instancesOf,
      final Map<Class<?>, List<Object>> parsedExtensions) {

    final List<T> list = findExtensions(cls, instancesOf, parsedExtensions);
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
  }

  /**
   * Find the first extension of the given type.
   *
   * @param cls the type
   * @param instancesOf if {@code true} instanceof will be used, otherwise {@link
   *     Class#equals(Object)} will be used
   * @param extensions the GPX extensions
   * @param jaxbContext the {@link JAXBContext} to parse the elements
   * @param <T> the type
   * @return {@link Optional#empty()} if there is no such element, otherwise an optional with the
   *     parsed element
   */
  public static <T> Optional<T> findFirstExtension(
      final Class<T> cls,
      final boolean instancesOf,
      final ExtensionsType extensions,
      final JAXBContext jaxbContext) {

    final List<T> list = findExtensions(cls, instancesOf, extensions, jaxbContext);
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
  }

  /**
   * Find the first extension of the given type.
   *
   * @param cls the type
   * @param instancesOf if {@code true} instanceof will be used, otherwise {@link
   *     Class#equals(Object)} will be used
   * @param extensions the GPX extensions
   * @param unmarshaller the {@link Unmarshaller} to parse the elements
   * @param <T> the type
   * @return {@link Optional#empty()} if there is no such element, otherwise an optional with the
   *     parsed element
   */
  public static <T> Optional<T> findFirstExtension(
      final Class<T> cls,
      final boolean instancesOf,
      final ExtensionsType extensions,
      final Unmarshaller unmarshaller) {

    final List<T> list = findExtensions(cls, instancesOf, extensions, unmarshaller);
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
  }

}
