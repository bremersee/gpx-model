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

import org.bremersee.gpx.model.ObjectFactory;

/**
 * XML context helper.
 *
 * @author Christian Bremer
 */
public abstract class GpxJaxbContextHelper {

  @SuppressWarnings("WeakerAccess")
  public static final String[] CONTEXT_PATHS = {
      ObjectFactory.class.getPackage().getName()
  };

  @SuppressWarnings("unused")
  public static String[] contextPathsBuilder(String[] otherContextPaths) {
    if (otherContextPaths == null || otherContextPaths.length == 0) {
      return CONTEXT_PATHS;
    }
    final String[] newContextPaths = new String[CONTEXT_PATHS.length + otherContextPaths.length];
    System.arraycopy(
        CONTEXT_PATHS,
        0,
        newContextPaths,
        0,
        CONTEXT_PATHS.length);
    System.arraycopy(
        otherContextPaths,
        0,
        newContextPaths,
        CONTEXT_PATHS.length,
        otherContextPaths.length);
    return newContextPaths;
  }

  @SuppressWarnings("WeakerAccess")
  public static String contextPaths() {
    return contextPaths(CONTEXT_PATHS);
  }

  @SuppressWarnings("WeakerAccess")
  public static String contextPaths(final String[] contextPaths) {
    final StringBuilder sb = new StringBuilder();
    if (contextPaths != null) {
      for (int i = 0; i < contextPaths.length; i++) {
        sb.append(contextPaths[i]);
        if (i < contextPaths.length - 1) {
          sb.append(':');
        }
      }
    }
    return sb.toString();
  }

}
