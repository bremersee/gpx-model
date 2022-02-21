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

package org.bremersee.gpx.model;

import java.util.List;

/**
 * The common gpx interface.
 *
 * @author Christian Bremer
 */
public interface CommonGpxType {

  /**
   * Gets name.
   *
   * @return the name
   */
  String getName();

  /**
   * Sets name.
   *
   * @param value the value
   */
  void setName(String value);

  /**
   * Gets cmt.
   *
   * @return the cmt
   */
  String getCmt();

  /**
   * Sets cmt.
   *
   * @param value the value
   */
  void setCmt(String value);

  /**
   * Gets desc.
   *
   * @return the desc
   */
  String getDesc();

  /**
   * Sets desc.
   *
   * @param value the value
   */
  void setDesc(String value);

  /**
   * Gets src.
   *
   * @return the src
   */
  String getSrc();

  /**
   * Sets src.
   *
   * @param value the value
   */
  void setSrc(String value);

  /**
   * Gets links.
   *
   * @return the links
   */
  List<LinkType> getLinks();

  /**
   * Gets extensions.
   *
   * @return the extensions
   */
  ExtensionsType getExtensions();

  /**
   * Sets extensions.
   *
   * @param value the value
   */
  void setExtensions(ExtensionsType value);

}
