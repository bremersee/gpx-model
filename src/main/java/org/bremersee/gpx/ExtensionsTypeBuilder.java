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
import jakarta.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bremersee.gpx.model.ExtensionsType;
import org.bremersee.xml.XmlDocumentBuilder;
import org.w3c.dom.Element;

/**
 * The extensions builder interface.
 *
 * @author Christian Bremer
 */
public interface ExtensionsTypeBuilder {

  /**
   * Use document builder.
   *
   * @param documentBuilder the document builder
   * @return the extensions builder
   */
  ExtensionsTypeBuilder use(XmlDocumentBuilder documentBuilder);

  /**
   * Add element to the extensions.
   *
   * @param extensionElement the extension element
   * @return the extensions type builder
   */
  ExtensionsTypeBuilder addElement(Element extensionElement);

  /**
   * Add element to the extensions.
   *
   * @param extensionElement the extension element
   * @param jaxbContext the jaxb context
   * @return the extensions type builder
   */
  ExtensionsTypeBuilder addElement(Object extensionElement, JAXBContext jaxbContext);

  /**
   * Add element to the extensions.
   *
   * @param extensionElement the extension element
   * @param marshaller the marshaller
   * @return the extensions type builder
   */
  ExtensionsTypeBuilder addElement(Object extensionElement, Marshaller marshaller);

  /**
   * Build extensions
   *
   * @param returnNullIfEmpty the return null if empty
   * @return the extensions type
   */
  ExtensionsType build(boolean returnNullIfEmpty);

  /**
   * Create default extensions builder.
   *
   * @return the extensions builder
   */
  static ExtensionsTypeBuilder newInstance() {
    return new DefaultBuilder();
  }

  /**
   * Create default extensions builder.
   *
   * @param extensionElements the extension elements
   * @return the extensions type builder
   */
  static ExtensionsTypeBuilder newInstance(Collection<? extends Element> extensionElements) {
    return new DefaultBuilder(extensionElements);
  }

  /**
   * Create default extensions builder.
   *
   * @param extensionsType the extensions type
   * @return the extensions type builder
   */
  static ExtensionsTypeBuilder newInstance(ExtensionsType extensionsType) {
    return new DefaultBuilder(extensionsType);
  }

  /**
   * The default builder.
   */
  class DefaultBuilder implements ExtensionsTypeBuilder {

    private final List<Element> anies = new ArrayList<>();

    private XmlDocumentBuilder documentBuilder = XmlDocumentBuilder.newInstance();

    /**
     * Instantiates a new default builder.
     */
    DefaultBuilder() {
    }

    /**
     * Instantiates a new default builder.
     *
     * @param extensionElements the extension elements
     */
    DefaultBuilder(Collection<? extends Element> extensionElements) {
      if (extensionElements != null) {
        anies.addAll(extensionElements);
      }
    }

    /**
     * Instantiates a new default builder.
     *
     * @param extensionsType the extensions type
     */
    DefaultBuilder(ExtensionsType extensionsType) {
      if (extensionsType != null) {
        anies.addAll(extensionsType.getAnies());
      }
    }

    @Override
    public ExtensionsTypeBuilder use(XmlDocumentBuilder documentBuilder) {
      if (documentBuilder != null) {
        this.documentBuilder = documentBuilder;
      }
      return this;
    }

    @Override
    public ExtensionsTypeBuilder addElement(Element extensionElement) {
      if (extensionElement != null) {
        anies.add(extensionElement);
      }
      return this;
    }

    @Override
    public ExtensionsTypeBuilder addElement(Object extensionElement, JAXBContext jaxbContext) {
      if (extensionElement == null) {
        return this;
      }
      return addElement(
          documentBuilder.buildDocument(extensionElement, jaxbContext).getDocumentElement());
    }

    @Override
    public ExtensionsTypeBuilder addElement(Object extensionElement, Marshaller marshaller) {
      if (extensionElement == null) {
        return this;
      }
      return addElement(
          documentBuilder.buildDocument(extensionElement, marshaller).getDocumentElement());
    }

    @Override
    public ExtensionsType build(boolean returnNullIfEmpty) {
      if (returnNullIfEmpty && anies.isEmpty()) {
        return null;
      }
      final ExtensionsType extensionsType = new ExtensionsType();
      extensionsType.getAnies().addAll(anies);
      return extensionsType;
    }

  }
}
